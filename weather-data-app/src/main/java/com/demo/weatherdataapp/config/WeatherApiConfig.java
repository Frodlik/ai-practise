package com.demo.weatherdataapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "weather-api")
public class WeatherApiConfig {
    private String baseUrl;
    private String apiKey;
}
