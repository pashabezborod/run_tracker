package ru.pashabezborod.bi_test.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.pashabezborod.bi_test.TestCommon;
import ru.pashabezborod.bi_test.model.dto.user.UserRs;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.pashabezborod.bi_test.model.Sex.FEMALE;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerContractTest extends TestCommon {

    static {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    }

    private final String CREATE_UPDATE = """
            {
              "sex": "%s",
              "firstName": "%s",
              "lastName": "%s",
              "birthDate": "%s"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Get user")
    void getUser() throws Exception {
        createTestUser();
        var json = mockMvc.perform(get("/user/" + user.getId().toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var currentUser = objectMapper.readValue(json, UserRs.class);

        assertEquals(user.getSex(), currentUser.getSex());
        assertEquals(user.getFirstName(), currentUser.getFirstName());
        assertEquals(user.getLastName(), currentUser.getLastName());
        assertEquals(user.getBirthDate(), currentUser.getBirthDate());
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() throws Exception {
        createTestUser();
        createTestUser();
        createTestUser();
        var json = mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var users = objectMapper.readValue(json, new TypeReference<List<UserRs>>() {});

        assertEquals(3, users.size());
    }

    @Test
    @DisplayName("Create user")
    void createUser() throws Exception {
        var json = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(CREATE_UPDATE, FEMALE.name(), "name", "last", "2000-01-01")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var currentUser = objectMapper.readValue(json, UserRs.class);

        assertEquals(FEMALE, currentUser.getSex());
        assertEquals("name", currentUser.getFirstName());
        assertEquals("last", currentUser.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), currentUser.getBirthDate());
    }

    @Test
    @DisplayName("Edit user")
    void editUser() throws Exception {
        createTestUser();
        var json = mockMvc.perform(patch("/user/" + user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(CREATE_UPDATE, FEMALE.name(), "name", "last", "2000-01-01")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var currentUser = objectMapper.readValue(json, UserRs.class);

        assertEquals(FEMALE, currentUser.getSex());
        assertEquals("name", currentUser.getFirstName());
        assertEquals("last", currentUser.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), currentUser.getBirthDate());
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() throws Exception {
        createTestUser();
        assertFalse(userRepository.findAll().isEmpty());

        mockMvc.perform(delete("/user/" + user.getId().toString()))
                .andExpect(status().isNoContent());

        assertTrue(userRepository.findAll().isEmpty());
    }
}
