package ru.pashabezborod.bi_test.model.dto.run;

public record RunStatisticRs(
        Integer numberOfRuns,
        Integer totalMeters,
        Double averageSpeed) {
}
