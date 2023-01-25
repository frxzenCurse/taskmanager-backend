package training.taskManager.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import training.taskManager.model.Task;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findAllByWorkspaceId(Long workspaceId, Pageable pageable);

    @Query("SELECT t FROM Task t " +
            "WHERE workspace_id = ?1 " +
            "AND name LIKE CONCAT('%', ?2, '%')")
    List<Task> searchTasks(Long workspaceId, String query);
}
