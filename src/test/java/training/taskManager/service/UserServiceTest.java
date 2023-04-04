package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import training.taskManager.model.User;
import training.taskManager.repo.RoleRepo;
import training.taskManager.repo.UserRepo;
import training.taskManager.repo.WorkspaceRepo;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private WorkspaceRepo workspaceRepo;
    @Mock private RoleRepo roleRepo;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepo, workspaceRepo, roleRepo);
    }

    @Test
    void canAddUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "nikita");
        map.put("email", "qwe");
        map.put("picture", "ssss");

        underTest.addUser(map);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepo).save(userArgumentCaptor.capture());
        verify(roleRepo).save(any());
        verify(workspaceRepo).save(any());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo(map.get("name"));
        assertThat(capturedUser.getEmail()).isEqualTo(map.get("email"));
        assertThat(capturedUser.getAvatar()).isEqualTo(map.get("picture"));
    }

    @Test
    void canFindByEmail() {
        underTest.findUserByEmail(anyString());

        verify(userRepo).findUserByEmail(anyString());
    }

    @Test
    @Disabled
    void getCurrentUser() {
    }
}