package com.weatherapp.service;

import com.weatherapp.dto.WeatherReportDto;
import com.weatherapp.dto.WeatherReportInfoDto;
import com.weatherapp.service.openweather.model.WeatherApiResponse;

import java.util.List;

public interface IWeatherService {
  WeatherApiResponse fetchWeatherByCity(String city, String jwtToken);

  String fetchWeatherByCoordinates(Double lat, Double lon, String jwtToken);

  WeatherReportDto generateReport(
      List<String> requestedFields, WeatherApiResponse weatherApiResponse, WeatherReportInfoDto reportInfo);
}
