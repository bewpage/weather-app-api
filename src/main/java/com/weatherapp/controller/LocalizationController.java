package com.weatherapp.controller;

import com.weatherapp.service.LocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/location")
public class LocalizationController {

  @Autowired private LocalizationService localizationService;

  @GetMapping("/verify/{city}")
  public ResponseEntity<String> verifyCity(@PathVariable String city) {
    boolean isValid = localizationService.verifyCity(city);
    if (isValid) {
      return new ResponseEntity<>("City is valid", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("City not found", HttpStatus.NOT_FOUND);
    }
  }
}
