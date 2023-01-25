package training.taskManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import training.taskManager.enumeration.Roles;
import training.taskManager.exceptions.RoleNotFoundException;
import training.taskManager.exceptions.WorkspaceNotFoundException;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.repo.RoleRepo;
import training.taskManager.repo.WorkspaceRepo;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepo workspaceRepo;
    private final UserService userService;
    private final RoleRepo roleRepo;

    public Workspace addWorkspace(Workspace workspace) {
        User user = userService.getCurrentUser();
        workspace = addUserInWorkspace(user, workspace);

        roleRepo.save(new Role(Roles.OWNER, user, workspace));

        return workspace;
    }

    public Workspace getWorkspaceById(Long workspaceId) {
        return workspaceRepo.getWorkspaceById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Рабочее пространство с id " + workspaceId + " не было найдено"));
    }

    public void deleteWorkspace(Long workspaceId) {
        workspaceRepo.deleteById(workspaceId);
    }

    public Workspace addUserInWorkspace(User user, Workspace workspace) {
        workspace.addMember(user);
        return workspaceRepo.save(workspace);
    }

    public void workspaceExit(Long workspaceId) {
        User user = userService.getCurrentUser();
        Workspace workspace = getWorkspaceById(workspaceId);
        Role role = roleRepo.findRoleByIds(user.getId(), workspace.getId())
                            .orElseThrow(() -> new RoleNotFoundException("Пользователь не был найден в рабочем пространстве с id " + workspace.getId()));

        workspace.getMembers().remove(user);
        workspace.getRoles().remove(role);
        workspaceRepo.save(workspace);
    }
}
