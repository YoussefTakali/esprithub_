package tn.esprithub.server.auth.service;

import tn.esprithub.server.auth.dto.AuthResponse;
import tn.esprithub.server.auth.dto.LoginRequest;

public interface IAuthService {
    
    AuthResponse authenticateUser(LoginRequest loginRequest);
    
    AuthResponse refreshToken(String refreshToken);
}
