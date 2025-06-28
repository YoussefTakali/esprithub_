package tn.esprithub.server.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalUsers;
    private long totalAdmins;
    private long totalChiefs;
    private long totalTeachers;
    private long totalStudents;
    private long usersWithGithubToken;
    private double githubIntegrationRate;
}
