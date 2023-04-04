package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import training.taskManager.exceptions.WorkspaceNotFoundException;
import training.taskManager.model.Role;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.repo.RoleRepo;
import training.taskManager.repo.WorkspaceRepo;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock private WorkspaceRepo workspaceRepo;
    @Mock private UserService userService;
    @Mock private RoleRepo roleRepo;
    private WorkspaceService underTest;

    @BeforeEach
    void setUp() {
        underTest = new WorkspaceService(workspaceRepo, userService, roleRepo);
    }

    @Test
    void canAddWorkspace() {
        User user = new User();
        Workspace workspace = new Workspace();
        when(userService.getCurrentUser()).thenReturn(user);

        underTest.addWorkspace(workspace);

        verify(userService).getCurrentUser();
        verify(roleRepo).save(any());
    }

    @Test
    void canGetWorkspaceById() {
        Long workspaceId = 1L;
        when(workspaceRepo.getWorkspaceById(workspaceId)).thenReturn(Optional.of(new Workspace()));

        underTest.getWorkspaceById(workspaceId);

        verify(workspaceRepo).getWorkspaceById(workspaceId);
    }

    @Test
    void willThrowWhenWorkspaceNotFound() {
        Long id = 1L;

        assertThatThrownBy(() -> underTest.getWorkspaceById(id))
                .isInstanceOf(WorkspaceNotFoundException.class)
                .hasMessageContaining("Рабочее пространство с id " + id + " не было найдено");
    }

    @Test
    void canDeleteWorkspace() {
        Long id = 1L;

        underTest.deleteWorkspace(id);

        verify(workspaceRepo).deleteById(id);
    }

    @Test
    void canAddUserInWorkspace() {
        User user = new User();
        Workspace workspace = new Workspace();

        underTest.addUserInWorkspace(user, workspace);

        ArgumentCaptor<Workspace> workspaceArgumentCaptor = ArgumentCaptor.forClass(Workspace.class);
        verify(workspaceRepo).save(workspaceArgumentCaptor.capture());
        Workspace capturedWorkspace = workspaceArgumentCaptor.getValue();
        assertThat(capturedWorkspace).isEqualTo(workspace);
    }

    @Test
    void canExitFromWorkspace() {
        Long workspaceId = 1L;
        Workspace workspace = new Workspace();
        workspace.setRoles(new ArrayList<>());
        User user = new User();
        when(userService.getCurrentUser()).thenReturn(user);
        when(workspaceRepo.getWorkspaceById(workspaceId)).thenReturn(Optional.of(workspace));
        when(roleRepo.findRoleByIds(any(), any())).thenReturn(Optional.of(new Role()));

        underTest.workspaceExit(workspaceId);

        ArgumentCaptor<Workspace> argumentCaptor = ArgumentCaptor.forClass(Workspace.class);

        verify(userService).getCurrentUser();
        verify(roleRepo).findRoleByIds(any(), any());
        verify(workspaceRepo).save(argumentCaptor.capture());
        Workspace capturedWorkspace = argumentCaptor.getValue();
        assertThat(capturedWorkspace).isEqualTo(workspace);
    }
}