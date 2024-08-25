package ru.pashabezborod.bi_test.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import ru.pashabezborod.bi_test.model.Sex;
import ru.pashabezborod.bi_test.validation.actions.CreateAction;
import ru.pashabezborod.bi_test.validation.actions.UpdateAction;

import java.time.LocalDate;

public record UserCreateEditRq(
        @NotNull(message = "User's sex can not be empty", groups = CreateAction.class)
        @Schema(title = "User sex", requiredMode = Schema.RequiredMode.REQUIRED, example = "OTHER")
        Sex sex,
        @NotBlank(message = "User's first name can not be blank", groups = CreateAction.class)
        @Schema(title = "User first name. Can contain only letters", requiredMode = Schema.RequiredMode.REQUIRED, example = "Name")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "First name can contain only letters", groups = {CreateAction.class, UpdateAction.class})
        String firstName,
        @NotBlank(message = "User's last name can not be blank", groups = CreateAction.class)
        @Schema(title = "User last name. Can contain only letters", requiredMode = Schema.RequiredMode.REQUIRED, example = "Surname")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "Last name can contain only letters", groups = {CreateAction.class, UpdateAction.class})
        String lastName,
        @NotNull(message = "User's birthDate can not be null", groups = CreateAction.class)
        @PastOrPresent(message = "Birth date can not be future", groups = {CreateAction.class, UpdateAction.class})
        @Schema(title = "Birth date. Format yyyy-MM-dd", requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01")
        LocalDate birthDate) {
}
