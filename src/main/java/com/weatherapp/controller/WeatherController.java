package com.weatherapp.controller;

import com.weatherapp.dto.CoordinatesAndCityInfoDto;
import com.weatherapp.dto.WeatherReportDto;
import com.weatherapp.dto.WeatherReportInfoDto;
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

  @GetMapping(value = "/byCity")
  public ResponseEntity<?> getWeatherByCity(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam String city,
      @RequestParam List<String> fields) {
    try {
      // Fetch coordinates and city info
      CoordinatesAndCityInfoDto info = localizationService.getLatLong(city);
      String jwtToken = null;
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwtToken = authorizationHeader.substring(7);
      }
      logger.info("Fetching weather for city: " + city);
      WeatherApiResponse weatherApiResponse = weatherService.fetchWeatherByCity(city, jwtToken);

      // Create WeatherReportInfoDto
      WeatherReportInfoDto reportInfo = new WeatherReportInfoDto();
      reportInfo.setCity(info.getCity());

      // Generate the report based on the user's selected fields
      WeatherReportDto weatherReport =
          weatherService.generateReport(fields, weatherApiResponse, reportInfo);

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
    CoordinatesAndCityInfoDto info = localizationService.getLatLong(city);
    Map<String, Double> coordinates = info.getCoordinates();
    Double lat = coordinates.get("lat");
    Double lon = coordinates.get("lon");
    return weatherService.fetchWeatherByCoordinates(lat, lon, jwtToken);
  }

  @GetMapping(value = "/searchCity")
  public List<Map<String, String>> searchCity(@RequestParam String city) {
    return localizationService.searchCity(city);
  }
}
