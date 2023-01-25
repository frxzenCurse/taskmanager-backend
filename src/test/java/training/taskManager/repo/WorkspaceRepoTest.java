package training.taskManager.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import training.taskManager.model.Workspace;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WorkspaceRepoTest {

    @Autowired
    private WorkspaceRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldReturnWorkspaceOptionById() {
        Workspace workspace = underTest.save(new Workspace());

        Optional<Workspace> expected = underTest.getWorkspaceById(workspace.getId());

        assertThat(expected).isEqualTo(Optional.of(workspace));
    }
    @Test
    void itShouldReturnEmptyOptionById() {

        Optional<Workspace> expected = underTest.getWorkspaceById(1L);

        assertThat(expected).isEqualTo(Optional.empty());
    }
}