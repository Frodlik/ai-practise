package com.demo.weatherdataapp.scheduler;

import com.demo.weatherdataapp.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherDataSchedulerTest {
    @Mock
    private WeatherService weatherService;
    @InjectMocks
    private WeatherDataScheduler scheduler;

    @Test
    void testRefreshAllWeatherData_whenCitiesAndZipCodesExist_thenRefreshAll() {
        List<String> cities = Arrays.asList("London", "Paris", "New York");
        List<String> zipCodes = Arrays.asList("94040", "10001", "75001");

        when(weatherService.getAllTrackedCities()).thenReturn(cities);
        when(weatherService.getAllTrackedZipCodes()).thenReturn(zipCodes);

        scheduler.refreshAllWeatherData();

        verify(weatherService).getAllTrackedCities();
        verify(weatherService).getAllTrackedZipCodes();
        verify(weatherService).refreshWeatherDataForCity("London");
        verify(weatherService).refreshWeatherDataForCity("Paris");
        verify(weatherService).refreshWeatherDataForCity("New York");
        verify(weatherService).refreshWeatherDataForZipCode("94040");
        verify(weatherService).refreshWeatherDataForZipCode("10001");
        verify(weatherService).refreshWeatherDataForZipCode("75001");
    }

    @Test
    void testRefreshAllWeatherData_whenNoCitiesOrZipCodes_thenNoRefresh() {
        when(weatherService.getAllTrackedCities()).thenReturn(Collections.emptyList());
        when(weatherService.getAllTrackedZipCodes()).thenReturn(Collections.emptyList());

        scheduler.refreshAllWeatherData();

        verify(weatherService).getAllTrackedCities();
        verify(weatherService).getAllTrackedZipCodes();
        verify(weatherService, never()).refreshWeatherDataForCity(anyString());
        verify(weatherService, never()).refreshWeatherDataForZipCode(anyString());
    }

    @Test
    void testRefreshAllWeatherData_whenExceptionInGettingCities_thenHandleGracefully() {
        when(weatherService.getAllTrackedCities()).thenThrow(new RuntimeException("Database error"));

        assertDoesNotThrow(() -> scheduler.refreshAllWeatherData());

        verify(weatherService).getAllTrackedCities();
        verify(weatherService, never()).getAllTrackedZipCodes();
        verify(weatherService, never()).refreshWeatherDataForCity(anyString());
        verify(weatherService, never()).refreshWeatherDataForZipCode(anyString());
    }

    @Test
    void testRefreshAllWeatherData_whenExceptionInRefreshingCity_thenContinueWithOthers() {
        List<String> cities = Arrays.asList("London", "Paris", "Berlin");
        List<String> zipCodes = Collections.emptyList();

        when(weatherService.getAllTrackedCities()).thenReturn(cities);
        when(weatherService.getAllTrackedZipCodes()).thenReturn(zipCodes);
        doThrow(new RuntimeException("API error")).when(weatherService).refreshWeatherDataForCity("Paris");

        scheduler.refreshAllWeatherData();

        verify(weatherService).refreshWeatherDataForCity("London");
        verify(weatherService).refreshWeatherDataForCity("Paris");
        verify(weatherService).refreshWeatherDataForCity("Berlin");
    }

    @Test
    void testRefreshAllWeatherData_whenExceptionInRefreshingZipCode_thenContinueWithOthers() {
        List<String> cities = Collections.emptyList();
        List<String> zipCodes = Arrays.asList("94040", "10001", "75001");

        when(weatherService.getAllTrackedCities()).thenReturn(cities);
        when(weatherService.getAllTrackedZipCodes()).thenReturn(zipCodes);
        doThrow(new RuntimeException("API error")).when(weatherService).refreshWeatherDataForZipCode("10001");

        scheduler.refreshAllWeatherData();

        verify(weatherService).refreshWeatherDataForZipCode("94040");
        verify(weatherService).refreshWeatherDataForZipCode("10001");
        verify(weatherService).refreshWeatherDataForZipCode("75001");
    }

    @Test
    void testSystemHealthCheck_whenSuccessful_thenLogCitiesCount() {
        List<String> cities = Arrays.asList("London", "Paris");

        when(weatherService.getAllTrackedCities()).thenReturn(cities);

        scheduler.systemHealthCheck();

        verify(weatherService).getAllTrackedCities();
    }

    @Test
    void testSystemHealthCheck_whenException_thenHandleGracefully() {
        when(weatherService.getAllTrackedCities()).thenThrow(new RuntimeException("Database connection failed"));

        assertDoesNotThrow(() -> scheduler.systemHealthCheck());

        verify(weatherService).getAllTrackedCities();
    }
}