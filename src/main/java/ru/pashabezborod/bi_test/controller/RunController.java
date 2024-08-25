package ru.pashabezborod.bi_test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pashabezborod.bi_test.exception.MBadRequest;
import ru.pashabezborod.bi_test.model.dto.run.FinishRunRq;
import ru.pashabezborod.bi_test.model.dto.run.FinishRunRs;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRq;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRs;
import ru.pashabezborod.bi_test.service.RunService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/run")
@Tag(name = "Run operations")
class RunController {

    private final RunService runService;

    @PostMapping("/start")
    @Operation(summary = "Start new run")
    StartRunRs startRun(@RequestBody StartRunRq rq) throws MBadRequest {
        return new StartRunRs(runService.startRun(rq));
    }

    @PostMapping("/finish")
    @Operation(summary = "Finish run")
    FinishRunRs finishRun(@RequestBody FinishRunRq rq) throws MBadRequest {
        return new FinishRunRs(runService.finishRun(rq));
    }
}
