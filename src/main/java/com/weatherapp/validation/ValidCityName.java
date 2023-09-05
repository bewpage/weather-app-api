package com.weatherapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CityNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCityName {
    String message() default "Invalid city name. The city name can only contain alphabets and spaces.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
