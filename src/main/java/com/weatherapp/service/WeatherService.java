package com.weatherapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

  @Value("${OPENWEATHER_API_KEY}")
  private String apiKey;

  @Autowired private RestTemplate restTemplate;

  public String fetchWeather(String city) {
    String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=";
    String units = "&units=metric";
    String url = apiURL + city + "&appid=" + apiKey + units;
    return restTemplate.getForObject(url, String.class);
  }
}
