package training.taskManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import training.taskManager.dto.TaskDto;
import training.taskManager.enumeration.Roles;
import training.taskManager.enumeration.Status;
import training.taskManager.model.Role;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.service.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@WithMockUser
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RoleService roleService;
    @MockBean
    private WorkspaceService workspaceService;
    @MockBean
    private UserService userService;
    @MockBean
    private FilesInfoService filesInfoService;
    @MockBean
    private TaskService taskService;

    @Test
    void itShouldAddTask() throws Exception {
        TaskDto request = TaskDto.builder()
                .name("task request")
                .status(Status.GOING)
                .description("it should add task")
                .build();
        User user = new User("name", "email", "avatar");
        Task task = Task.builder()
                .id(1L)
                .name(request.getName())
                .status(request.getStatus())
                .description(request.getDescription())
                .user(user)
                .build();
        when(taskService.addTask(any())).thenReturn(task);
        when(userService.getCurrentUser()).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(
                post("/tasks/add")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        String result = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(request.getName());
        assertThat(result).contains(request.getDescription());
        verify(taskService).addTask(any());
        verify(userService).getCurrentUser();
    }

    @Test
    void itShouldUpdateTask() throws Exception {
        Task request = Task.builder()
                .name("Task2022")
                .description("Task_description2022")
                .status(Status.GOING)
                .build();
        when(taskService.updateTask(any())).thenReturn(ResponseEntity.ok(request));

        ResultActions resultActions = mockMvc.perform(
                post("/tasks/update")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        String response = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        verify(taskService).updateTask(any());
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(request));
    }

    @Test
    void itShouldDeleteTaskById() throws Exception {
        String message = "Задача удалена";
        Long id = 1L;
        when(taskService.deleteTask(id)).thenReturn(message);

        ResultActions resultActions = mockMvc
                .perform(post("/tasks/delete/" + id)
                        .with(csrf()));

        resultActions.andExpect(status().isOk())
                .andExpect(content().string(message));
        verify(taskService).deleteTask(id);
    }

    @Test
    void itShouldReturnTasksByWorkspaceIdAndPageIndex() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("nikita")
                .email("email")
                .avatar("avatar")
                .build();
        Task task = Task.builder()
                .name("write tests")
                .status(Status.GOING)
                .user(user)
                .build();
        Task secondTask = Task.builder()
                .name("finish project")
                .status(Status.PLANNED)
                .user(user)
                .build();
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(secondTask);
        when(taskService.getTasks(any(), any())).thenReturn(tasks);
        when(userService.getCurrentUser()).thenReturn(user);
        when(roleService.findRole(any(), any())).thenReturn(new Role(Roles.OWNER, user, new Workspace()));

        ResultActions resultActions = mockMvc.perform(
                get("/tasks/")
                        .param("workspaceId", "1")
                        .param("page", "1")
        );

        String response = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<String> names = tasks.stream()
                .map(Task::getName)
                .collect(Collectors.toList());

        verify(taskService).getTasks(any(), any());
        assertThat(response).contains(names);
        assertThat(response).contains(user.getName());
        assertThat(response).contains(user.getAvatar());
    }

    @Test
    void itShouldSearchTasks() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("nikita")
                .email("email")
                .avatar("avatar")
                .build();
        Task task = Task.builder()
                .name("write tests")
                .status(Status.GOING)
                .user(user)
                .build();
        Task secondTask = Task.builder()
                .name("finish project")
                .status(Status.PLANNED)
                .user(user)
                .build();
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(secondTask);
        when(taskService.searchTasks(any(), any())).thenReturn(tasks);
        when(userService.getCurrentUser()).thenReturn(user);
        when(roleService.findRole(any(), any())).thenReturn(new Role(Roles.OWNER, user, new Workspace()));

        ResultActions resultActions = mockMvc.perform(
                get("/tasks/search")
                        .param("workspaceId", "1")
                        .param("query", "asd")
        );

        String response = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<String> names = tasks.stream()
                .map(Task::getName)
                .collect(Collectors.toList());

        verify(taskService).searchTasks(any(), any());
        assertThat(response).contains(names);
        assertThat(response).contains(user.getName());
        assertThat(response).contains(user.getAvatar());
    }
}