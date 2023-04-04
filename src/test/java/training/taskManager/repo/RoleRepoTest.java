package training.taskManager.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import training.taskManager.enumeration.Roles;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepoTest {

    @Autowired
    private RoleRepo underTest;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WorkspaceRepo workspaceRepo;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        userRepo.deleteAll();
        workspaceRepo.deleteAll();
    }

    @Test
    void itShouldReturnRoleOptionalByUserAndWorkspaceIds() {
        User user = User.builder()
                .name("nikita")
                .email("email")
                .avatar("avatar")
                .build();
        user = userRepo.save(user);
        Workspace workspace = new Workspace();
        workspace.addMember(user);
        workspace = workspaceRepo.save(workspace);
        Role role = underTest.save(new Role(Roles.OWNER, user, workspace));

        Optional<Role> expected = underTest.findRoleByIds(user.getId(), workspace.getId());

        assertThat(expected).isEqualTo(Optional.of(role));
    }
    @Test
    void itShouldReturnEmptyOptionalByUserAndWorkspaceIds() {
        User user = User.builder()
                .name("nikita")
                .email("email")
                .avatar("avatar")
                .build();
        user = userRepo.save(user);
        Workspace workspace = new Workspace();
        workspace.addMember(user);
        workspace = workspaceRepo.save(workspace);

        Optional<Role> expected = underTest.findRoleByIds(user.getId(), workspace.getId());

        assertThat(expected).isEqualTo(Optional.empty());
    }
}