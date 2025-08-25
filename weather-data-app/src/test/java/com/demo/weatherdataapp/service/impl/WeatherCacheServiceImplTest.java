package com.demo.weatherdataapp.service.impl;

import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.entity.WeatherData;
import com.demo.weatherdataapp.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherCacheServiceImplTest {
    @Mock
    private WeatherDataRepository weatherDataRepository;
    @InjectMocks
    private WeatherCacheServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "cacheValidityMinutes", 30);
    }

    @Test
    void givenCityName_whenGetCachedWeatherData_thenReturnCachedWeatherData() {
        String city = "London";
        WeatherRequest request = new WeatherRequest();
        request.setCity(city);
        WeatherData mockWeatherData = WeatherData.builder()
                .city(city)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(weatherDataRepository.findLatestByCityIgnoreCase(city)).thenReturn(Optional.of(mockWeatherData));

        Optional<WeatherData> actual = service.getCachedWeatherData(request);

        assertTrue(actual.isPresent());
        assertEquals(city, actual.get().getCity());
        verify(weatherDataRepository, times(1)).findLatestByCityIgnoreCase(city);
    }

    @Test
    void testGivenZipCode_whenGetCachedWeatherData_thenReturnCachedWeatherData() {
        String zipCode = "94040";
        String country = "US";
        String expectedZipQuery = zipCode + "," + country;

        WeatherRequest request = new WeatherRequest();
        request.setZipCode(zipCode);
        request.setCountry(country);

        WeatherData mockWeatherData = WeatherData.builder()
                .zipCode(expectedZipQuery)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(weatherDataRepository.findLatestByZipCode(expectedZipQuery)).thenReturn(Optional.of(mockWeatherData));

        Optional<WeatherData> actual = service.getCachedWeatherData(request);

        assertTrue(actual.isPresent());
        assertEquals(expectedZipQuery, actual.get().getZipCode());
        verify(weatherDataRepository, times(1)).findLatestByZipCode(expectedZipQuery);
    }

    @Test
    void testGivenZipCodeWithoutCountry_whenGetCachedWeatherData_thenUseDefaultCountry() {
        String zipCode = "10001";
        String expectedZipQuery = zipCode + ",US";
        WeatherRequest request = new WeatherRequest();
        request.setZipCode(zipCode);
        WeatherData mockWeatherData = WeatherData.builder()
                .zipCode(expectedZipQuery)
                .build();

        when(weatherDataRepository.findLatestByZipCode(expectedZipQuery)).thenReturn(Optional.of(mockWeatherData));

        Optional<WeatherData> actual = service.getCachedWeatherData(request);

        assertTrue(actual.isPresent());
        verify(weatherDataRepository, times(1)).findLatestByZipCode(expectedZipQuery);
    }

    @Test
    void testGivenNoCityOrZipCode_whenGetCachedWeatherData_thenReturnEmptyOptional() {
        WeatherRequest request = new WeatherRequest();

        Optional<WeatherData> actual = service.getCachedWeatherData(request);

        assertTrue(actual.isEmpty());
        verify(weatherDataRepository, never()).findLatestByCityIgnoreCase(anyString());
        verify(weatherDataRepository, never()).findLatestByZipCode(anyString());
    }

    @Test
    void testGivenRecentWeatherData_whenIsCacheValid_thenReturnTrue() {
        WeatherData weatherData = WeatherData.builder()
                .lastUpdated(LocalDateTime.now().minusMinutes(10))
                .build();

        boolean isValid = service.isCacheValid(weatherData);

        assertTrue(isValid);
    }

    @Test
    void testGivenExpiredWeatherData_whenIsCacheValid_thenReturnFalse() {
        WeatherData weatherData = WeatherData.builder()
                .lastUpdated(LocalDateTime.now().minusMinutes(45))
                .build();

        boolean isValid = service.isCacheValid(weatherData);

        assertFalse(isValid);
    }

    @Test
    void testGivenWeatherDataWithNullLastUpdated_whenIsCacheValid_thenReturnFalse() {
        WeatherData weatherData = WeatherData.builder()
                .lastUpdated(null)
                .build();

        boolean isValid = service.isCacheValid(weatherData);

        assertFalse(isValid);
    }

    @Test
    void testGivenNullWeatherData_whenIsCacheValid_thenReturnFalse() {
        WeatherData weatherData = null;

        boolean isValid = service.isCacheValid(weatherData);

        assertFalse(isValid);
    }

    @Test
    void testGivenWeatherData_whenSaveToCache_thenSaveWeatherDataWithUpdatedTimestamp() {
        WeatherData inputWeatherData = WeatherData.builder()
                .city("London")
                .build();
        WeatherData savedWeatherData = WeatherData.builder()
                .id(1L)
                .city("London")
                .lastUpdated(LocalDateTime.now())
                .build();

        when(weatherDataRepository.save(any(WeatherData.class))).thenReturn(savedWeatherData);

        WeatherData actual = service.saveToCache(inputWeatherData);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("London", actual.getCity());
        assertNotNull(inputWeatherData.getLastUpdated());
        verify(weatherDataRepository, times(1)).save(any(WeatherData.class));
    }

    @Test
    void testGivenExpiredCache_whenCleanupExpiredCache_thenDeleteExpiredData() {
        List<WeatherData> expiredData = Arrays.asList(
                WeatherData.builder().id(1L).city("Old City 1").build(),
                WeatherData.builder().id(2L).city("Old City 2").build()
        );

        when(weatherDataRepository.findDataOlderThan(any(LocalDateTime.class))).thenReturn(expiredData);

        service.cleanupExpiredCache();

        verify(weatherDataRepository, times(1)).findDataOlderThan(any(LocalDateTime.class));
        verify(weatherDataRepository, times(1)).deleteAll(expiredData);
    }

    @Test
    void testGivenNoExpiredCache_whenCleanupExpiredCache_thenDoNothing() {
        when(weatherDataRepository.findDataOlderThan(any(LocalDateTime.class))).thenReturn(List.of());

        service.cleanupExpiredCache();

        verify(weatherDataRepository, times(1)).findDataOlderThan(any(LocalDateTime.class));
        verify(weatherDataRepository, never()).deleteAll(anyList());
    }

    @Test
    void testGivenRepositoryException_whenGetCachedDataByCity_thenReturnEmptyOptional() {
        String city = "London";
        WeatherRequest request = new WeatherRequest();
        request.setCity(city);

        when(weatherDataRepository.findLatestByCityIgnoreCase(city)).thenThrow(new RuntimeException("Database error"));

        Optional<WeatherData> actual = service.getCachedWeatherData(request);

        assertTrue(actual.isEmpty());
        verify(weatherDataRepository, times(1)).findLatestByCityIgnoreCase(city);
    }

    @Test
    void testGivenRepositoryException_whenGetCachedDataByZipCode_thenReturnEmptyOptional() {
        String zipCode = "94040";
        String country = "US";
        WeatherRequest request = new WeatherRequest();
        request.setZipCode(zipCode);
        request.setCountry(country);

        when(weatherDataRepository.findLatestByZipCode(zipCode + "," + country)).thenThrow(new RuntimeException("Database error"));

        Optional<WeatherData> actual = service.getCachedWeatherData(request);

        assertTrue(actual.isEmpty());
        verify(weatherDataRepository, times(1)).findLatestByZipCode(zipCode + "," + country);
    }
}