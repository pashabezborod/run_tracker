package ru.pashabezborod.bi_test.service;

import jakarta.validation.Valid;
import ru.pashabezborod.bi_test.exception.*;
import ru.pashabezborod.bi_test.model.db.Run;
import ru.pashabezborod.bi_test.model.dto.run.FinishRunRq;
import ru.pashabezborod.bi_test.model.dto.run.RunStatisticRs;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRq;
import ru.pashabezborod.bi_test.model.dto.run.UserRunRs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RunService {

    Run startRun(@Valid StartRunRq rq) throws MBadRequest;

    Run finishRun(@Valid FinishRunRq rq) throws MBadRequest;

    List<UserRunRs> getUserRuns(UUID userId, LocalDateTime from, LocalDateTime to) throws UserNotFoundException;

    RunStatisticRs getUserStatistics(UUID userId, LocalDateTime from, LocalDateTime to) throws UserNotFoundException;
}
