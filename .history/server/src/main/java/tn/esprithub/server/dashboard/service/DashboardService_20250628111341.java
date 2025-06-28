package tn.esprithub.server.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprithub.server.common.enums.UserRole;
import tn.esprithub.server.dashboard.dto.DashboardStats;
import tn.esprithub.server.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    
    public DashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
        long totalChiefs = userRepository.countByRole(UserRole.CHIEF);
        long totalTeachers = userRepository.countByRole(UserRole.TEACHER);
        long totalStudents = userRepository.countByRole(UserRole.STUDENT);
        long usersWithGithubToken = userRepository.countUsersWithGithubToken();
        
        double githubIntegrationRate = totalUsers > 0 ? 
            (double) usersWithGithubToken / totalUsers * 100 : 0.0;
        
        return DashboardStats.builder()
                .totalUsers(totalUsers)
                .totalAdmins(totalAdmins)
                .totalChiefs(totalChiefs)
                .totalTeachers(totalTeachers)
                .totalStudents(totalStudents)
                .usersWithGithubToken(usersWithGithubToken)
                .githubIntegrationRate(githubIntegrationRate)
                .build();
    }
}
