package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import training.taskManager.dto.CommentDto;
import training.taskManager.dto.TaskDto;
import training.taskManager.enumeration.Status;
import training.taskManager.model.Comment;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.repo.CommentRepo;
import training.taskManager.repo.TaskRepo;
import training.taskManager.repo.UserRepo;
import training.taskManager.repo.WorkspaceRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepo commentRepo;
    @Mock private TaskRepo taskRepo;
    @Mock private UserService userService;

    private CommentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CommentService(commentRepo, taskRepo, userService);
    }

    @Test
    void canSaveComment() {
        User user = new User();
        Task task = new Task();
        String body = "comment";
        CommentDto commentDto = new CommentDto(body, 1L);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(user);

        underTest.saveComment(commentDto);

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(taskRepo).findById(any());
        verify(userService).getCurrentUser();
        verify(commentRepo).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment.getBody()).isEqualTo(body);
    }

    @Test
    void canDeleteComment() {

        String expected = underTest.deleteComment(any());

        verify(commentRepo).deleteById(any());
        assertThat(expected).isEqualTo("Комментарий был удален");
    }
}