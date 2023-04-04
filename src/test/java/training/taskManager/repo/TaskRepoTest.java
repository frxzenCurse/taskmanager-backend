package training.taskManager.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import training.taskManager.enumeration.Status;
import training.taskManager.model.Task;
import training.taskManager.model.Workspace;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepoTest {

    @Autowired
    private TaskRepo underTest;
    @Autowired
    private WorkspaceRepo workspaceRepo;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        workspaceRepo.deleteAll();
    }

    @Test
    void itShouldReturnTasksListByWorkspaceId() {
        Workspace workspace = workspaceRepo.save(new Workspace());
        List<Task> tasks = new ArrayList<>();
        Task task = Task.builder()
                .name("task")
                .status(Status.GOING)
                .workspace(workspace)
                .build();
        tasks.add(task);
        underTest.save(task);

        Pageable page = PageRequest.of(0, 10);
        List<Task> expected = underTest.findAllByWorkspaceId(workspace.getId(), page);

        assertThat(expected).isEqualTo(tasks);
    }

    @Test
    void isShouldReturnEmptyListByWorkspaceId() {
        Workspace workspace = workspaceRepo.save(new Workspace());
        List<Task> tasks = new ArrayList<>();

        Pageable page = PageRequest.of(0, 10);
        List<Task> expected = underTest.findAllByWorkspaceId(workspace.getId(), page);

        assertThat(expected).isEqualTo(tasks);
    }

    @Test
    void itShouldReturnTasksListByQuery() {
        Workspace workspace = workspaceRepo.save(new Workspace());
        List<Task> tasks = new ArrayList<>();
        Task task1 = Task.builder()
                .name("task")
                .status(Status.GOING)
                .workspace(workspace)
                .build();
        Task task2 = Task.builder()
                .name("hhhhh")
                .status(Status.GOING)
                .workspace(workspace)
                .build();
        tasks.add(task1);

        underTest.save(task1);
        underTest.save(task2);

        List<Task> expected = underTest.searchTasks(workspace.getId(), "as");

        assertThat(expected).isEqualTo(tasks);
    }
    @Test
    void itShouldReturnEmptyListByQuery() {
        Workspace workspace = workspaceRepo.save(new Workspace());
        List<Task> tasks = new ArrayList<>();
        Task task2 = Task.builder()
                .name("hhhhh")
                .status(Status.GOING)
                .workspace(workspace)
                .build();

        underTest.save(task2);

        List<Task> expected = underTest.searchTasks(workspace.getId(), "as");

        assertThat(expected).isEqualTo(tasks);
    }
}
