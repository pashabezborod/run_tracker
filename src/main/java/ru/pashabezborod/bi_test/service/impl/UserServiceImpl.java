package ru.pashabezborod.bi_test.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.pashabezborod.bi_test.exception.UserNotFoundException;
import ru.pashabezborod.bi_test.model.db.User;
import ru.pashabezborod.bi_test.model.dto.user.UserCreateEditRq;
import ru.pashabezborod.bi_test.service.UserService;
import ru.pashabezborod.bi_test.validation.actions.CreateAction;
import ru.pashabezborod.bi_test.validation.actions.UpdateAction;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserTransactionalService service;

    @Override
    public User get(@NotNull @Valid UUID uuid) throws UserNotFoundException {
        return service.get(uuid);
    }

    @Override
    public Collection<User> getAll() {
        return service.getAll();
    }

    @Override
    @Validated(CreateAction.class)
    public User create(@Valid UserCreateEditRq rq) {
        User result = service.save(new User(rq));
        log.info("Created user: {}", result);
        return result;
    }

    @Override
    @Validated(UpdateAction.class)
    public User edit(@NotNull(groups = UpdateAction.class) @Valid UUID uuid, @Valid UserCreateEditRq rq) throws UserNotFoundException {
        User user = get(uuid);
        boolean userChanged = false;
        if (rq.sex() != null && !Objects.equals(rq.sex(), user.getSex())) {
            userChanged = true;
            user.setSex(rq.sex());
        }
        if (rq.birthDate() != null && !Objects.equals(rq.birthDate(), user.getBirthDate())) {
            userChanged = true;
            user.setBirthDate(rq.birthDate());
        }
        if (rq.firstName() != null && !Objects.equals(rq.firstName(), user.getFirstName())) {
            userChanged = true;
            user.setFirstName(rq.firstName());
        }
        if (rq.lastName() != null && !Objects.equals(rq.lastName(), user.getLastName())) {
            userChanged = true;
            user.setLastName(rq.lastName());
        }
        if (userChanged) {
            log.info("Updating user: old data: {}, new data: {}", user, rq);
            service.save(user);
        }
        return user;
    }

    @Override
    public void deleteUser(@NotNull @Valid UUID uuid) {
        if (service.exists(uuid)) {
            service.delete(uuid);
            log.info("Deleted user: {}", uuid);
        }
    }
}

@Service
@RequiredArgsConstructor
class UserTransactionalService {

    private final ru.pashabezborod.bi_test.repository.UserRepository userRepository;

    @Transactional(readOnly = true)
    User get(UUID id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    Collection<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    boolean exists(UUID id) {
        return userRepository.existsById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
