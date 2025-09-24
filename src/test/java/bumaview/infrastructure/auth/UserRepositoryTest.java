package bumaview.infrastructure.auth;

import bumaview.domain.auth.Role;
import bumaview.domain.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:mysql://localhost:3306/bumaview",
    "spring.datasource.username=bumaview_user", 
    "spring.datasource.password=userpassword",
    "spring.security.password.bcrypt-strength=12"
})
@Transactional
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회가 정상적으로 동작한다")
    void saveAndFindUser() {
        // given
        User user = new User("testuser", "테스트사용자", "password123", Role.USER);

        // when
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById("testuser").orElse(null);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo("testuser");
        assertThat(foundUser.getNickname()).isEqualTo("테스트사용자");
        assertThat(foundUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("existsById 메서드가 존재하는 사용자에 대해 true를 반환한다")
    void existsByIdReturnsTrueForExistingUser() {
        // given
        User user = new User("existinguser", "기존사용자", "password123", Role.USER);
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsById("existinguser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsById 메서드가 존재하지 않는 사용자에 대해 false를 반환한다")
    void existsByIdReturnsFalseForNonExistingUser() {
        // when
        boolean exists = userRepository.existsById("nonexistentuser");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자 삭제가 정상적으로 동작한다")
    void deleteUser() {
        // given
        User user = new User("deleteuser", "삭제할사용자", "password123", Role.USER);
        userRepository.save(user);

        // when
        userRepository.deleteById("deleteuser");
        boolean exists = userRepository.existsById("deleteuser");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("모든 사용자 조회가 정상적으로 동작한다")
    void findAllUsers() {
        // given
        userRepository.deleteAll(); // 기존 데이터 정리
        User user1 = new User("user1", "사용자1", "password123", Role.USER);
        User user2 = new User("user2", "사용자2", "password456", Role.USER);
        userRepository.save(user1);
        userRepository.save(user2);

        // when
        var users = userRepository.findAll();

        // then
        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
        assertThat(users).extracting(User::getId).contains("user1", "user2");
    }
}