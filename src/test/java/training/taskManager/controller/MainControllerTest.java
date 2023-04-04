package training.taskManager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import training.taskManager.enumeration.Roles;
import training.taskManager.enumeration.Status;
import training.taskManager.model.Role;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.service.RoleService;
import training.taskManager.service.TaskService;
import training.taskManager.service.UserService;
import training.taskManager.service.WorkspaceService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MainController.class)
@WithMockUser
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private WorkspaceService workspaceService;
    @MockBean
    private RoleService roleService;
    @MockBean
    private TaskService taskService;

    @Test
    void itShouldReturnWelcomePage() throws Exception {
        mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void itShouldReturnWorkspacePage() throws Exception {
        Long id = 1L;
        User user = new User("name", "email", "avatar");
        Task task = Task.builder()
                .id(1L)
                .name("write main controller test")
                .status(Status.GOING)
                .user(user)
                .build();
        Workspace workspace = new Workspace();
        Role role = new Role(Roles.OWNER, user, workspace);
        workspace.setRoles(List.of(role));
        when(userService.getCurrentUser()).thenReturn(user);
        when(workspaceService.getWorkspaceById(id)).thenReturn(workspace);
        List<Task> tasks = List.of(task);
        when(taskService.getTasks(any(), any())).thenReturn(tasks);
        when(roleService.findRole(any(), any())).thenReturn(role);

        ResultActions resultActions = mockMvc.perform(get("/workspace/" + id));

        Map<String, Object> result = resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("workspace/index"))
                .andReturn().getModelAndView()
                .getModel();
        verify(userService).getCurrentUser();
        verify(workspaceService).getWorkspaceById(id);
        verify(taskService).getTasks(any(), any());
        verify(roleService).findRole(any(), any());
        assertThat(result).as("Contains right model keys").containsKeys("tasks", "user", "userRole", "statuses", "roles", "selectRoles");
        assertThat(result).as("Contains right model objects").containsValues(role, user, workspace.getRoles(), tasks);
    }

    @Test
    void itShouldReturnGreetingPage() throws Exception {
        User user = new User("name", "email", "avatar");
        Workspace workspace = new Workspace();
        user.addWorkspace(workspace);
        Role role = new Role(Roles.OWNER, user, workspace);
        when(userService.getCurrentUser()).thenReturn(user);
        when(roleService.findRole(any(), any())).thenReturn(role);

        mockMvc.perform(get("/start")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("start"));
    }
}