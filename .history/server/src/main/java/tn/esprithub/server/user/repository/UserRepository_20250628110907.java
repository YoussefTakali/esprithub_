package tn.esprithub.server.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprithub.server.common.enums.UserRole;
import tn.esprithub.server.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderId(String providerId);
    
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:enabled IS NULL OR u.enabled = :enabled)")
    Page<User> findUsersWithFilters(@Param("email") String email,
                                   @Param("role") UserRole role,
                                   @Param("enabled") Boolean enabled,
                                   Pageable pageable);
    
    boolean existsByEmail(String email);
    
    long countByRole(UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.githubToken IS NOT NULL")
    long countUsersWithGithubToken();
}
