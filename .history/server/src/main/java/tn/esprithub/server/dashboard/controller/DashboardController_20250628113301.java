package tn.esprithub.server.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprithub.server.common.dto.ApiResponse;
import tn.esprithub.server.dashboard.dto.DashboardStats;
import tn.esprithub.server.dashboard.service.IDashboardService;
import tn.esprithub.server.security.UserPrincipal;
import tn.esprithub.server.user.dto.UserDto;
import tn.esprithub.server.user.service.IUserService;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final UserService userService;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHIEF')")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
        DashboardStats stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDto>> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getUserByEmail(userPrincipal.getEmail())
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/welcome")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getWelcomeMessage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDto user = userService.getUserByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String message = generateWelcomeMessage(user);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    private String generateWelcomeMessage(UserDto user) {
        String roleBasedMessage = switch (user.getRole()) {
            case ADMIN -> "You have full administrative access to the EspritHub platform.";
            case CHIEF -> "Welcome to your department dashboard. Manage your teams and projects efficiently.";
            case TEACHER -> "Ready to inspire and educate? Access your courses and student management tools.";
            case STUDENT -> "Continue your learning journey with access to courses and collaboration tools.";
        };
        
        String githubStatus = Boolean.TRUE.equals(user.getHasGithubToken()) ? 
            "Your GitHub account is connected and ready to use." :
            "Don't forget to connect your GitHub account for the full experience.";
        
        return String.format("Welcome back, %s %s! %s %s", 
                user.getFirstName(), user.getLastName(), roleBasedMessage, githubStatus);
    }
}
