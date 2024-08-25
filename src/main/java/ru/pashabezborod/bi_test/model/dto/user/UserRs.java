package ru.pashabezborod.bi_test.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.pashabezborod.bi_test.model.Sex;
import ru.pashabezborod.bi_test.model.db.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRs {
    private String id;
    private Sex sex;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public UserRs(User user) {
        id = user.getId().toString();
        sex = user.getSex();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        birthDate = user.getBirthDate();
    }

}
