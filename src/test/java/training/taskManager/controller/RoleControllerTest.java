package training.taskManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import training.taskManager.dto.RoleDto;
import training.taskManager.enumeration.Roles;
import training.taskManager.exceptions.ExceptionController;
import training.taskManager.exceptions.UserNotFoundException;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.service.RoleService;
import training.taskManager.service.UserService;
import training.taskManager.service.WorkspaceService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@WithMockUser
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoleController roleController;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RoleService roleService;
    @MockBean
    private WorkspaceService workspaceService;
    @MockBean
    private UserService userService;

    @Test
    void itShouldSaveRole() throws Exception {
        RoleDto request = new RoleDto("task_manager@gmail.com", Roles.OWNER, 1L);
        when(roleService.addRole(any())).thenReturn(new Role(1L, request.getRole(), new User(), new Workspace()));

        ResultActions resultActions = mockMvc.perform(
                post("/role/add")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        verify(roleService).addRole(any());
        assertThat(result).contains(request.getRole().getValue());
    }

    @Test
    void itWillThrowUserNotFound() throws Exception {
        RoleDto request = new RoleDto("task_manager@gmail.com", Roles.OWNER, 1L);
        when(roleService.addRole(any())).thenThrow(new UserNotFoundException("Пользователь с почтой " + request.getEmail()));

        mockMvc
                .perform(post("/role/add")
                        .with(csrf()).contentType("application/json").content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(UserNotFoundException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).isEqualTo("Пользователь с почтой " + request.getEmail()));
    }

    @Test
    void itShouldDeleteRole() throws Exception {
        String message = "Роль успешно удалена";
        Long id = 1L;
        when(roleService.deleteRole(id)).thenReturn(message);

        ResultActions resultActions = mockMvc
                .perform(post("/role/delete/" + id)
                        .with(csrf()));

        String result = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).isEqualTo(message);
        verify(roleService).deleteRole(id);
    }
}