package com.demo.weatherdataapp.service.impl;

import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.entity.WeatherData;
import com.demo.weatherdataapp.repository.WeatherDataRepository;
import com.demo.weatherdataapp.service.WeatherCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherCacheServiceImpl implements WeatherCacheService {
    private final WeatherDataRepository weatherDataRepository;

    @Value("${weather.cache.validity-minutes:30}")
    private int cacheValidityMinutes;

    @Override
    public Optional<WeatherData> getCachedWeatherData(WeatherRequest request) {
        if (StringUtils.hasText(request.getCity())) {
            return getCachedDataByCity(request.getCity());
        }

        if (StringUtils.hasText(request.getZipCode())) {
            return getCachedDataByZipCode(request.getZipCode(), request.getCountry());
        }

        log.warn("Weather request contains neither city nor zip code: {}", request);

        return Optional.empty();
    }

    @Override
    public boolean isCacheValid(WeatherData data) {
        if (data == null || data.getLastUpdated() == null) {
            return false;
        }

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(cacheValidityMinutes);
        boolean isValid = data.getLastUpdated().isAfter(threshold);

        log.debug("Cache validation for {}: {} (last updated: {})", data.getCity(), isValid ? "VALID" : "EXPIRED", data.getLastUpdated());

        return isValid;
    }

    @Override
    public WeatherData saveToCache(WeatherData weatherData) {
        weatherData.setLastUpdated(LocalDateTime.now());
        WeatherData saved = weatherDataRepository.save(weatherData);

        log.info("Cached weather data for: {} (ID: {})", saved.getCity(), saved.getId());

        return saved;
    }

    @Override
    public void cleanupExpiredCache() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(cacheValidityMinutes * 2L);

        try {
            List<WeatherData> expiredData = weatherDataRepository.findDataOlderThan(threshold);
            if (!expiredData.isEmpty()) {
                weatherDataRepository.deleteAll(expiredData);
                log.info("Cleaned up {} expired cache entries", expiredData.size());
            }
        } catch (Exception e) {
            log.error("Error during cache cleanup", e);
        }
    }

    private Optional<WeatherData> getCachedDataByCity(String city) {
        try {
            return weatherDataRepository.findLatestByCityIgnoreCase(city);
        } catch (Exception e) {
            log.error("Error fetching cached data for city: {}", city, e);

            return Optional.empty();
        }
    }

    private Optional<WeatherData> getCachedDataByZipCode(String zipCode, String country) {
        try {
            String zipQuery = formatZipCode(zipCode, country);

            return weatherDataRepository.findLatestByZipCode(zipQuery);
        } catch (Exception e) {
            log.error("Error fetching cached data for zip: {}", zipCode, e);

            return Optional.empty();
        }
    }

    private String formatZipCode(String zipCode, String country) {
        return StringUtils.hasText(country) ? zipCode + "," + country : zipCode + ",US";
    }
}
