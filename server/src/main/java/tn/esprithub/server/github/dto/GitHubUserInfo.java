package tn.esprithub.server.github.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubUserInfo {
    private Long id;
    private String login;
    private String name;
    private String email;
    private String avatarUrl;
}
