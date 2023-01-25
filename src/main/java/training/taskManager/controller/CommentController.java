package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import training.taskManager.dto.CommentDto;
import training.taskManager.model.Comment;
import training.taskManager.service.CommentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ModelAndView saveComment(@RequestBody CommentDto commentDto) {
        Comment comment = commentService.saveComment(commentDto);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("comment", comment);

        return new ModelAndView("partial/comment", modelMap);
    }

    @PostMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
