package training.taskManager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import training.taskManager.dto.TaskDto;
import training.taskManager.exceptions.TaskNotFoundException;
import training.taskManager.model.*;
import training.taskManager.repo.SubtaskRepo;
import training.taskManager.repo.TaskRepo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepo taskRepo;
    private final SubtaskRepo subtaskRepo;
    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final FilesInfoService filesInfoService;

    public Task addTask(TaskDto taskDto) {
        User user = userService.getCurrentUser();
        Workspace workspace = workspaceService.getWorkspaceById(taskDto.getWorkspaceId());

        Task task = new Task(
                taskDto.getName(),
                taskDto.getStatus(),
                convertStringToDate(taskDto.getExpectedTime()),
                taskDto.getDescription(),
                user,
                workspace
        );
        task = taskRepo.save(task);

        if (!taskDto.getFiles().isEmpty()) {
            List<FilesInfo> files = filesInfoService.saveFiles(task, taskDto.getFiles());
            task.setFiles(files);
        }

        if (!taskDto.getSubtasks().isEmpty()) {
            Task finalTask = task;
            taskDto.getSubtasks().stream()
                    .map(item -> new Subtask(item, finalTask))
                    .forEach(subtaskRepo::save);
        }

        return task;
    }

    private LocalDate convertStringToDate(String stringDate) {
        if (stringDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return LocalDate.parse(stringDate, formatter);
    }

    public ResponseEntity<Task> updateTask(Task task) {
        Task oldTask = taskRepo.findById(task.getId())
                .orElseThrow(() -> new TaskNotFoundException("Нет задачи с id" + task.getId()));
        User user = oldTask.getUser();
        Workspace workspace = oldTask.getWorkspace();

        if (!task.getSubtasks().isEmpty()) {
            List<Subtask> subtasks = task.getSubtasks();

            subtasks.forEach(item -> item.setTask(task));
            subtasks = subtasks.stream()
                    .map(subtaskRepo::save)
                    .collect(Collectors.toList());
            task.setSubtasks(subtasks);
        }

        task.setUser(user);
        task.setWorkspace(workspace);
        task.setComments(oldTask.getComments());

        return ResponseEntity.ok(taskRepo.save(task));
    }

    public String deleteTask(Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с id " + taskId + " не была найдена"));

        if (!task.getFiles().isEmpty()) {
            task.getFiles().forEach(file -> filesInfoService.deleteFile(file.getId()));
        }

        taskRepo.deleteById(taskId);
        return "Задача удалена";
    }

    public List<Task> getTasks(Long workspaceId, Integer pageNum) {
        Pageable page = PageRequest.of(pageNum, 10);

        return taskRepo.findAllByWorkspaceId(workspaceId, page);
    }

    public List<Task> searchTasks(Long workspaceId, String query) {
        return taskRepo.searchTasks(workspaceId, query);
    }
}
