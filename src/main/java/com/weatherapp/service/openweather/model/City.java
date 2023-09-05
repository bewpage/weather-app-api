package com.weatherapp.service.openweather.model;

import com.weatherapp.validation.ValidCityName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class City {
  @ValidCityName private String name;
  private Coordinates coordinates;
}
