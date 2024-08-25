package ru.pashabezborod.bi_test.model.dto.run;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.pashabezborod.bi_test.model.db.Run;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserRunRs {
    private String id;
    private Double startLatitude;
    private Double startLongitude;
    private LocalDateTime startDatetime;
    private Double finishLatitude;
    private Double finishLongitude;
    private LocalDateTime finishDatetime;
    private Integer distance;
    private Double averageSpeed;

    public UserRunRs(Run run, Double averageSpeed) {
        id = run.getId().toString();
        startLatitude = run.getStartLatitude();
        startLongitude = run.getStartLongitude();
        startDatetime = run.getStartDatetime();
        finishLatitude = run.getFinishLatitude();
        finishLongitude = run.getFinishLongitude();
        finishDatetime = run.getFinishDatetime();
        distance = run.getDistance();
        this.averageSpeed = averageSpeed;
    }
}
