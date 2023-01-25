package training.taskManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import training.taskManager.dto.CommentDto;
import training.taskManager.model.Comment;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.service.CommentService;
import training.taskManager.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommentController.class)
@WithMockUser
class CommentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @MockBean
    private UserService userService;

    @Test
    void itShouldSaveCommentAndReturnModel() throws Exception {
        CommentDto request = new CommentDto("task manager app 2022 forever", 1L);
        when(commentService.saveComment(any())).thenReturn(new Comment(1L, request.getBody(), new Task(), new User()));

        ResultActions resultActions = mockMvc.perform(
                post("/comments/add")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        verify(commentService).saveComment(any());
        assertThat(result).contains(request.getBody());
    }

    @Test
    void itShouldDeleteCommentAndReturnMessage() throws Exception {
        String message = "Комментарий был удален";
        Long id = 1L;
        when(commentService.deleteComment(id)).thenReturn(message);

        ResultActions resultActions = mockMvc
                .perform(post("/comments/delete/" + id)
                        .with(csrf()));

        String result = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).isEqualTo(message);
        verify(commentService).deleteComment(id);
    }
}