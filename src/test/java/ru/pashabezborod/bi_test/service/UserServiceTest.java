package ru.pashabezborod.bi_test.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.pashabezborod.bi_test.TestCommon;
import ru.pashabezborod.bi_test.exception.UserNotFoundException;
import ru.pashabezborod.bi_test.exception.UuidNotValidException;
import ru.pashabezborod.bi_test.model.Sex;
import ru.pashabezborod.bi_test.model.dto.user.UserCreateEditRq;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("UserService")
public class UserServiceTest extends TestCommon {

    static {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Get exists user")
    void getUser() throws UserNotFoundException {
        var currentUser = userService.get(user.getId());

        assertNotNull(user);
        assertEquals(user.getFirstName(), currentUser.getFirstName());
        assertEquals(user.getLastName(), currentUser.getLastName());
        assertEquals(user.getId(), currentUser.getId());
        assertEquals(user.getBirthDate(), currentUser.getBirthDate());
        assertSame(user.getSex(), currentUser.getSex());
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() {
        assertEquals(1, userService.getAll().size());
        createUser();
        assertEquals(2, userService.getAll().size());
        userRepository.deleteAll();
        assertEquals(0, userService.getAll().size());
    }

    @Test
    @DisplayName("Get user with wrong inputs")
    void getUserNotExists() {
        assertThrows(UserNotFoundException.class, () -> userService.get(UUID.randomUUID()));
        assertThrows(ConstraintViolationException.class, () -> userService.get(null));
    }

    @Test
    @DisplayName("Create user")
    void createUser() {
        var rq = new UserCreateEditRq(
                Sex.OTHER,
                "TEST",
                "TEST",
                LocalDate.now());
        var createdUser = userService.create(rq);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(rq.firstName(), createdUser.getFirstName());
        assertEquals(rq.lastName(), createdUser.getLastName());
        assertEquals(rq.birthDate(), createdUser.getBirthDate());
        assertSame(rq.sex(), createdUser.getSex());
    }

    @Test
    @DisplayName("Create user with null sex")
    void createUserWithWrongInputs() {
        assertThrows(ConstraintViolationException.class, () -> userService.create(new UserCreateEditRq(
                null, "TEST", "TEST", LocalDate.now())));
    }

    @Test
    @DisplayName("Create user with wrong birthDate")
    void createUserWithWrongBirthDate() {
        assertThrows(ConstraintViolationException.class, () -> userService.create(new UserCreateEditRq(
                Sex.MALE, "TEST", "TEST", null)));
        assertThrows(ConstraintViolationException.class, () -> userService.create(new UserCreateEditRq(
                Sex.MALE, "TEST", "TEST", LocalDate.now().plusDays(1L))));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n", ".", "/", "1", "abc123", "&", "$", "\f"})
    @DisplayName("Create user with wrong first/last name")
    void createUserWithWrongFirstAndLastName(String name) {
        assertThrows(ConstraintViolationException.class, () -> userService.create(new UserCreateEditRq(
                Sex.FEMALE, name, "TEST", LocalDate.now())));
        assertThrows(ConstraintViolationException.class, () -> userService.create(new UserCreateEditRq(
                Sex.FEMALE, "TEST", name, LocalDate.now())));
    }

    @Test
    @DisplayName("Edit user")
    void editUser() throws UserNotFoundException, UuidNotValidException {
        var rq = new UserCreateEditRq(Sex.MALE, "hi", "there", LocalDate.now().minusDays(1));
        var currentUser = userService.edit(user.getId(), rq);

        assertNotNull(currentUser);
        assertEquals(rq.firstName(), currentUser.getFirstName());
        assertEquals(rq.lastName(), currentUser.getLastName());
        assertEquals(rq.birthDate(), currentUser.getBirthDate());
        assertSame(rq.sex(), currentUser.getSex());

        rq = new UserCreateEditRq(Sex.OTHER, null, null, null);
        currentUser = userService.edit(user.getId(), rq);
        assertSame(rq.sex(), currentUser.getSex());

        rq = new UserCreateEditRq(null, "how", null, null);
        currentUser = userService.edit(user.getId(), rq);
        assertEquals(rq.firstName(), currentUser.getFirstName());

        rq = new UserCreateEditRq(null, null, "areYou", null);
        currentUser = userService.edit(user.getId(), rq);
        assertEquals(rq.lastName(), currentUser.getLastName());

        rq = new UserCreateEditRq(null, null, null, LocalDate.of(2000, 1, 1));
        currentUser = userService.edit(user.getId(), rq);
        assertEquals(rq.birthDate(), currentUser.getBirthDate());
    }

    @Test
    @DisplayName("Edit user with wrong UUID")
    void editUserWithWrongUUID() {
        var rq = new UserCreateEditRq(Sex.MALE, "hi", "there", LocalDate.now().minusDays(1));
        assertThrows(ConstraintViolationException.class, () -> userService.edit(null, rq));
        assertThrows(UserNotFoundException.class, () -> userService.edit(UUID.randomUUID(), rq));
    }

    @Test
    @DisplayName("Update user with wrong birthDate")
    void updateUserWithWrongBirthDate() {
        assertThrows(ConstraintViolationException.class, () -> userService.edit(user.getId(),
                new UserCreateEditRq(Sex.MALE, "TEST", "TEST", LocalDate.now().plusDays(1L))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n", ".", "/", "1", "abc123", "&", "$", "\f"})
    @DisplayName("Edit user with wrong first/last name")
    void editUserWithWrongFirstAndLastName(String name) {
        assertThrows(ConstraintViolationException.class, () -> userService.edit(user.getId(),
                new UserCreateEditRq(Sex.FEMALE, name, "TEST", LocalDate.now())));
        assertThrows(ConstraintViolationException.class, () -> userService.edit(user.getId(),
                new UserCreateEditRq(Sex.FEMALE, "TEST", name, LocalDate.now())));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() throws UuidNotValidException {
        assertEquals(1L, userRepository.count());
        userService.deleteUser(user.getId());
    }

    @Test
    @DisplayName("Delete user with wrong UUID")
    void deleteUserWithWrongUUID() {
        assertThrows(ConstraintViolationException.class, () -> userService.deleteUser(null));
    }
}
