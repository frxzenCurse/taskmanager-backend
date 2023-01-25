package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import training.taskManager.dto.TaskDto;
import training.taskManager.enumeration.Status;
import training.taskManager.exceptions.TaskNotFoundException;
import training.taskManager.model.Subtask;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.repo.SubtaskRepo;
import training.taskManager.repo.TaskRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepo taskRepo;
    @Mock private SubtaskRepo subtaskRepo;
    @Mock private WorkspaceService workspaceService;
    @Mock private UserService userService;
    @Mock private FilesInfoService filesInfoService;

    private TaskService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TaskService(
                taskRepo,
                subtaskRepo,
                workspaceService,
                userService,
                filesInfoService
        );
    }

    @Test
    void canAddTask() {
        List<String> subtasks = new ArrayList<>(Arrays.asList("qwe", "asd"));
        TaskDto taskDto = TaskDto.builder()
                .name("task")
                .status(Status.GOING)
                .subtasks(subtasks)
                .workspaceId(1L)
                .files(new ArrayList<>())
                .build();
        when(userService.getCurrentUser()).thenReturn(new User("nikita", "qwe", "avatar"));
        Workspace workspace = new Workspace();
        workspace.setId(1L);
        when(workspaceService.getWorkspaceById(any())).thenReturn(workspace);

        underTest.addTask(taskDto);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        ArgumentCaptor<Subtask> subtaskArgumentCaptor = ArgumentCaptor.forClass(Subtask.class);

        verify(workspaceService).getWorkspaceById(any());
        verify(userService).getCurrentUser();
        verify(taskRepo).save(taskArgumentCaptor.capture());
        verify(subtaskRepo, times(2)).save(subtaskArgumentCaptor.capture());
        Task capturedTask = taskArgumentCaptor.getValue();
        List<Subtask> capturedSubtasks = subtaskArgumentCaptor.getAllValues();
        assertThat(capturedTask.getName()).isEqualTo(taskDto.getName());
        assertThat(capturedSubtasks.get(0).getName()).isEqualTo(subtasks.get(0));
        assertThat(capturedSubtasks.get(1).getName()).isEqualTo(subtasks.get(1));
    }

    @Test
    void canUpdateTask() {
        Task task = Task.builder()
                .name("task")
                .user(new User())
                .workspace(new Workspace())
                .subtasks(new ArrayList<>())
                .build();
        task.addSubtask(new Subtask("subtasks", task));
        when(taskRepo.findById(any())).thenReturn(Optional.of(task));

        underTest.updateTask(task);

        verify(taskRepo).findById(any());
        verify(taskRepo).save(any());
        verify(subtaskRepo).save(any());
    }

    @Test
    void willThrowWhenTaskIsNotFound() {
        Task task = Task.builder()
                        .id(1L)
                        .build();

        assertThatThrownBy(() -> underTest.updateTask(task))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Нет задачи с id" + task.getId());
    }

    @Test
    void canDeleteTask() {
        Long id = 1L;
        Task task = new Task();
        task.setFiles(new ArrayList<>());
        when(taskRepo.findById(id)).thenReturn(Optional.of(task));

        String expected = underTest.deleteTask(id);

        verify(taskRepo).deleteById(id);
        assertThat(expected).isEqualTo("Задача удалена");
    }

    @Test
    void canGetTasks() {
        underTest.getTasks(1L, 1);

        verify(taskRepo).findAllByWorkspaceId(any(), any());
    }

    @Test
    void canSearchTasks() {
        underTest.searchTasks(any(), any());

        verify(taskRepo).searchTasks(any(), any());
    }
}