package com.weatherapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeatherReportDto {
  private WeatherReportDataDto weatherReportData;
  private WeatherReportInfoDto weatherReportInfo;
}
