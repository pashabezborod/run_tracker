package ru.pashabezborod.bi_test.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.pashabezborod.bi_test.validation.validators.LongitudeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LongitudeValidator.class)
public @interface Longitude {

    String message() default "Latitude must be between -180 and 180";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

