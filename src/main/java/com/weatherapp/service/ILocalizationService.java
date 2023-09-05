package com.weatherapp.service;

import com.weatherapp.dto.CoordinatesAndCityInfoDto;

import java.util.List;
import java.util.Map;

public interface ILocalizationService {
  CoordinatesAndCityInfoDto getLatLong(String city);

  boolean verifyCity(String city);

  List<Map<String, String>> searchCity(String cityQuery);

  CoordinatesAndCityInfoDto getLatLongFallback(String city, Throwable t);
}
