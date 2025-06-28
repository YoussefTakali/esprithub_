package tn.esprithub.server.github.service;

import tn.esprithub.server.github.dto.GitHubUserInfo;

public interface IGitHubService {
    
    GitHubUserInfo getUserInfo(String accessToken);
    
    boolean validateGitHubToken(String accessToken);
    
    void linkGitHubAccount(String userEmail, String githubToken);
}
