package training.taskManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import training.taskManager.dto.SubtaskDto;
import training.taskManager.exceptions.TaskNotFoundException;
import training.taskManager.model.Subtask;
import training.taskManager.model.Task;
import training.taskManager.repo.SubtaskRepo;
import training.taskManager.repo.TaskRepo;

@Service
@RequiredArgsConstructor
public class SubtaskService {
    private final SubtaskRepo subtaskRepo;
    private final TaskRepo taskRepo;

    public Subtask save(SubtaskDto subtaskDto) {
        Task task = taskRepo.findById(subtaskDto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Задача с id " + subtaskDto.getTaskId() + " не найдена"));
        Subtask subtask = new Subtask(subtaskDto.getName(), task);
        return subtaskRepo.save(subtask);
    }

    public String deleteSubtask(Long subtaskId) {
        subtaskRepo.deleteById(subtaskId);
        return "Подзача была удалена";
    }
}
