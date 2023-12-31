package com.weatherapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeatherReportDataDto {
    private Double averageTemperature;
    private Double maxTemperature;
    private Double minTemperature;
    private Double averageHumidity;
    private Double maxWindSpeed;
    private Double averageWindSpeed;
}
