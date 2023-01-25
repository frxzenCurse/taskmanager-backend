package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import training.taskManager.dto.TaskDto;
import training.taskManager.enumeration.Status;
import training.taskManager.model.Role;
import training.taskManager.model.Task;
import training.taskManager.model.User;
import training.taskManager.service.RoleService;
import training.taskManager.service.TaskService;
import training.taskManager.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final RoleService roleService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody ModelAndView addTask(@ModelAttribute TaskDto taskDto) {
        Task task = taskService.addTask(taskDto);
        User user = userService.getCurrentUser();
        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("task", task);
        modelMap.put("user", user);

        ModelAndView modelAndView = new ModelAndView("partial/task.html", modelMap);

        return modelAndView;
    }

    @PostMapping("/update")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        return taskService.updateTask(task);
    }

    @PostMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }

    @GetMapping("/")
    public ModelAndView getTasks(@RequestParam Long workspaceId, @RequestParam Integer page) {
        List<Task> tasks = taskService.getTasks(workspaceId, page);

        return getModel(tasks, workspaceId, "partial/tasks-page.html");
    }

    @GetMapping("/search")
    public ModelAndView searchTasks(@RequestParam Long workspaceId, @RequestParam String query) {
        List<Task> tasks = taskService.searchTasks(workspaceId, query);

        return getModel(tasks, workspaceId, "partial/searched-tasks.html");
    }

    private ModelAndView getModel(List<Task> tasks, Long workspaceId, String viewName) {
        User user = userService.getCurrentUser();
        Role role = roleService.findRole(user.getId(), workspaceId);
        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("tasks", tasks);
        modelMap.put("userRole", role);
        modelMap.put("user", user);
        modelMap.put("statuses", Status.values());

        ModelAndView modelAndView = new ModelAndView(viewName, modelMap);

        return modelAndView;
    }
}
