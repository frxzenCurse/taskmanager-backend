package training.taskManager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import training.taskManager.model.FilesInfo;

@Repository
public interface FilesInfoRepo extends JpaRepository<FilesInfo, Long> {
}
