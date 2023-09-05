package com.weatherapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CityNameValidator implements ConstraintValidator<ValidCityName, String> {
  @Override
  public void initialize(ValidCityName constraintAnnotation) {
    // Initialization code, if needed.
  }

  @Override
  public boolean isValid(String cityName, ConstraintValidatorContext context) {
    if (cityName == null) {
      return false;
    }

    // Only allow alphabets and spaces in the city name
    return cityName.matches("[a-zA-Z\\s]+");
  }
}
