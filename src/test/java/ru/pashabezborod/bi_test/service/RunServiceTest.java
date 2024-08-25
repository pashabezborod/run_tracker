package ru.pashabezborod.bi_test.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.pashabezborod.bi_test.TestCommon;
import ru.pashabezborod.bi_test.exception.MBadRequest;
import ru.pashabezborod.bi_test.exception.UserNotFoundException;
import ru.pashabezborod.bi_test.exception.UuidNotValidException;
import ru.pashabezborod.bi_test.exception.run.InvalidRunFinishTimeException;
import ru.pashabezborod.bi_test.exception.run.InvalidRunStartTimeException;
import ru.pashabezborod.bi_test.exception.run.LastRunNotFinishedException;
import ru.pashabezborod.bi_test.exception.run.NoActiveRunException;
import ru.pashabezborod.bi_test.model.db.Run;
import ru.pashabezborod.bi_test.model.dto.run.FinishRunRq;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRq;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Run service")
public class RunServiceTest extends TestCommon {

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
        runRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Start run")
    void startRun() throws MBadRequest {
        var rq = new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().minusDays(1));
        var run = runService.startRun(rq);

        assertNotNull(run);
        assertEquals(1, runRepository.count());
        assertEquals(run.getId(), runRepository.findAll().getFirst().getId());
        assertEquals(rq.startDateTime(), run.getStartDatetime());
        assertEquals(rq.startLatitude(), run.getStartLatitude());
        assertEquals(rq.startLongitude(), run.getStartLongitude());
        assertEquals(rq.user(), run.getUser().toString());

