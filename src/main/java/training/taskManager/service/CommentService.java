package training.taskManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import training.taskManager.dto.CommentDto;
import training.taskManager.exceptions.TaskNotFoundException;
import training.taskManager.model.Comment;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.repo.CommentRepo;
import training.taskManager.repo.TaskRepo;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final TaskRepo taskRepo;
    private final UserService userService;

    public Comment saveComment(CommentDto commentDto) {
        Task task = taskRepo.findById(commentDto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + commentDto.getTaskId() + " not found"));
        User user = userService.getCurrentUser();

        Comment comment =  new Comment(
                commentDto.getId(),
                commentDto.getBody(),
                task,
                user
        );

        return commentRepo.save(comment);
    }

    public String deleteComment(Long commentId) {
        commentRepo.deleteById(commentId);
        return "Комментарий был удален";
    }
}
