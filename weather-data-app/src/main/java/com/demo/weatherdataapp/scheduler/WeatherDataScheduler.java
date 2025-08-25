package com.demo.weatherdataapp.scheduler;

import com.demo.weatherdataapp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherDataScheduler {
    private final WeatherService weatherService;

    @Scheduled(fixedRate = 1800000)
    public void refreshAllWeatherData() {
        log.info("Starting scheduled refresh of all weather data");

        try {
            List<String> cities = weatherService.getAllTrackedCities();
            List<String> zipCodes = weatherService.getAllTrackedZipCodes();

            log.info("Found {} cities and {} zip codes to refresh", cities.size(), zipCodes.size());

            refreshCities(cities);
            refreshZipCodes(zipCodes);

            log.info("Completed scheduled refresh of weather data");
        } catch (Exception e) {
            log.error("Error during scheduled weather data refresh", e);
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void systemHealthCheck() {
        log.debug("Performing system health check");

        try {
            List<String> cities = weatherService.getAllTrackedCities();

            log.debug("System health check: Database accessible, tracking {} cities", cities.size());
        } catch (Exception e) {
            log.error("System health check failed", e);
        }
    }

    private void refreshCities(List<String> cities) {
        cities.parallelStream().forEach(city -> {
            try {
                weatherService.refreshWeatherDataForCity(city);
            } catch (Exception e) {
                log.error("Error refreshing data for city: {}", city, e);
            }
        });
    }

    private void refreshZipCodes(List<String> zipCodes) {
        zipCodes.parallelStream().forEach(zip -> {
            try {
                weatherService.refreshWeatherDataForZipCode(zip);
            } catch (Exception e) {
                log.error("Error refreshing data for zip code: {}", zip, e);
            }
        });
    }
}
