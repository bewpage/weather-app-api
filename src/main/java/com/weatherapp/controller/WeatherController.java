package com.weatherapp.controller;

import com.weatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
  @Autowired private WeatherService weatherService;

  @GetMapping(value = "/byCity/{city}")
  @CrossOrigin(origins = "http://localhost:3000")
  public String getWeatherByCity(@PathVariable String city) {
    return weatherService.fetchWeather(city);
  }
}
