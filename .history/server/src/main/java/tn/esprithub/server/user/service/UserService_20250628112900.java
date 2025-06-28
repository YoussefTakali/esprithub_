package tn.esprithub.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprithub.server.common.enums.AuthProvider;
import tn.esprithub.server.common.enums.UserRole;
import tn.esprithub.server.user.dto.CreateUserRequest;
import tn.esprithub.server.user.dto.UpdateUserRequest;
import tn.esprithub.server.user.dto.UserDto;
import tn.esprithub.server.user.entity.User;
import tn.esprithub.server.user.repository.UserRepository;
import tn.esprithub.server.utils.mapper.UserMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersWithFilters(String email, UserRole role, Boolean enabled, Pageable pageable) {
        return userRepository.findUsersWithFilters(email, role, enabled, pageable)
                .map(userMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }
    
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        validateEspritEmail(request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .emailVerified(true) // Auto-verify for esprit.tn emails
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Created new user with email: {}", savedUser.getEmail());
        
        return userMapper.toDto(savedUser);
    }
    
    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        
        User savedUser = userRepository.save(user);
        log.info("Updated user with id: {}", savedUser.getId());
        
        return userMapper.toDto(savedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }
    
    @Transactional
    public UserDto updateGithubToken(String email, String githubToken, String githubUsername) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        user.setGithubToken(githubToken);
        user.setGithubUsername(githubUsername);
        
        User savedUser = userRepository.save(user);
        log.info("Updated GitHub token for user: {}", savedUser.getEmail());
        
        return userMapper.toDto(savedUser);
    }
    
    private void validateEspritEmail(String email) {
        if (!email.endsWith("@esprit.tn")) {
            throw new IllegalArgumentException("Email must be from esprit.tn domain");
        }
    }
    
    public boolean hasGithubToken(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getGithubToken() != null && !user.getGithubToken().isEmpty())
                .orElse(false);
    }
}
