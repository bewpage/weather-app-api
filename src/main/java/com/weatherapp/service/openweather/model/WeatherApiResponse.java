package com.weatherapp.service.openweather.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WeatherApiResponse {
  private List<Forecast> list;
  private City city;
}
