package training.taskManager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import training.taskManager.model.Subtask;

@Repository
public interface SubtaskRepo extends JpaRepository<Subtask, Long> {
}
