package ru.pashabezborod.bi_test.validation.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.pashabezborod.bi_test.validation.Latitude;

import java.util.Objects;

public class LatitudeValidator implements ConstraintValidator<Latitude, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return Objects.nonNull(value) && value >= -90 && value <= 90;
    }
}
