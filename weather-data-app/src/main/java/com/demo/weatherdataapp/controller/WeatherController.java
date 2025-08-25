package com.demo.weatherdataapp.controller;

import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.dto.WeatherResponse;
import com.demo.weatherdataapp.service.WeatherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Validated
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/city/{cityName}")
    public ResponseEntity<WeatherResponse> getWeatherByCity(@PathVariable @NotBlank String cityName) {
        log.info("GET /api/weather/city/{}", cityName);

        WeatherResponse response = weatherService.getWeatherByCity(cityName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/zip/{zipCode}")
    public ResponseEntity<WeatherResponse> getWeatherByZipCode(
            @PathVariable @NotBlank String zipCode,
            @RequestParam(value = "country", required = false) String country) {
        log.info("GET /api/weather/zip/{} (country: {})", zipCode, country);

        WeatherResponse response = weatherService.getWeatherByZipCode(zipCode, country);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<WeatherResponse> searchWeather(@Valid @RequestBody WeatherRequest request) {
        log.info("POST /api/weather/search: {}", request);

        WeatherResponse response = weatherService.getWeather(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh/city/{cityName}")
    public ResponseEntity<WeatherResponse> refreshCityWeather(@PathVariable @NotBlank String cityName) {
        log.info("POST /api/weather/refresh/city/{}", cityName);

        WeatherResponse response = weatherService.refreshWeatherByCity(cityName);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh/zip/{zipCode}")
    public ResponseEntity<WeatherResponse> refreshZipCodeWeather(
            @PathVariable @NotBlank String zipCode,
            @RequestParam(value = "country", required = false) String country) {
        log.info("POST /api/weather/refresh/zip/{} (country: {})", zipCode, country);

        WeatherResponse response = weatherService.refreshWeatherByZipCode(zipCode, country);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tracked/cities")
    public ResponseEntity<List<String>> getTrackedCities() {
        log.debug("GET /api/weather/tracked/cities");

        List<String> cities = weatherService.getAllTrackedCities();

        return ResponseEntity.ok(cities);
    }

    @GetMapping("/tracked/zipcodes")
    public ResponseEntity<List<String>> getTrackedZipCodes() {
        log.debug("GET /api/weather/tracked/zipcodes");

        List<String> zipCodes = weatherService.getAllTrackedZipCodes();

        return ResponseEntity.ok(zipCodes);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Weather service is running");
    }
}
