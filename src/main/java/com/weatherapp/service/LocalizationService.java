package com.weatherapp.service;

import com.weatherapp.dto.OSMResponseDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LocalizationService implements ILocalizationService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private RestTemplate restTemplate;

  @Override
  public boolean verifyCity(String city) {
    String url = "https://nominatim.openstreetmap.org/search?q=" + city + "&format=json";
    OSMResponseDto[] response = restTemplate.getForObject(url, OSMResponseDto[].class);
    return response != null && response.length > 0;
  }

  @Override
  public List<Map<String, String>> searchCity(String cityQuery) {
    RestTemplate restTemplate = new RestTemplate();
    String url = "https://nominatim.openstreetmap.org/search?q=" + cityQuery + "&format=json";
    OSMResponseDto[] response = restTemplate.getForObject(url, OSMResponseDto[].class);

    List<Map<String, String>> searchResults = new ArrayList<>();

    if (response != null) {
      for (OSMResponseDto result : response) {
        Map<String, String> cityData = new HashMap<>();
        cityData.put("city", result.getDisplay_name());
        cityData.put("lat", String.valueOf(result.getLatitude()));
        cityData.put("lon", String.valueOf(result.getLongitude()));
        searchResults.add(cityData);
      }
    }

    return searchResults;
  }

  @RateLimiter(name = "getLatLong", fallbackMethod = "getLatLongFallback")
  @Override
  public Map<String, Double> getLatLong(String city) {
    String url = "https://nominatim.openstreetmap.org/search?q=" + city + "&format=json";
    String yourApplicationName = "WeatherApp v1.0 (bewpage)";

    // Create headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", yourApplicationName);
    // Create HttpEntity object
    HttpEntity<String> entity = new HttpEntity<>(headers);

    Map<String, Double> coordinates = new HashMap<>();

    try {
      // Make the API call
      ResponseEntity<OSMResponseDto[]> responseEntity =
          restTemplate.exchange(url, HttpMethod.GET, entity, OSMResponseDto[].class);
      OSMResponseDto[] response = responseEntity.getBody();
      if (response != null && response.length > 0) {
        coordinates.put("lat", response[0].getLatitude());
        coordinates.put("lon", response[0].getLongitude());
      } else {
        throw new Exception("No coordinates found for city: " + city);
      }

    } catch (Exception e) {
      // Handle exception
      logger.error("Failed to fetch coordinates: {}", e.getMessage());
      throw new RuntimeException("Failed to fetch coordinates: " + e.getMessage());
    }

    return coordinates;
  }

  @Override
  public Map<String, Double> getLatLongFallback(String city, Throwable t) {
    logger.error("Rate limit exceeded for city: {}", city, t);
    return Collections.emptyMap();
  }
}
