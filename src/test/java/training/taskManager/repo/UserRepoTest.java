package training.taskManager.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import training.taskManager.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldReturnUserByEmail() {
        // given
        String email = "ivan@gmail.com";
        User user = new User("ivan", email, "avatar");

        underTest.save(user);

        // when
        User expected = underTest.findUserByEmail(email).get();

        // then
        assertThat(expected.getEmail()).isEqualTo(email);
    }

    @Test
    void itShouldNotReturnUserByEmail() {
        // given
        String email = "ivan@gmail.com";

        // when
        User expected = underTest.findUserByEmail(email).orElse(null);

        // then
        assertThat(expected).isNull();
    }
}