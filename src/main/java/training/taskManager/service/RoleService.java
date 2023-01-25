package training.taskManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import training.taskManager.dto.RoleDto;
import training.taskManager.exceptions.InvalidUserException;
import training.taskManager.exceptions.RoleNotFoundException;
import training.taskManager.exceptions.UserNotFoundException;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.repo.RoleRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;
    private final UserService userService;
    private final WorkspaceService workspaceService;

    public Role findRole(Long userId, Long workspaceId) {
        return roleRepo.findRoleByIds(userId, workspaceId)
                .orElseThrow(() -> new RoleNotFoundException("Пользователь не был найден в рабочем пространстве с id " + workspaceId));
    }

    public Role addRole(RoleDto roleDto) {
        User user = userService.findUserByEmail(roleDto.getEmail());
        if (user == null) {
            throw new UserNotFoundException("Пользователь с email " + roleDto.getEmail() + " не был найден");
        }

        Workspace workspace = workspaceService.getWorkspaceById(roleDto.getWorkspaceId());
        if (user.getWorkspaces().contains(workspace)) {
            throw new InvalidUserException("Пользователь с почтой " + roleDto.getEmail() + " уже в этом рабочем пространстве");
        }
        workspace = workspaceService.addUserInWorkspace(user, workspace);

        return roleRepo.save(new Role(roleDto.getRole(), user, workspace));
    }

    public String deleteRole(Long roleId) {
        roleRepo.deleteById(roleId);

        return "Роль успешно удалена";
    }
}
