package com.demo.weatherdataapp.service;

import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.entity.WeatherData;

import java.util.Optional;

public interface WeatherCacheService {
    Optional<WeatherData> getCachedWeatherData(WeatherRequest request);

    boolean isCacheValid(WeatherData data);

    WeatherData saveToCache(WeatherData weatherData);

    void cleanupExpiredCache();
}
