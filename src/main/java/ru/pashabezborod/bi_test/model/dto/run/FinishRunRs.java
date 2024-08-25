package ru.pashabezborod.bi_test.model.dto.run;

import ru.pashabezborod.bi_test.model.db.Run;

import java.time.LocalDateTime;

public record FinishRunRs(
        String id,
        Double startLatitude,
        Double startLongitude,
        LocalDateTime startDatetime,
        Double finishLatitude,
        Double finishLongitude,
        LocalDateTime finishDatetime,
        Integer distance) {

    public FinishRunRs(Run run) {
        this(
                run.getId().toString(),
                run.getStartLatitude(),
                run.getStartLongitude(),
                run.getStartDatetime(),
                run.getFinishLatitude(),
                run.getFinishLongitude(),
                run.getFinishDatetime(),
                run.getDistance()
        );
    }
}
