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
import ru.pashabezborod.bi_test.model.dto.run.FinishRunRs;
import ru.pashabezborod.bi_test.model.dto.run.RunStatisticRs;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRs;
import ru.pashabezborod.bi_test.model.dto.run.UserRunRs;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RunControllerContractTest extends TestCommon {

    static {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    }

    private static final String START_RUN = """
            {
              "user": "%s",
              "startLatitude": %f,
              "startLongitude": %f,
              "startDateTime": "%s"
            }
            """;
    private static final String FINISH_RUN = """
            {
              "user": "%s",
              "finishLatitude": %f,
              "finishLongitude": %f,
              "finishDateTime": "%s",
              "distance": %d
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

    @BeforeEach
    void setUp() {
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        runRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Start run")
    void startRun() throws Exception {
        var json = mockMvc.perform(post("/run/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(START_RUN, user.getId(), 0.0d, 0.0d, "2000-01-01T00:00:00")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var response = objectMapper.readValue(json, StartRunRs.class);

        assertNotNull(response.id());
        assertEquals(0.0d, response.startLatitude());
        assertEquals(0.0d, response.startLongitude());
        assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), response.startDatetime());
    }

    @Test
    @DisplayName("Finish run")
    void finishRun() throws Exception {
        startRun();
        var json = mockMvc.perform(post("/run/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(FINISH_RUN, user.getId(), 0.0d, 0.0d, "2000-01-01T01:00:00", 1000)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var response = objectMapper.readValue(json, FinishRunRs.class);

        assertNotNull(response.id());
        assertEquals(0.0d, response.finishLatitude());
        assertEquals(0.0d, response.finishLongitude());
        assertEquals(LocalDateTime.of(2000, 1,1, 1, 0), response.finishDatetime());
        assertEquals(1000, response.distance());
    }

    @Test
    @DisplayName("Get user statistic")
    void getUserStatistic() throws Exception {
        finishRun();

        var json =mockMvc.perform(get("/user/" + user.getId().toString() + "/statistic?from=1999-01-01T00:00:00&to=1999-01-01T01:00:00"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var response = objectMapper.readValue(json, RunStatisticRs.class);
        assertEquals(0d, response.averageSpeed());
        assertEquals(0, response.numberOfRuns());
        assertEquals(0, response.totalMeters());


        json = mockMvc.perform(get("/user/" + user.getId().toString() + "/statistic?from=2000-01-01T00:00:00&to=2001-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        response = objectMapper.readValue(json, RunStatisticRs.class);

        assertEquals(1d, response.averageSpeed());
        assertEquals(1, response.numberOfRuns());
        assertEquals(1000, response.totalMeters());
    }

    @Test
    @DisplayName("Get user runs")
    void getUserRuns() throws Exception {
        finishRun();

        var json = mockMvc.perform(get("/user/" + user.getId().toString() + "/run?from=1999-01-01T00:00:00&to=1999-01-01T01:00:00"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var response = objectMapper.readValue(json, new TypeReference<List<UserRunRs>>() {});
        assertTrue(response.isEmpty());

        json = mockMvc.perform(get("/user/" + user.getId().toString() + "/run?from=2000-01-01T00:00:00&to=2001-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        response = objectMapper.readValue(json, new TypeReference<>() {});

        assertEquals(1, response.size());
        assertEquals(1d, response.getFirst().getAverageSpeed());
        assertNotNull(response.getFirst().getId());
        assertNotNull(response.getFirst().getStartLatitude());
        assertNotNull(response.getFirst().getStartLongitude());
        assertNotNull(response.getFirst().getStartDatetime());
        assertNotNull(response.getFirst().getFinishLatitude());
        assertNotNull(response.getFirst().getFinishLongitude());
        assertNotNull(response.getFirst().getFinishDatetime());
        assertNotNull(response.getFirst().getDistance());
    }
}
