package ru.pashabezborod.bi_test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.pashabezborod.bi_test.exception.UserNotFoundException;
import ru.pashabezborod.bi_test.exception.UuidNotValidException;
import ru.pashabezborod.bi_test.model.dto.run.RunStatisticRs;
import ru.pashabezborod.bi_test.model.dto.run.UserRunRs;
import ru.pashabezborod.bi_test.model.dto.user.UserCreateEditRq;
import ru.pashabezborod.bi_test.model.dto.user.UserRs;
import ru.pashabezborod.bi_test.service.RunService;
import ru.pashabezborod.bi_test.service.UserService;
import ru.pashabezborod.bi_test.util.UuidHelper;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User operations")
class UserController {

    private final UserService userService;
    private final RunService runService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    UserRs get(@PathVariable String id) throws UserNotFoundException, UuidNotValidException {
        return new UserRs(userService.get(UuidHelper.get(id)));
    }

    @GetMapping
    @Operation(summary = "Get list of all users")
    List<UserRs> getAll() {
        return userService.getAll().stream()
                .map(UserRs::new)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Create new user")
    UserRs create(@RequestBody UserCreateEditRq rq) {
        return new UserRs(userService.create(rq));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user data")
    UserRs update(@PathVariable String id, @RequestBody UserCreateEditRq rq) throws UuidNotValidException, UserNotFoundException {
        return new UserRs(userService.edit(UuidHelper.get(id), rq));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void delete(@PathVariable String id) throws UuidNotValidException {
        userService.deleteUser(UuidHelper.get(id));
    }

    @Operation(summary = "User statistic")
    @GetMapping("/{id}/statistic")
    RunStatisticRs getUserStatistic(
            @PathVariable
            @Schema(title = "User id")
            String id,
            @RequestParam(required = false)
            @Schema(title = "Start runs time after, format yyyy-MM-ddTHH:mm:ss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime from,
            @RequestParam(required = false)
            @Schema(title = "Start runs time before, format yyyy-MM-ddTHH:mm:ss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime to) throws UuidNotValidException, UserNotFoundException {
        return runService.getUserStatistics(UuidHelper.get(id), from, to);
    }

    @GetMapping("/{id}/run")
    List<UserRunRs> getUserRuns(
            @PathVariable
            @Schema(title = "User id")
            String id,
            @RequestParam(required = false)
            @Schema(title = "Start runs time after, format yyyy-MM-ddTHH:mm:ss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime from,
            @RequestParam(required = false)
            @Schema(title = "Start runs time before, format yyyy-MM-ddTHH:mm:ss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime to) throws UuidNotValidException, UserNotFoundException {
        return runService.getUserRuns(UuidHelper.get(id), from, to);
    }
}
