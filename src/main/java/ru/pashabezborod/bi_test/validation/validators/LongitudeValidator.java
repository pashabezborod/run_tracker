package ru.pashabezborod.bi_test.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.pashabezborod.bi_test.validation.Longitude;

import java.util.Objects;

public class LongitudeValidator implements ConstraintValidator<Longitude, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return Objects.nonNull(value) && value >= -180 && value <= 180;
    }
}
