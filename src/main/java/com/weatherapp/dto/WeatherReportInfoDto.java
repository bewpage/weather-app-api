package com.weatherapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherReportInfoDto {
  private String startForecastDateTime;
  private String endForecastDateTime;
  private String city;
}
