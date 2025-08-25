package com.demo.weatherdataapp.service;

import com.demo.weatherdataapp.dto.OpenWeatherResponse;
import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.dto.WeatherResponse;
import com.demo.weatherdataapp.entity.WeatherData;

import java.util.List;

public interface WeatherService {
    WeatherResponse getWeather(WeatherRequest request);

    WeatherResponse getWeatherByCity(String cityName);

    WeatherResponse getWeatherByZipCode(String zipCode, String country);

    WeatherResponse refreshWeatherByCity(String cityName);

    WeatherResponse refreshWeatherByZipCode(String zipCode, String country);

    OpenWeatherResponse fetchWeatherData(WeatherRequest request);

    WeatherData fetchAndSaveWeatherData(WeatherRequest request);

    List<String> getAllTrackedCities();

    List<String> getAllTrackedZipCodes();

    void refreshWeatherDataForCity(String city);

    void refreshWeatherDataForZipCode(String zipCode);
}
