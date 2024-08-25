package ru.pashabezborod.bi_test;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.pashabezborod.bi_test.model.Sex;
import ru.pashabezborod.bi_test.model.db.User;
import ru.pashabezborod.bi_test.repository.RunRepository;
import ru.pashabezborod.bi_test.repository.UserRepository;
import ru.pashabezborod.bi_test.service.RunService;
import ru.pashabezborod.bi_test.service.UserService;

import java.time.LocalDate;
import java.util.Random;

public abstract class TestCommon {

    protected static PostgreSQLContainer<?> postgres;

    @Autowired protected UserRepository userRepository;
    @Autowired protected RunRepository runRepository;

    @Autowired protected RunService runService;
    @Autowired protected UserService userService;

    protected User user;

    protected void createTestUser() {
        var user = new User();
        var random = RandomStringGenerator.builder().withinRange(65, 122).build();
        user.setSex(Sex.values()[new Random().nextInt(2)]);
        user.setBirthDate(LocalDate.now());
        user.setFirstName(random.generate(10));
        user.setLastName(random.generate(10));
        this.user = userRepository.save(user);
    }
}
