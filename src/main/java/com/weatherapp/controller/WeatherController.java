package com.weatherapp.controller;

import com.weatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

  @Autowired private WeatherService weatherService;

  @GetMapping(value = "/byCity/{city}")
  public String getWeatherByCity(
      @RequestHeader("Authorization") String authorizationHeader, @PathVariable String city) {
    String jwtToken = null;
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwtToken = authorizationHeader.substring(7);
    }
    return weatherService.fetchWeather(city, jwtToken);
  }
}
