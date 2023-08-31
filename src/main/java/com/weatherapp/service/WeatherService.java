package com.weatherapp.service;

import com.weatherapp.dto.WeatherReportDto;
import com.weatherapp.exception.WeatherServiceException;
import com.weatherapp.service.openweather.model.Forecast;
import com.weatherapp.service.openweather.model.WeatherApiResponse;
import com.weatherapp.utility.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService implements IWeatherService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${OPENWEATHER_API_KEY}")
  private String apiKey;

  @Autowired private RestTemplate restTemplate;

  @Autowired private LocalizationService localizationService;

  @Autowired private JwtUtil jwtUtil;

  @Override
  public WeatherApiResponse fetchWeatherByCity(String city, String jwtToken) {

    // Validate the JWT
    if (!jwtUtil.isValidToken(jwtToken)) {
      throw new SecurityException("Invalid JWT token");
    }

    // Step 1: Get Coordinates for City
    logger.info("Fetching coordinates for city: " + city);
    Map<String, Double> coordinates = localizationService.getLatLong(city);
    if (coordinates == null || coordinates.isEmpty()) {
      throw new RuntimeException("Failed to fetch coordinates for city: " + city);
    }
    logger.info("Coordinates for city: " + city + " are: " + coordinates.toString());

    // Step 2: Get Weather for Coordinates
    logger.info("Fetching weather for coordinates: " + coordinates.toString());
    // Build the URL for the 5 day/3-hour forecast
    String apiURL = "https://api.openweathermap.org/data/2.5/forecast?";
    String units = "&units=metric";
    String lat = "lat=" + coordinates.get("lat");
    String lon = "lon=" + coordinates.get("lon");
    String url = apiURL + lat + "&" + lon + "&appid=" + apiKey + units;

    try {
      return restTemplate.getForObject(url, WeatherApiResponse.class);
    } catch (HttpClientErrorException e) {
      logger.error("HTTP Error: {}", e.getStatusCode());
      throw new WeatherServiceException(
          "Could not fetch weather data. HTTP Error: " + e.getStatusCode());
    } catch (RestClientException e) {
      logger.error("Rest Client Exception: {}", e.getMessage());
      throw new WeatherServiceException(
          "Could not fetch weather data. Rest client exception: " + e.getMessage());
    }
  }

  @Override
  public String fetchWeatherByCoordinates(Double lat, Double lon, String jwtToken) {

    // Validate the JWT
    if (!jwtUtil.isValidToken(jwtToken)) {
      throw new SecurityException("Invalid JWT token");
    }

    String apiURL = "https://api.openweathermap.org/data/2.5/weather?";
    String units = "&units=metric";
    String url = apiURL + "lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + units;
    return restTemplate.getForObject(url, String.class);
  }

  @Override
  public WeatherReportDto generateReport(
      List<String> requestedFields, WeatherApiResponse weatherApiResponse) {
    WeatherReportDto report = new WeatherReportDto();
    if (weatherApiResponse != null
        && weatherApiResponse.getList() != null
        && !weatherApiResponse.getList().isEmpty()) {
      List<Forecast> forecasts = weatherApiResponse.getList();
      if (requestedFields.contains("averageTemperature")) {
        double totalTemperature = 0;
        for (Forecast forecast : forecasts) {
          totalTemperature += forecast.getMain().getTemp();
        }
        double averageTemperature = totalTemperature / forecasts.size();
        report.setAverageTemperature(averageTemperature);
      }
      if (requestedFields.contains("averageHumidity")) {
        double totalHumidity = 0;
        for (Forecast forecast : forecasts) {
          totalHumidity += forecast.getMain().getHumidity();
        }
        double averageHumidity = totalHumidity / forecasts.size();
        report.setAverageHumidity(averageHumidity);
      }
      if (requestedFields.contains("maxTemperature")) {
        double maxTemperature =
            Double.MIN_VALUE; // Initialize to the smallest possible double value
        for (Forecast forecast : forecasts) {
          if (forecast.getMain().getTemp() > maxTemperature) {
            maxTemperature = forecast.getMain().getTemp();
          }
        }
        report.setMaxTemperature(maxTemperature);
      }
      if (requestedFields.contains("averageWindSpeed")) {
        double totalWindSpeed = 0;
        for (Forecast forecast : forecasts) {
          totalWindSpeed += forecast.getWind().getSpeed();
        }
        double averageWindSpeed = totalWindSpeed / forecasts.size();
        report.setAverageWindSpeed(averageWindSpeed);
      }
    }

    return report;
  }
}
