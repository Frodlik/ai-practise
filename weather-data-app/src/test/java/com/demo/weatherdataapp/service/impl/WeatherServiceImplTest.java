package com.demo.weatherdataapp.service.impl;

import com.demo.weatherdataapp.client.WeatherApiClient;
import com.demo.weatherdataapp.config.WeatherApiConfig;
import com.demo.weatherdataapp.dto.OpenWeatherResponse;
import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.dto.WeatherResponse;
import com.demo.weatherdataapp.entity.WeatherData;
import com.demo.weatherdataapp.exception.InvalidWeatherRequestException;
import com.demo.weatherdataapp.repository.WeatherDataRepository;
import com.demo.weatherdataapp.service.WeatherCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {
    @Mock
    private WeatherApiClient weatherApiClient;
    @Mock
    private WeatherApiConfig weatherApiConfig;
    @Mock
    private WeatherCacheService cacheService;
    @Mock
    private WeatherDataRepository weatherDataRepository;
    @InjectMocks
    private WeatherServiceImpl service;

    @Test
    void testGivenCityNameAndValidCachedData_whenGetWeather_thenReturnCachedWeatherResponse() {
        WeatherRequest cityRequest = createCityRequest();
        WeatherData cachedData = createMockWeatherData();

        when(cacheService.getCachedWeatherData(cityRequest)).thenReturn(Optional.of(cachedData));
        when(cacheService.isCacheValid(cachedData)).thenReturn(true);

        WeatherResponse actual = service.getWeather(cityRequest);

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        verify(cacheService).getCachedWeatherData(cityRequest);
        verify(cacheService).isCacheValid(cachedData);
        verifyNoInteractions(weatherApiClient);
    }

    @Test
    void testGivenCityNameAndNoCachedData_whenGetWeather_thenFetchAndSaveWeatherData() {
        WeatherRequest cityRequest = createCityRequest();
        OpenWeatherResponse apiResponse = createMockApiResponse();
        WeatherData savedData = createMockWeatherData();

        when(weatherApiConfig.getApiKey()).thenReturn("test-api-key");
        when(cacheService.getCachedWeatherData(cityRequest)).thenReturn(Optional.empty());
        when(weatherApiClient.getWeatherByCity("London", "test-api-key", "metric"))
                .thenReturn(apiResponse);
        when(cacheService.saveToCache(any(WeatherData.class))).thenReturn(savedData);

        WeatherResponse actual = service.getWeather(cityRequest);

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        verify(cacheService).getCachedWeatherData(cityRequest);
        verify(weatherApiClient).getWeatherByCity("London", "test-api-key", "metric");
        verify(cacheService).saveToCache(any(WeatherData.class));
    }

    @Test
    void testGivenInvalidRequest_whenGetWeather_thenThrowInvalidWeatherRequestException() {
        WeatherRequest invalidRequest = createEmptyRequest();

        assertThrows(InvalidWeatherRequestException.class, () -> service.getWeather(invalidRequest));
        verifyNoInteractions(cacheService, weatherApiClient);
    }

    @Test
    void testGivenNullRequest_whenGetWeather_thenThrowInvalidWeatherRequestException() {
        assertThrows(InvalidWeatherRequestException.class, () -> service.getWeather(null));
        verifyNoInteractions(cacheService, weatherApiClient);
    }

    @Test
    void testGivenCityName_whenFetchWeatherData_thenReturnOpenWeatherResponse() throws Exception {
        WeatherRequest cityRequest = createCityRequest();
        OpenWeatherResponse apiResponse = createMockApiResponse();
        Method fetchMethod = WeatherServiceImpl.class.getDeclaredMethod("fetchWeatherData", WeatherRequest.class);
        fetchMethod.setAccessible(true);

        when(weatherApiConfig.getApiKey()).thenReturn("test-api-key");
        when(weatherApiClient.getWeatherByCity("London", "test-api-key", "metric")).thenReturn(apiResponse);

        OpenWeatherResponse actual = (OpenWeatherResponse) fetchMethod.invoke(service, cityRequest);

        assertNotNull(actual);
        assertEquals("London", actual.getName());
        verify(weatherApiClient).getWeatherByCity("London", "test-api-key", "metric");
    }

    @Test
    void testGivenInvalidRequest_whenFetchWeatherData_thenThrowInvalidWeatherRequestException() throws Exception {
        WeatherRequest invalidRequest = createEmptyRequest();

        Method fetchMethod = WeatherServiceImpl.class.getDeclaredMethod("fetchWeatherData", WeatherRequest.class);
        fetchMethod.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> fetchMethod.invoke(service, invalidRequest));

        assertInstanceOf(InvalidWeatherRequestException.class, exception.getCause());
        verifyNoInteractions(weatherApiClient);
    }

    @Test
    void testGivenCityName_whenRefreshWeatherByCity_thenFetchAndSaveWeatherData() {
        OpenWeatherResponse apiResponse = createMockApiResponse();
        WeatherData savedData = createMockWeatherData();

        when(weatherApiConfig.getApiKey()).thenReturn("test-api-key");
        when(weatherApiClient.getWeatherByCity("London", "test-api-key", "metric")).thenReturn(apiResponse);
        when(cacheService.saveToCache(any(WeatherData.class))).thenReturn(savedData);

        WeatherResponse actual = service.refreshWeatherByCity("London");

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        verify(weatherApiClient).getWeatherByCity("London", "test-api-key", "metric");
        verify(cacheService).saveToCache(any(WeatherData.class));
    }

    @Test
    void testWhenGetAllTrackedCities_thenReturnListOfCities() {
        List<String> trackedCities = Arrays.asList("London", "New York");
        when(weatherDataRepository.findDistinctCities()).thenReturn(trackedCities);

        List<String> actual = service.getAllTrackedCities();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(actual.contains("London"));
        assertTrue(actual.contains("New York"));
        verify(weatherDataRepository).findDistinctCities();
    }

    @Test
    void testGivenCityName_whenGetWeatherByCity_thenDelegateToGetWeather() {
        WeatherData cachedData = createMockWeatherData();

        when(cacheService.getCachedWeatherData(any())).thenReturn(Optional.of(cachedData));
        when(cacheService.isCacheValid(cachedData)).thenReturn(true);

        WeatherResponse actual = service.getWeatherByCity("London");

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        verify(cacheService).getCachedWeatherData(any(WeatherRequest.class));
    }

    @Test
    void testGivenZipCode_whenGetWeatherByZipCode_thenDelegateToGetWeather() {
        WeatherData cachedData = createMockWeatherData();

        when(cacheService.getCachedWeatherData(any())).thenReturn(Optional.of(cachedData));
        when(cacheService.isCacheValid(cachedData)).thenReturn(true);

        WeatherResponse actual = service.getWeatherByZipCode("94040", "US");

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        verify(cacheService).getCachedWeatherData(any(WeatherRequest.class));
    }

    @Test
    void testValidateRequest_whenValidCityRequest_thenNoException() throws Exception {
        WeatherRequest validRequest = createCityRequest();

        Method validateMethod = WeatherServiceImpl.class.getDeclaredMethod("validateRequest", WeatherRequest.class);
        validateMethod.setAccessible(true);

        assertDoesNotThrow(() -> validateMethod.invoke(service, validRequest));
    }

    @Test
    void testValidateRequest_whenNullRequest_thenThrowException() throws Exception {
        Method validateMethod = WeatherServiceImpl.class.getDeclaredMethod("validateRequest", WeatherRequest.class);
        validateMethod.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> validateMethod.invoke(service, (WeatherRequest) null));

        assertInstanceOf(InvalidWeatherRequestException.class, exception.getCause());
    }

    @Test
    void testConvertToWeatherData_whenValidApiResponse_thenReturnWeatherData() throws Exception {
        OpenWeatherResponse apiResponse = createMockApiResponse();
        String zipCode = "12345";

        Method mapMethod = WeatherServiceImpl.class.getDeclaredMethod("mapFromApiResponse", OpenWeatherResponse.class, String.class);
        mapMethod.setAccessible(true);

        WeatherData actual = (WeatherData) mapMethod.invoke(service, apiResponse, zipCode);

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        assertEquals("GB", actual.getCountry());
        assertEquals(20.5, actual.getTemperature());
        assertEquals(18.3, actual.getFeelsLike());
        assertEquals(65, actual.getHumidity());
        assertEquals(1013, actual.getPressure());
        assertEquals("Clear sky", actual.getDescription());
        assertEquals("Clear", actual.getMainWeather());
        assertEquals(3.2, actual.getWindSpeed());
        assertEquals(180, actual.getWindDirection());
    }

    @Test
    void testConvertToWeatherResponse_whenValidWeatherData_thenReturnWeatherResponse() throws Exception {
        WeatherData weatherData = createMockWeatherData();

        Method mapMethod = WeatherServiceImpl.class.getDeclaredMethod("mapToResponse", WeatherData.class);
        mapMethod.setAccessible(true);

        WeatherResponse actual = (WeatherResponse) mapMethod.invoke(service, weatherData);

        assertNotNull(actual);
        assertEquals("London", actual.getCity());
        assertEquals("GB", actual.getCountry());
        assertEquals(20.5, actual.getTemperature());
        assertEquals(18.3, actual.getFeelsLike());
        assertEquals(65, actual.getHumidity());
        assertEquals(1013, actual.getPressure());
        assertEquals("Clear sky", actual.getDescription());
        assertEquals("Clear", actual.getMainWeather());
        assertEquals(3.2, actual.getWindSpeed());
        assertEquals(180, actual.getWindDirection());
    }

    private WeatherRequest createCityRequest() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("London");

        return request;
    }

    private WeatherRequest createEmptyRequest() {
        return new WeatherRequest();
    }

    private WeatherData createMockWeatherData() {
        return WeatherData.builder()
                .id(1L)
                .city("London")
                .country("GB")
                .temperature(20.5)
                .feelsLike(18.3)
                .humidity(65)
                .pressure(1013)
                .description("Clear sky")
                .mainWeather("Clear")
                .windSpeed(3.2)
                .windDirection(180)
                .timestamp(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private OpenWeatherResponse createMockApiResponse() {
        return createMockApiResponseForCity();
    }

    private OpenWeatherResponse createMockApiResponseForCity() {
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setName("London");

        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(20.5);
        main.setFeelsLike(18.3);
        main.setHumidity(65);
        main.setPressure(1013);
        response.setMain(main);

        OpenWeatherResponse.Sys sys = new OpenWeatherResponse.Sys();
        sys.setCountry("GB");
        response.setSys(sys);

        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setDescription("Clear sky");
        weather.setMain("Clear");
        response.setWeather(List.of(weather));

        OpenWeatherResponse.Wind wind = new OpenWeatherResponse.Wind();
        wind.setSpeed(3.2);
        wind.setDeg(180);
        response.setWind(wind);

        return response;
    }
}
