package training.taskManager.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import training.taskManager.enumeration.Roles;
import training.taskManager.exceptions.InvalidUserException;
import training.taskManager.exceptions.UserNotFoundException;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.repo.RoleRepo;
import training.taskManager.repo.UserRepo;
import training.taskManager.repo.WorkspaceRepo;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final WorkspaceRepo workspaceRepo;
    private final RoleRepo roleRepo;

    public User addUser(Map<String, Object> data) {
        Workspace workspace = new Workspace();
        User user = new User(
                (String) data.get("name"),
                (String) data.get("email"),
                (String) data.get("picture")
        );
        user = userRepo.save(user);
        workspace.addMember(user);
        workspace = workspaceRepo.save(workspace);

        roleRepo.save(new Role(Roles.OWNER, user, workspace));

        return user;
    }

    public User findUserByEmail(String email) {

        return userRepo.findUserByEmail(email)
                .orElse(null);
    }

    public User getCurrentUser() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            User user = findUserByEmail(oidcUser.getAttribute("email"));

            return user;
        }

        throw new InvalidUserException("Invalid user");
    }
}