        run.setFinishDatetime(LocalDateTime.now().minusDays(1));
        runRepository.save(run);
        assertDoesNotThrow(() -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now())));

    }

    @Test
    @DisplayName("Start run with wrong user")
    void startRunWithWrongUser() {
        assertThrows(ConstraintViolationException.class, () -> runService.startRun(
                new StartRunRq(null, 0.0d, 0.0d, LocalDateTime.now())));
        assertThrows(UserNotFoundException.class, () -> runService.startRun(
                new StartRunRq(UUID.randomUUID().toString(), 0.0d, 0.0d, LocalDateTime.now())));
        assertThrows(UuidNotValidException.class, () -> runService.startRun(
                new StartRunRq("bad uuid", 0.0d, 0.0d, LocalDateTime.now())));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(doubles = {-91d, 91d, 100500d})
    @DisplayName("Start run with wrong start latitude")
    void startRunWithWrongStartLatitude(Double startLatitude) {
        assertThrows(ConstraintViolationException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), startLatitude, 0.0d, LocalDateTime.now())));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(doubles = {-181d, 181d, 100500d})
    @DisplayName("Start run with wrong start longitude")
    void startRunWithWrongStartLongitude(Double startLatitude) {
        assertThrows(ConstraintViolationException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, startLatitude, LocalDateTime.now())));
    }

    @Test
    @DisplayName("Start run with wrong start date")
    void startRunWithWrongStartDate() throws MBadRequest {
        assertThrows(ConstraintViolationException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, 0.0d, null)));
        assertThrows(ConstraintViolationException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().plusDays(1))));

        runService.startRun(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now()));
        assertThrows(LastRunNotFinishedException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now())));

        var run = runRepository.findAll().getFirst();
        run.setFinishDatetime(LocalDateTime.now().minusDays(5));
        runRepository.save(run);
        assertThrows(InvalidRunStartTimeException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().minusDays(1))));

        run.setStartDatetime(LocalDateTime.now().minusDays(5));
        run.setFinishDatetime(LocalDateTime.now());
        runRepository.save(run);
        assertThrows(InvalidRunStartTimeException.class, () -> runService.startRun(
                new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().minusDays(1))));
    }

    @Test
    @DisplayName("Finish run")
    void finishRun() throws MBadRequest {
        final int DISTANCE = 1568521;
        var rq = new FinishRunRq(user.getId().toString(), 10d, 10d, LocalDateTime.now().minusHours(2), null);
        runService.startRun(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().minusDays(1)));
        var run = runService.finishRun(rq);

        assertNotNull(run);
        assertEquals(rq.finishLatitude(), run.getFinishLatitude());
        assertEquals(rq.finishLongitude(), run.getFinishLongitude());
        assertEquals(rq.finishDateTime(), run.getFinishDatetime());
        assertEquals(DISTANCE, run.getDistance());

        rq = new FinishRunRq(user.getId().toString(), 10d, 10d, LocalDateTime.now(), 100);
        runService.startRun(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().minusHours(1)));
        run = runService.finishRun(rq);
        assertEquals(rq.distance(), run.getDistance());
    }

    @Test
    @DisplayName("Finish run with wrong user")
    void finishRunWithWrongUser() {
        assertThrows(ConstraintViolationException.class, () -> runService.finishRun(
                new FinishRunRq(null, 0.0d, 0.0d, LocalDateTime.now(), null)));
        assertThrows(UserNotFoundException.class, () -> runService.finishRun(
                new FinishRunRq(UUID.randomUUID().toString(), 0.0d, 0.0d, LocalDateTime.now(), null)));
        assertThrows(UuidNotValidException.class, () -> runService.finishRun(
                new FinishRunRq("bad uuid", 0.0d, 0.0d, LocalDateTime.now(), null)));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(doubles = {-91d, 91d, 100500d})
    @DisplayName("Finish run with wrong finish latitude")
    void finishRunWithWrongStartLatitude(Double startLatitude) {
        assertThrows(ConstraintViolationException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), startLatitude, 0.0d, LocalDateTime.now(), null)));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(doubles = {-181d, 181d, 100500d})
    @DisplayName("Finish run with wrong finish longitude")
    void finishRunWithWrongStartLongitude(Double startLatitude) {
        assertThrows(ConstraintViolationException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, startLatitude, LocalDateTime.now(), null)));
    }

    @Test
    @DisplayName("Finish run with wrong finish date")
    void finishRunWithWrongStartDate() throws MBadRequest {
        assertThrows(ConstraintViolationException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, null, null)));
        assertThrows(ConstraintViolationException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().plusDays(1), null)));
        assertThrows(NoActiveRunException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now(), null)));

        runService.startRun(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now()));
        runService.finishRun(new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now(), null));
        assertThrows(NoActiveRunException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now(), null)));

        runService.startRun(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now()));
        assertThrows(InvalidRunFinishTimeException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now().minusDays(1), null)));
    }

    @Test
    @DisplayName("Finish run with negative distance")
    void finishRunWithNegativeDistance() {
        assertThrows(ConstraintViolationException.class, () -> runService.finishRun(
                new FinishRunRq(user.getId().toString(), 0.0d, 0.0d, LocalDateTime.now(), -1)));
    }

    @Test
    @DisplayName("Get user runs")
    void getUserRuns() throws MBadRequest {
        LocalDateTime now = LocalDateTime.now();
        var run = new Run(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, now.minusHours(1)));
        run.setFinishLatitude(0.0d);
        run.setFinishLongitude(0.0d);
        run.setFinishDatetime(now);
        run.setDistance(100_000);
        runRepository.save(run);

        run = new Run(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, now.minusDays(1).minusHours(1)));
        run.setFinishLatitude(0.0d);
        run.setFinishLongitude(0.0d);
        run.setFinishDatetime(now.minusDays(1));
        run.setDistance(50_000);
        runRepository.save(run);

        var result = runService.getUserRuns(user.getId(), null, null);
        assertEquals(2, result.size());
        assertEquals(50d, result.get(0).getAverageSpeed());
        assertEquals(100d, result.get(1).getAverageSpeed());

        assertEquals(2, runService.getUserRuns(user.getId(), now.minusWeeks(1), null).size());
        assertEquals(2, runService.getUserRuns(user.getId(), null, now).size());
        assertEquals(1, runService.getUserRuns(user.getId(), now.minusHours(2), null).size());
        assertEquals(1, runService.getUserRuns(user.getId(), now.minusWeeks(1), now.minusHours(2)).size());
        assertEquals(2, runService.getUserRuns(user.getId(), now.minusWeeks(1), now).size());
    }

    @Test
    @DisplayName("Get user runs with wrong user id")
    void getUserRunsWithWrongUserId() {
        assertThrows(ConstraintViolationException.class, () -> runService.getUserRuns(null, null, null));
        assertThrows(UserNotFoundException.class, () -> runService.getUserRuns(UUID.randomUUID(), null, null));
    }

    @Test
    @DisplayName("Get user statistics")
    void getUserStatistics() throws MBadRequest {
        var statistic = runService.getUserStatistics(user.getId(), null, null);
        assertNotNull(statistic);
        assertEquals(0, statistic.numberOfRuns());
        assertEquals(0, statistic.totalMeters());
        assertEquals(0, statistic.averageSpeed());

        LocalDateTime now = LocalDateTime.now();
        var run = new Run(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, now.minusHours(1)));
        run.setFinishLatitude(0.0d);
        run.setFinishLongitude(0.0d);
        run.setFinishDatetime(now);
        run.setDistance(100_000);
        runRepository.save(run);
        run = new Run(new StartRunRq(user.getId().toString(), 0.0d, 0.0d, now.minusDays(1).minusHours(1)));
        run.setFinishLatitude(0.0d);
        run.setFinishLongitude(0.0d);
        run.setFinishDatetime(now.minusDays(1));
        run.setDistance(50_000);
        runRepository.save(run);

        statistic = runService.getUserStatistics(user.getId(), null, null);
        assertEquals(2, statistic.numberOfRuns());
        assertEquals(150_000, statistic.totalMeters());
        assertEquals((double) 150 / 2, statistic.averageSpeed());

        statistic = runService.getUserStatistics(user.getId(), now.minusMinutes(1), null);
        assertEquals(0, statistic.numberOfRuns());
        assertEquals(0, statistic.totalMeters());
        assertEquals(0, statistic.averageSpeed());

        statistic = runService.getUserStatistics(user.getId(), null, now.minusWeeks(1));
        assertEquals(0, statistic.numberOfRuns());
        assertEquals(0, statistic.totalMeters());
        assertEquals(0, statistic.averageSpeed());

        statistic = runService.getUserStatistics(user.getId(), now.minusWeeks(1), now.minusHours(2));
        assertEquals(1, statistic.numberOfRuns());
        assertEquals(50_000, statistic.totalMeters());
        assertEquals(50, statistic.averageSpeed());

        statistic = runService.getUserStatistics(user.getId(), now.minusHours(2), now);
        assertEquals(1, statistic.numberOfRuns());
        assertEquals(100_000, statistic.totalMeters());
        assertEquals(100, statistic.averageSpeed());

        statistic = runService.getUserStatistics(user.getId(), now.minusWeeks(1), now);
        assertEquals(2, statistic.numberOfRuns());
        assertEquals(150_000, statistic.totalMeters());
        assertEquals((double) 150 / 2, statistic.averageSpeed());
    }

    @Test
    @DisplayName("Get user statistics with wrong user id")
    void getUserStatisticsWithWrongUserId() {
        assertThrows(ConstraintViolationException.class, () -> runService.getUserStatistics(null, null, null));
        assertThrows(UserNotFoundException.class, () -> runService.getUserStatistics(UUID.randomUUID(), null, null));
    }
}
