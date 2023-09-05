package com.weatherapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CoordinatesAndCityInfoDto {
  public Map<String, Double> coordinates;
  public String city;
}
