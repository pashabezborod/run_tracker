package ru.pashabezborod.bi_test.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.pashabezborod.bi_test.validation.validators.LatitudeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LatitudeValidator.class)
public @interface Latitude {

    String message() default "Latitude must be between -90 and 90";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
