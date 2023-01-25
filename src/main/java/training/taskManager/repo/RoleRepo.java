package training.taskManager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import training.taskManager.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r " +
            "WHERE user_id = ?1 " +
            "AND workspace_id = ?2")
    Optional<Role> findRoleByIds(Long userId, Long workspaceId);
}
