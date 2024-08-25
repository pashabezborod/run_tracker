package ru.pashabezborod.bi_test.model.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pashabezborod.bi_test.exception.UuidNotValidException;
import ru.pashabezborod.bi_test.model.dto.run.StartRunRq;
import ru.pashabezborod.bi_test.util.UuidHelper;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Run {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "\"user\"")
    private UUID user;
    private Double startLatitude;
    private Double startLongitude;
    private LocalDateTime startDatetime;
    private Double finishLatitude;
    private Double finishLongitude;
    private LocalDateTime finishDatetime;
    private Integer distance;


    public Run(StartRunRq rq) throws UuidNotValidException {
        this.user = UuidHelper.get(rq.user());
        this.startLatitude = rq.startLatitude();
        this.startLongitude = rq.startLongitude();
        this.startDatetime = rq.startDateTime();
    }
}
