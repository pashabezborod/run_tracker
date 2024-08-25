package ru.pashabezborod.bi_test.model.dto.run;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import ru.pashabezborod.bi_test.validation.Latitude;
import ru.pashabezborod.bi_test.validation.Longitude;

import java.time.LocalDateTime;

public record FinishRunRq(
        @NotNull(message = "User id can not be null")
        @Schema(title = "User id", requiredMode = Schema.RequiredMode.REQUIRED)
        String user,
        @Latitude
        @Schema(title = "Finish latitude (between -90 a 90)", example = "0.0", requiredMode = Schema.RequiredMode.REQUIRED)
        Double finishLatitude,
        @Longitude
        @Schema(title = "Finish longitude (between -180 a 180)", example = "0.0", requiredMode = Schema.RequiredMode.REQUIRED)
        Double finishLongitude,
        @NotNull(message = "Finish time can not be null")
        @PastOrPresent(message = "Finish time can not be future")
        @Schema(title = "Finish time. Format yyyy-MM-ddTHH:mm:ss", requiredMode = Schema.RequiredMode.REQUIRED, example = "2020-01-01T00:00:00")
        LocalDateTime finishDateTime,
        @PositiveOrZero(message = "Distance can not be negative")
        @Schema(title = "Total distance in meters. Can be null", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "100")
        Integer distance) {
}
