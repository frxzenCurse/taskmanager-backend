package training.taskManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import training.taskManager.dto.SubtaskDto;
import training.taskManager.model.Subtask;
import training.taskManager.model.Task;
import training.taskManager.service.SubtaskService;
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

@WebMvcTest(SubtaskController.class)
@WithMockUser
class SubtaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SubtaskService subtaskService;
    @MockBean
    private UserService userService;

    @Test
    void itShouldSaveSubtask() throws Exception {
        SubtaskDto request = new SubtaskDto("subtask2022", 1L);
        when(subtaskService.save(any())).thenReturn(new Subtask(request.getName(), new Task()));

        ResultActions resultActions = mockMvc.perform(
                post("/subtasks/add")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        verify(subtaskService).save(any());
        assertThat(result).contains(request.getName());
    }

    @Test
    void itShouldDeleteSubtask() throws Exception {
        String message = "Подзача была удалена";
        Long id = 1L;
        when(subtaskService.deleteSubtask(id)).thenReturn(message);

        ResultActions resultActions = mockMvc
                .perform(post("/subtasks/delete/" + id)
                        .with(csrf()));

        resultActions.andExpect(status().isOk())
                .andExpect(content().string(message));
        verify(subtaskService).deleteSubtask(id);
    }
}