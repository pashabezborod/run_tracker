package ru.pashabezborod.bi_test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pashabezborod.bi_test.model.db.Run;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RunRepository extends JpaRepository<Run, UUID> {

    Optional<Run> findTopByUserOrderByStartDatetimeDesc(UUID user);

    Collection<Run> findAllByUserAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanEqual(
            UUID user, LocalDateTime startDatetime, LocalDateTime endDatetime);

    Collection<Run> findAllByUserAndStartDatetimeGreaterThanEqual(UUID user, LocalDateTime startDatetime);

    Collection<Run> findAllByUserAndStartDatetimeLessThanEqual(UUID user, LocalDateTime endDatetime);

    Collection<Run> findAllByUser(UUID user);
}
