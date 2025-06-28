package tn.esprithub.server.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprithub.server.common.enums.UserRole;
import tn.esprithub.server.user.dto.CreateUserRequest;
import tn.esprithub.server.user.dto.UpdateUserRequest;
import tn.esprithub.server.user.dto.UserDto;

import java.util.Optional;

public interface IUserService {
    
    Page<UserDto> getAllUsers(Pageable pageable);
    
    Page<UserDto> getUsersWithFilters(String email, UserRole role, Boolean enabled, Pageable pageable);
    
    Optional<UserDto> getUserById(Long id);
    
    Optional<UserDto> getUserByEmail(String email);
    
    UserDto createUser(CreateUserRequest request);
    
    UserDto updateUser(Long id, UpdateUserRequest request);
    
    void deleteUser(Long id);
    
    UserDto updateGithubToken(String email, String githubToken, String githubUsername);
    
    boolean hasGithubToken(String email);
}
