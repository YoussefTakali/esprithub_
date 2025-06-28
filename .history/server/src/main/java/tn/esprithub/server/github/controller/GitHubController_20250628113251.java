package tn.esprithub.server.github.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.esprithub.server.common.dto.ApiResponse;
import tn.esprithub.server.github.dto.GitHubLinkRequest;
import tn.esprithub.server.github.dto.GitHubUserInfo;
import tn.esprithub.server.github.service.IGitHubService;
import tn.esprithub.server.security.UserPrincipal;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubController {
    
    private final IGitHubService gitHubService;
    
    @PostMapping("/link")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> linkGitHubAccount(
            @Valid @RequestBody GitHubLinkRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            gitHubService.linkGitHubAccount(userPrincipal.getEmail(), request.getGithubToken());
            return ResponseEntity.ok(ApiResponse.success("GitHub account linked successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/validate-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@Valid @RequestBody GitHubLinkRequest request) {
        try {
            boolean isValid = gitHubService.validateGitHubToken(request.getGithubToken());
            return ResponseEntity.ok(ApiResponse.success("Token validation completed", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Token validation completed", false));
        }
    }
    
    @GetMapping("/user-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GitHubUserInfo>> getUserInfo(@RequestParam String token) {
        try {
            GitHubUserInfo userInfo = gitHubService.getUserInfo(token);
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
