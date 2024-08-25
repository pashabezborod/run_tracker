package ru.pashabezborod.bi_test.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.pashabezborod.bi_test.exception.*;
import ru.pashabezborod.bi_test.exception.run.InvalidRunFinishTimeException;
import ru.pashabezborod.bi_test.exception.run.InvalidRunStartTimeException;
import ru.pashabezborod.bi_test.exception.run.LastRunNotFinishedException;
import ru.pashabezborod.bi_test.exception.run.NoActiveRunException;
import ru.pashabezborod.bi_test.model.db.Run;
import ru.pashabezborod.bi_test.model.db.User;
import ru.pashabezborod.bi_test.model.dto.run.FinishRunRq;
import ru.pashabezborod.bi_test.model.dto.run.RunStatisticRs;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRq;
import ru.pashabezborod.bi_test.model.dto.run.UserRunRs;
import ru.pashabezborod.bi_test.repository.RunRepository;
import ru.pashabezborod.bi_test.service.RunService;
import ru.pashabezborod.bi_test.service.UserService;
import ru.pashabezborod.bi_test.util.UuidHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class RunServiceImpl implements RunService {

    private final RunTransactionalService service;
    private final UserService userService;

    @Override
    public Run startRun(@Valid StartRunRq rq) throws MBadRequest {
        User user = userService.get(UuidHelper.get(rq.user()));
        checkCanStartRun(user.getId(), rq.startDateTime());
        Run result = service.save(new Run(rq));
        log.info("User {} started run {}", user.getId(), result.getId());
        return result;
    }

    @Override
    public Run finishRun(@Valid FinishRunRq rq) throws MBadRequest {
        User user = userService.get(UuidHelper.get(rq.user()));
        Run run = getLastRun(user.getId());
        checkCanFinishRun(rq.finishDateTime(), run);

        run.setFinishLatitude(rq.finishLatitude());
        run.setFinishLongitude(rq.finishLongitude());
        run.setFinishDatetime(rq.finishDateTime());
        run.setDistance(rq.distance() == null ? calculateDistance(run) : rq.distance());

        service.save(run);
        log.info("User {} finished run {}", user.getId(), run.getId());
        return run;
    }

    @Override
    public List<UserRunRs> getUserRuns(UUID userId, LocalDateTime from, LocalDateTime to) throws UserNotFoundException {
        userService.get(userId);
        return service.findAllForUserRuns(userId, from, to).stream()
                .map(it -> new UserRunRs(it, calculateAverageSpeed(it)))
                .sorted(Comparator.comparing(UserRunRs::getStartDatetime))
                .toList();
    }

    @Override
    public RunStatisticRs getUserStatistics(UUID userId, LocalDateTime from, LocalDateTime to) throws UserNotFoundException {
        List<UserRunRs> items = getUserRuns(userId, from, to);
        return new RunStatisticRs(
                items.size(),
                items.stream().mapToInt(UserRunRs::getDistance).sum(),
                items.stream().mapToDouble(UserRunRs::getAverageSpeed).average().orElse(0d));
    }

    private void checkCanStartRun(UUID userId, LocalDateTime startDateTime) throws LastRunNotFinishedException, InvalidRunStartTimeException {
        Optional<Run> run = service.findByUserOrderByStartDatetimeDesc(userId);
        if (run.isEmpty()) return;
        if (run.get().getFinishDatetime() == null)
            throw new LastRunNotFinishedException();
        if (startDateTime.isBefore(run.get().getStartDatetime()) || startDateTime.isBefore(run.get().getFinishDatetime()))
            throw new InvalidRunStartTimeException();
    }

    private Run getLastRun(UUID userId) throws NoActiveRunException {
        return service.findByUserOrderByStartDatetimeDesc(userId).orElseThrow(NoActiveRunException::new);
    }

    private void checkCanFinishRun(LocalDateTime finishDateTime, Run run) throws NoActiveRunException, InvalidRunFinishTimeException {
        if (run.getFinishDatetime() != null)
            throw new NoActiveRunException();
        if (finishDateTime.isBefore(run.getStartDatetime()))
            throw new InvalidRunFinishTimeException();
    }

    private Integer calculateDistance(Run run) {
        // https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
        int radius = 6371;
        double pi = Math.PI / 180;
        double a = 0.5 - Math.cos((run.getFinishLatitude() - run.getStartLatitude()) * pi) / 2 +
                Math.cos(run.getStartLatitude() * pi) * Math.cos(run.getFinishLatitude() * pi) *
                        (1 - Math.cos((run.getFinishLongitude() - run.getStartLongitude()) * pi)) / 2;
        return (int) Math.ceil(2 * radius * Math.asin(Math.sqrt(a)) * 1000);
    }

    private Double calculateAverageSpeed(Run run) {
        return ((double) run.getDistance() / 1000 / Duration.between(run.getStartDatetime(), run.getFinishDatetime()).toSeconds()) * 3600;
    }
}

@Service
@RequiredArgsConstructor
class RunTransactionalService {

    private final RunRepository runRepository;

    @Transactional(readOnly = true)
    Optional<Run> findByUserOrderByStartDatetimeDesc(UUID user) {
        return runRepository.findTopByUserOrderByStartDatetimeDesc(user);
    }

    @Transactional(readOnly = true)
    Collection<Run> findAllForUserRuns(UUID userId, LocalDateTime from, LocalDateTime to) {
        if (isNull(from) && isNull(to)) return runRepository.findAllByUser(userId);
        else if (isNull(from)) return runRepository.findAllByUserAndStartDatetimeLessThanEqual(userId, to);
        else if (isNull(to)) return runRepository.findAllByUserAndStartDatetimeGreaterThanEqual(userId, from);
        else return runRepository.findAllByUserAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanEqual(userId, from, to);
    }

    @Transactional(rollbackFor = Exception.class)
    Run save(Run run) {
        return runRepository.save(run);
    }
}
