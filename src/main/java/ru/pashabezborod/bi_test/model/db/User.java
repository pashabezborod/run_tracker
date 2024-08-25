package ru.pashabezborod.bi_test.model.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.pashabezborod.bi_test.model.Sex;
import ru.pashabezborod.bi_test.model.dto.user.UserCreateEditRq;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    /*
    In this case I don't want to use List<Run> collection provided by Hibernate, because we always use
    conditional select from run table and filtering collection of all runs could dramatically reduce performance
    */

    public User(UserCreateEditRq rq) {
        this.sex = rq.sex();
        this.firstName = rq.firstName();
        this.lastName = rq.lastName();
        this.birthDate = rq.birthDate();
    }
}
