package com.demo.weatherdataapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherResponse {
    private String city;
    private String country;
    private String zipCode;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Integer pressure;
    private String description;
    private String mainWeather;
    private Double windSpeed;
    private Integer windDirection;
    private String lastUpdated;
}
