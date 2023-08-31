package com.weatherapp.controller;

import com.weatherapp.dto.WeatherReportDto;
import com.weatherapp.service.LocalizationService;
import com.weatherapp.service.WeatherService;
import com.weatherapp.service.openweather.model.WeatherApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private WeatherService weatherService;

  @Autowired private LocalizationService localizationService;

  @GetMapping(value = "/byCity/{city}")
  public ResponseEntity<?> getWeatherByCity(
      @RequestHeader("Authorization") String authorizationHeader, @PathVariable String city, @RequestParam List<String> fields) {
    try {
      String jwtToken = null;
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwtToken = authorizationHeader.substring(7);
      }
      logger.info("Fetching weather for city: " + city);
      WeatherApiResponse weatherApiResponse = weatherService.fetchWeatherByCity(city, jwtToken);

      // Generate the report based on the user's selected fields
      WeatherReportDto weatherReport = weatherService.generateReport(fields, weatherApiResponse);

      return new ResponseEntity<>(weatherReport, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error fetching weather: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value = "/byCoordinates")
  public String getWeatherByCoordinates(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam("lat") Double lat,
      @RequestParam("lon") Double lon) {
    logger.info("Fetching weather for coordinates: " + lat + ", " + lon);
    String jwtToken = null;
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwtToken = authorizationHeader.substring(7);
    }
    return weatherService.fetchWeatherByCoordinates(lat, lon, jwtToken);
  }

  @GetMapping(value = "/byCityCoordinates/{city}")
  public String getWeatherByCityCoordinates(
      @RequestHeader("Authorization") String authorizationHeader, @PathVariable String city) {
    String jwtToken = null;
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwtToken = authorizationHeader.substring(7);
    }
    Map<String, Double> coordinates = localizationService.getLatLong(city);
    return weatherService.fetchWeatherByCoordinates(
        coordinates.get("lat"), coordinates.get("lon"), jwtToken);
  }
}
