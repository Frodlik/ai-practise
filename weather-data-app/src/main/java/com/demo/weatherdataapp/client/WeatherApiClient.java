package com.demo.weatherdataapp.client;

import com.demo.weatherdataapp.dto.OpenWeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather-api-client", url = "${weather-api.base-url}")
public interface WeatherApiClient {
    @GetMapping
    OpenWeatherResponse getWeatherByCity(
            @RequestParam("q") String city,
            @RequestParam("appid") String apiKey,
            @RequestParam("units") String units
    );

    @GetMapping
    OpenWeatherResponse getWeatherByZipCode(
            @RequestParam("zip") String zipCode,
            @RequestParam("appid") String apiKey,
            @RequestParam("units") String units
    );
}
