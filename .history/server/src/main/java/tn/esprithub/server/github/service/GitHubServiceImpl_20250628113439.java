package tn.esprithub.server.github.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprithub.server.github.dto.GitHubUserInfo;
import tn.esprithub.server.user.service.;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubServiceImpl implements IGitHubService {
    
    private final IUserService userService;
    
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;
    
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;
    
    public GitHubUserInfo getUserInfo(String accessToken) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();
            var githubUser = github.getMyself();
            
            return GitHubUserInfo.builder()
                    .id(githubUser.getId())
                    .login(githubUser.getLogin())
                    .name(githubUser.getName())
                    .email(githubUser.getEmail())
                    .avatarUrl(githubUser.getAvatarUrl())
                    .build();
                    
        } catch (IOException e) {
            log.error("Error fetching GitHub user info", e);
            throw new RuntimeException("Failed to fetch GitHub user information", e);
        }
    }
    
    public boolean validateGitHubToken(String accessToken) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();
            github.getMyself(); // This will throw an exception if the token is invalid
            return true;
        } catch (IOException e) {
            log.warn("Invalid GitHub token provided");
            return false;
        }
    }
    
    public void linkGitHubAccount(String userEmail, String githubToken) {
        if (!validateGitHubToken(githubToken)) {
            throw new IllegalArgumentException("Invalid GitHub token");
        }
        
        try {
            GitHubUserInfo githubUserInfo = getUserInfo(githubToken);
            userService.updateGithubToken(userEmail, githubToken, githubUserInfo.getLogin());
            log.info("Successfully linked GitHub account for user: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to link GitHub account for user: {}", userEmail, e);
            throw new RuntimeException("Failed to link GitHub account", e);
        }
    }
}
