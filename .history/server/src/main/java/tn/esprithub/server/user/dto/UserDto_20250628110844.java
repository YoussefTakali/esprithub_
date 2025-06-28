package tn.esprithub.server.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprithub.server.common.enums.UserRole;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String githubUsername;
    private String profilePicture;
    private Boolean enabled;
    private Boolean emailVerified;
    private Boolean hasGithubToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
