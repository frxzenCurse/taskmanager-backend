package training.taskManager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import training.taskManager.model.Workspace;

import java.util.Optional;

@Repository
public interface WorkspaceRepo extends JpaRepository<Workspace, Long> {
    Optional<Workspace> getWorkspaceById(Long workspaceId);
}
