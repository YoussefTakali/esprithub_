package tn.esprithub.server.auth.service;

import org.springframework.security.core.Authentication;

public interface IJwtService {
    
    String generateAccessToken(Authentication authentication);
    
    String generateRefreshToken(Long userId, String email);
    
    Long getUserIdFromToken(String token);
    
    String getEmailFromToken(String token);
    
    boolean validateToken(String authToken);
}
