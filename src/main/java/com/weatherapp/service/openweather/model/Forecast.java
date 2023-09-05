package com.weatherapp.service.openweather.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Forecast {
    private Main main;
    private List<Weather> weather;
    private Long dt;
    private Wind wind;
}
