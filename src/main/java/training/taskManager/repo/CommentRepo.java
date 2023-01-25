package training.taskManager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import training.taskManager.model.Comment;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
}
