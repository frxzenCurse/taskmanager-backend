package training.taskManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import training.taskManager.model.Workspace;
import training.taskManager.service.UserService;
import training.taskManager.service.WorkspaceService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkspaceController.class)
@WithMockUser
class WorkspaceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private WorkspaceService workspaceService;
    @MockBean
    private UserService userService;


    @Test
    void itShouldAddWorkspace() throws Exception {
        Workspace request = Workspace.builder()
                .id(1L)
                .name("team workspace 20221")
                .build();
        when(workspaceService.addWorkspace(any())).thenReturn(request);

        ResultActions resultActions = mockMvc.perform(
                post("/workspace/add")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        String result = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(request.getName());
        verify(workspaceService).addWorkspace(any());
    }

    @Test
    void itShouldDeleteWorkspace() throws Exception {
        Long id = 1L;

        mockMvc
            .perform(post("/workspace/delete/" + id).with(csrf()))
            .andExpect(status().isOk());

        verify(workspaceService).deleteWorkspace(id);
    }

    @Test
    void itShouldDeleteUserFromWorkspace() throws Exception {
        Long id = 1L;

        mockMvc
                .perform(post("/workspace/exit/" + id).with(csrf()))
                .andExpect(status().isOk());

        verify(workspaceService).workspaceExit(id);
    }
}