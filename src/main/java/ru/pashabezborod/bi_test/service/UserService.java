package ru.pashabezborod.bi_test.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import ru.pashabezborod.bi_test.exception.UserNotFoundException;
import ru.pashabezborod.bi_test.exception.UuidNotValidException;
import ru.pashabezborod.bi_test.model.db.User;
import ru.pashabezborod.bi_test.model.dto.user.UserCreateEditRq;
import ru.pashabezborod.bi_test.validation.actions.CreateAction;
import ru.pashabezborod.bi_test.validation.actions.UpdateAction;

import java.util.Collection;
import java.util.UUID;

public interface UserService {

    User get(@NotNull @Valid UUID uuid) throws UserNotFoundException;

    Collection<User> getAll();

    @Validated(CreateAction.class)
    User create(@Valid UserCreateEditRq rq);

    @Validated(UpdateAction.class)
    User edit(@NotNull(groups = UpdateAction.class) @Valid UUID uuid, @Valid UserCreateEditRq rq) throws UserNotFoundException, UuidNotValidException;

    void deleteUser(@NotNull @Valid UUID uuid) throws UuidNotValidException;
}
