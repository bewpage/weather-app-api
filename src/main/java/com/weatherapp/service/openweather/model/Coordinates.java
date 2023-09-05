package com.weatherapp.service.openweather.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Coordinates {
    private double lat;
    private double lon;
    private String displayName;
}
