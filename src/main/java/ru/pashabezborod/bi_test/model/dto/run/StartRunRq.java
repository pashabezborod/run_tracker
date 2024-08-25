package ru.pashabezborod.bi_test.model.dto.run;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import ru.pashabezborod.bi_test.validation.Latitude;
import ru.pashabezborod.bi_test.validation.Longitude;

import java.time.LocalDateTime;

public record StartRunRq(
        @NotNull(message = "User id can not be null")
        @Schema(title = "User id", requiredMode = Schema.RequiredMode.REQUIRED)
        String user,
        @Latitude
        @Schema(title = "Start latitude (between -90 a 90)", example = "0.0", requiredMode = Schema.RequiredMode.REQUIRED)
        Double startLatitude,
        @Longitude
        @Schema(title = "Start longitude (between -180 a 180)", example = "0.0", requiredMode = Schema.RequiredMode.REQUIRED)
        Double startLongitude,
        @NotNull(message = "Start time can not be null")
        @PastOrPresent(message = "Start time can not be future")
        @Schema(title = "Start time. Format yyyy-MM-ddTHH:mm:ss", requiredMode = Schema.RequiredMode.REQUIRED, example = "2020-01-01T00:00:00")
        LocalDateTime startDateTime) {
}
