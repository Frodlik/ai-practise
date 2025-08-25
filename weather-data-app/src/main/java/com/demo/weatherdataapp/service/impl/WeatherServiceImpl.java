package com.demo.weatherdataapp.service.impl;

import com.demo.weatherdataapp.client.WeatherApiClient;
import com.demo.weatherdataapp.config.WeatherApiConfig;
import com.demo.weatherdataapp.dto.OpenWeatherResponse;
import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.dto.WeatherResponse;
import com.demo.weatherdataapp.entity.WeatherData;
import com.demo.weatherdataapp.exception.InvalidWeatherRequestException;
import com.demo.weatherdataapp.exception.WeatherApiException;
import com.demo.weatherdataapp.repository.WeatherDataRepository;
import com.demo.weatherdataapp.service.WeatherCacheService;
import com.demo.weatherdataapp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private static final String DEFAULT_UNITS = "metric";
    private static final String DEFAULT_COUNTRY = "US";

    private final WeatherApiClient weatherApiClient;
    private final WeatherApiConfig weatherApiConfig;
    private final WeatherCacheService cacheService;
    private final WeatherDataRepository weatherDataRepository;

    @Override
    @Transactional
    public WeatherResponse getWeather(WeatherRequest request) {
        log.info("Getting weather data for request: {}", request);

        validateRequest(request);

        Optional<WeatherData> cachedData = cacheService.getCachedWeatherData(request);
        if (cachedData.isPresent() && cacheService.isCacheValid(cachedData.get())) {
            log.info("Returning cached weather data for: {}", getRequestIdentifier(request));

            return mapToResponse(cachedData.get());
        }

        WeatherData freshData = fetchAndSaveWeatherData(request);

        return mapToResponse(freshData);
    }

    @Override
    public OpenWeatherResponse fetchWeatherData(WeatherRequest request) {
        validateRequest(request);

        try {
            if (StringUtils.hasText(request.getCity())) {
                return fetchByCityName(request.getCity());
            }

            return fetchByZipCode(request.getZipCode(), request.getCountry());
        } catch (Exception e) {
            throw new WeatherApiException("Failed to fetch weather data for: " + request, e);
        }
    }

    @Override
    @Transactional
    public WeatherData fetchAndSaveWeatherData(WeatherRequest request) {
        log.info("Fetching fresh weather data for: {}", getRequestIdentifier(request));

        OpenWeatherResponse apiResponse = fetchWeatherData(request);
        WeatherData weatherData = mapFromApiResponse(apiResponse, request.getZipCode());

        return cacheService.saveToCache(weatherData);
    }

    @Override
    public WeatherResponse getWeatherByCity(String cityName) {
        WeatherRequest request = new WeatherRequest();
        request.setCity(cityName);

        return getWeather(request);
    }

    @Override
    public WeatherResponse getWeatherByZipCode(String zipCode, String country) {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode(zipCode);
        request.setCountry(country);

        return getWeather(request);
    }

    @Override
    public WeatherResponse refreshWeatherByCity(String cityName) {
        WeatherRequest request = new WeatherRequest();
        request.setCity(cityName);
        WeatherData freshData = fetchAndSaveWeatherData(request);

        return mapToResponse(freshData);
    }

    @Override
    public WeatherResponse refreshWeatherByZipCode(String zipCode, String country) {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode(zipCode);
        request.setCountry(country);
        WeatherData freshData = fetchAndSaveWeatherData(request);

        return mapToResponse(freshData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTrackedCities() {
        return weatherDataRepository.findDistinctCities();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTrackedZipCodes() {
        return weatherDataRepository.findDistinctZipCodes();
    }

    @Override
    @Transactional
    public void refreshWeatherDataForCity(String city) {
        WeatherRequest request = new WeatherRequest();
        request.setCity(city);
        fetchAndSaveWeatherData(request);

        log.info("Refreshed weather data for city: {}", city);
    }

    @Override
    @Transactional
    public void refreshWeatherDataForZipCode(String zipCode) {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode(zipCode);
        fetchAndSaveWeatherData(request);

        log.info("Refreshed weather data for zip code: {}", zipCode);
    }

    private void validateRequest(WeatherRequest request) {
        if (request == null) {
            throw new InvalidWeatherRequestException("Weather request cannot be null");
        }

        boolean hasCityName = StringUtils.hasText(request.getCity());
        boolean hasZipCode = StringUtils.hasText(request.getZipCode());

        if (!hasCityName && !hasZipCode) {
            throw new InvalidWeatherRequestException("Either city name or zip code must be provided");
        }
    }

    private OpenWeatherResponse fetchByCityName(String cityName) {
        log.info("Fetching weather data from API for city: {}", cityName);

        return weatherApiClient.getWeatherByCity(cityName.trim(), weatherApiConfig.getApiKey(), DEFAULT_UNITS);
    }

    private OpenWeatherResponse fetchByZipCode(String zipCode, String country) {
        String zipQuery = formatZipCode(zipCode, country);
        log.info("Fetching weather data from API for zip code: {}", zipQuery);

        return weatherApiClient.getWeatherByZipCode(zipQuery, weatherApiConfig.getApiKey(), DEFAULT_UNITS);
    }

    private String formatZipCode(String zipCode, String country) {
        if (!StringUtils.hasText(zipCode)) {
            throw new InvalidWeatherRequestException("Zip code cannot be empty");
        }

        String cleanZipCode = zipCode.trim();
        String targetCountry = StringUtils.hasText(country) ? country.trim().toUpperCase() : DEFAULT_COUNTRY;

        return String.format(cleanZipCode, targetCountry);
    }

    private WeatherData mapFromApiResponse(OpenWeatherResponse response, String originalZipCode) {
        return WeatherData.builder()
                .city(response.getName())
                .country(response.getSys().getCountry())
                .zipCode(originalZipCode)
                .temperature(response.getMain().getTemp())
                .feelsLike(response.getMain().getFeelsLike())
                .humidity(response.getMain().getHumidity())
                .pressure(response.getMain().getPressure())
                .description(!response.getWeather().isEmpty() ? response.getWeather().getFirst().getDescription() : null)
                .mainWeather(!response.getWeather().isEmpty() ? response.getWeather().getFirst().getMain() : null)
                .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : null)
                .windDirection(response.getWind() != null ? response.getWind().getDeg() : null)
                .timestamp(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private WeatherResponse mapToResponse(WeatherData data) {
        return WeatherResponse.builder()
                .city(data.getCity())
                .country(data.getCountry())
                .zipCode(data.getZipCode())
                .temperature(data.getTemperature())
                .feelsLike(data.getFeelsLike())
                .humidity(data.getHumidity())
                .pressure(data.getPressure())
                .description(data.getDescription())
                .mainWeather(data.getMainWeather())
                .windSpeed(data.getWindSpeed())
                .windDirection(data.getWindDirection())
                .lastUpdated(data.getLastUpdated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    private String getRequestIdentifier(WeatherRequest request) {
        return StringUtils.hasText(request.getCity()) ? request.getCity() : request.getZipCode();
    }
}
