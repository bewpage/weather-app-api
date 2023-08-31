package com.weatherapp.service;

import java.util.List;
import java.util.Map;

public interface ILocalizationService {
  Map<String, Double> getLatLong(String city);

  boolean verifyCity(String city);

  List<Map<String, String>> searchCity(String cityQuery);

  Map<String, Double> getLatLongFallback(String city, Throwable t);
}
