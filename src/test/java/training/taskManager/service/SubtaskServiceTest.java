package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import training.taskManager.dto.SubtaskDto;
import training.taskManager.exceptions.TaskNotFoundException;
import training.taskManager.model.Subtask;
import training.taskManager.model.Task;
import training.taskManager.repo.SubtaskRepo;
import training.taskManager.repo.TaskRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubtaskServiceTest {

    @Mock private SubtaskRepo subtaskRepo;
    @Mock private TaskRepo taskRepo;
    private SubtaskService underTest;

    @BeforeEach
    void setUp() {
        underTest = new SubtaskService(subtaskRepo, taskRepo);
    }

    @Test
    void canSaveSubtask() {
        Long id = 1L;
        SubtaskDto subtaskDto = new SubtaskDto("subtask", id);
        Task task = new Task();
        when(taskRepo.findById(id)).thenReturn(Optional.of(task));

        underTest.save(subtaskDto);

        ArgumentCaptor<Subtask> argumentCaptor = ArgumentCaptor.forClass(Subtask.class);
        verify(subtaskRepo).save(argumentCaptor.capture());
        Subtask capturedSubtask = argumentCaptor.getValue();
        assertThat(capturedSubtask.getName()).isEqualTo(subtaskDto.getName());
    }

    @Test
    void willThrowWhenTaskNotFound() {
        Long id = 1L;
        SubtaskDto subtaskDto = new SubtaskDto("subtask", id);
        when(taskRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.save(subtaskDto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Задача с id " + subtaskDto.getTaskId() + " не найдена");
    }

    @Test
    void canDeleteSubtask() {
        Long id = 1L;

        String expected = underTest.deleteSubtask(id);

        verify(subtaskRepo).deleteById(id);
        assertThat(expected).isEqualTo("Подзача была удалена");
    }
}