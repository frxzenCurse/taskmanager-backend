package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final RoleService roleService;
    private final TaskService taskService;

    @GetMapping("/")
    public String getWelcomePage() {
        return "index";
    }

    @GetMapping("/workspace/{workspaceId}")
    public String getWorkspacePage(@PathVariable Long workspaceId, Model model) {
        User user = userService.getCurrentUser();
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        List<Task> tasks = taskService.getTasks(workspaceId, 0);
        Role role = roleService.findRole(user.getId(), workspace.getId());

        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        model.addAttribute("userRole", role);
        model.addAttribute("roles", workspace.getRoles());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("selectRoles", Roles.values());
        model.addAttribute("workspaceName", workspace.getName());

        return "workspace/index";
    }

    @GetMapping("/start")
    public String getStartPage(Model model) {
        User user = userService.getCurrentUser();
        List<Workspace> ownWorkspaces = new ArrayList<>();
        List<Workspace> invitedWorkspaces = new ArrayList<>();

        user.getWorkspaces().forEach(item -> {
            Role role = roleService.findRole(user.getId(), item.getId());

            if (role.getRole().equals(Roles.OWNER)) {
                ownWorkspaces.add(item);
            } else {
                invitedWorkspaces.add(item);
            }
        });

        model.addAttribute("ownWorkspaces", ownWorkspaces);
        model.addAttribute("invitedWorkspaces", invitedWorkspaces);
        model.addAttribute("user", user);

        return "start";
    }
}
