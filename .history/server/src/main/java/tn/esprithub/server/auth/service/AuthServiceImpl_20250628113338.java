package tn.esprithub.server.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprithub.server.auth.dto.AuthResponse;
import tn.esprithub.server.auth.dto.LoginRequest;
import tn.esprithub.server.security.UserPrincipal;
import tn.esprithub.server.user.dto.UserDto;
import tn.esprithub.server.user.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {
    
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final IUserService userService;
    
    @Transactional
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Validate email domain
            if (!loginRequest.getEmail().endsWith("@esprit.tn")) {
                throw new IllegalArgumentException("Email must be from esprit.tn domain");
            }
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(userPrincipal.getId(), userPrincipal.getEmail());
            
            UserDto userDto = userService.getUserByEmail(userPrincipal.getEmail())
                    .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
            
            boolean requiresGithubAuth = !userService.hasGithubToken(userPrincipal.getEmail());
            
            log.info("User {} authenticated successfully", userPrincipal.getEmail());
            
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userDto)
                    .requiresGithubAuth(requiresGithubAuth)
                    .build();
                    
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
    
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        try {
            if (!jwtService.validateToken(refreshToken)) {
                throw new IllegalArgumentException("Invalid refresh token");
            }
            
            Long userId = jwtService.getUserIdFromToken(refreshToken);
            String email = jwtService.getEmailFromToken(refreshToken);
            
            UserDto userDto = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            String newAccessToken = jwtService.generateRefreshToken(userId, email);
            String newRefreshToken = jwtService.generateRefreshToken(userId, email);
            
            boolean requiresGithubAuth = !userService.hasGithubToken(email);
            
            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .user(userDto)
                    .requiresGithubAuth(requiresGithubAuth)
                    .build();
                    
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }
}
