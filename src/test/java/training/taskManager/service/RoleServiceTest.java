package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import training.taskManager.dto.RoleDto;
import training.taskManager.enumeration.Roles;
import training.taskManager.exceptions.InvalidUserException;
import training.taskManager.exceptions.RoleNotFoundException;
import training.taskManager.exceptions.UserNotFoundException;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.repo.RoleRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock private RoleRepo roleRepo;
    @Mock private UserService userService;
    @Mock private WorkspaceService workspaceService;

    private RoleService underTest;

    @BeforeEach
    void setUp() {
        underTest = new RoleService(roleRepo, userService, workspaceService);
    }

    @Test
    void canFindRoleByWorkspaceAndUserIds() {
        Role role = new Role(Roles.OWNER, new User(), new Workspace());
        when(roleRepo.findRoleByIds(any(), any())).thenReturn(Optional.of(role));

        underTest.findRole(any(), any());

        verify(roleRepo).findRoleByIds(any(), any());
    }
    @Test
    void willThrowWhenRoleNotFound() {
        when(roleRepo.findRoleByIds(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findRole(any(), any()))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("Пользователь не был найден в рабочем пространстве с id ");
    }

    @Test
    void canAddRole() {
        RoleDto roleDto = new RoleDto("email", Roles.OWNER, 1L);
        User user = new User();
        Workspace workspace = new Workspace();

        when(userService.findUserByEmail(roleDto.getEmail())).thenReturn(user);
        when(workspaceService.getWorkspaceById(roleDto.getWorkspaceId())).thenReturn(workspace);

        underTest.addRole(roleDto);

        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);

        verify(userService).findUserByEmail(roleDto.getEmail());
        verify(workspaceService).getWorkspaceById(roleDto.getWorkspaceId());
        verify(roleRepo).save(roleArgumentCaptor.capture());

        Role capturedRole = roleArgumentCaptor.getValue();

        assertThat(capturedRole.getRole()).isEqualTo(roleDto.getRole());
    }

    @Test
    void willThrowWhenUserNotFound() {
        RoleDto roleDto = new RoleDto("email", Roles.OWNER, 1L);
        when(userService.findUserByEmail(roleDto.getEmail())).thenReturn(null);

        assertThatThrownBy(() -> underTest.addRole(roleDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с email " + roleDto.getEmail() + " не был найден");
    }
    @Test
    void willThrowWhenUserAlreadyInWorkspace() {
        RoleDto roleDto = new RoleDto("email", Roles.OWNER, 1L);
        User user = new User();
        Workspace workspace = new Workspace();
        user.addWorkspace(workspace);

        when(userService.findUserByEmail(roleDto.getEmail())).thenReturn(user);
        when(workspaceService.getWorkspaceById(roleDto.getWorkspaceId())).thenReturn(workspace);

        assertThatThrownBy(() -> underTest.addRole(roleDto))
                .isInstanceOf(InvalidUserException.class)
                .hasMessageContaining("Пользователь с почтой " + roleDto.getEmail() + " уже в этом рабочем пространстве");
    }

    @Test
    void canDeleteRole() {
        Long id = 1L;

        String expected = underTest.deleteRole(id);

        verify(roleRepo).deleteById(id);
        assertThat(expected).isEqualTo("Роль успешно удалена");
    }
}