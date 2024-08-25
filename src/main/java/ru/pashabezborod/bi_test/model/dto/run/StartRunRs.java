package ru.pashabezborod.bi_test.model.dto.run;

import ru.pashabezborod.bi_test.model.db.Run;

import java.time.LocalDateTime;

public record StartRunRs(
        String id,
        Double startLatitude,
        Double startLongitude,
        LocalDateTime startDatetime
) {
    public StartRunRs(Run run) {
        this(
                run.getId().toString(),
                run.getStartLatitude(),
                run.getStartLongitude(),
                run.getStartDatetime()
        );
    }
}
