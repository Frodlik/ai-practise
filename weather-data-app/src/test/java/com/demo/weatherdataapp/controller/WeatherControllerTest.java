package com.demo.weatherdataapp.controller;

import com.demo.weatherdataapp.dto.WeatherRequest;
import com.demo.weatherdataapp.dto.WeatherResponse;
import com.demo.weatherdataapp.service.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
@AutoConfigureMockMvc
class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private WeatherService weatherService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetWeatherByCity_whenValidCity_thenReturnWeatherResponse() throws Exception {
        String cityName = "London";
        WeatherResponse mockResponse = createWeatherResponse(cityName);

        when(weatherService.getWeatherByCity(cityName)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/weather/city/{cityName}", cityName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("London"))
                .andExpect(jsonPath("$.temperature").value(20.5))
                .andExpect(jsonPath("$.humidity").value(65));

        verify(weatherService).getWeatherByCity(cityName);
    }

    @Test
    void testGetWeatherByZipCode_whenValidZipCode_thenReturnWeatherResponse() throws Exception {
        String zipCode = "94040";
        String country = "US";
        WeatherResponse mockResponse = createWeatherResponse("Mountain View");

        when(weatherService.getWeatherByZipCode(zipCode, country)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/weather/zip/{zipCode}", zipCode)
                        .param("country", country))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("Mountain View"));

        verify(weatherService).getWeatherByZipCode(zipCode, country);
    }

    @Test
    void testGetWeatherByZipCode_whenNoCountryProvided_thenUseDefault() throws Exception {
        String zipCode = "94040";
        WeatherResponse mockResponse = createWeatherResponse("Mountain View");

        when(weatherService.getWeatherByZipCode(zipCode, null)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/weather/zip/{zipCode}", zipCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Mountain View"));

        verify(weatherService).getWeatherByZipCode(zipCode, null);
    }

    @Test
    void testSearchWeather_whenValidCityRequest_thenReturnWeatherResponse() throws Exception {
        WeatherRequest request = new WeatherRequest();
        request.setCity("London");
        WeatherResponse mockResponse = createWeatherResponse("London");

        when(weatherService.getWeather(any(WeatherRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/weather/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("London"));

        verify(weatherService).getWeather(any(WeatherRequest.class));
    }

    @Test
    void testSearchWeather_whenValidZipRequest_thenReturnWeatherResponse() throws Exception {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode("94040");
        request.setCountry("US");
        WeatherResponse mockResponse = createWeatherResponse("Mountain View");

        when(weatherService.getWeather(any(WeatherRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/weather/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Mountain View"));
    }

    @Test
    void testSearchWeather_whenInvalidRequest_thenBadRequest() throws Exception {
        WeatherRequest request = new WeatherRequest();

        mockMvc.perform(post("/api/weather/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshZipCodeWeather_whenValidZipCode_thenReturnUpdatedWeather() throws Exception {
        String zipCode = "94040";
        String country = "US";
        WeatherResponse mockResponse = createWeatherResponse("Mountain View");

        when(weatherService.refreshWeatherByZipCode(zipCode, country)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/weather/refresh/zip/{zipCode}", zipCode)
                        .param("country", country))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Mountain View"));

        verify(weatherService).refreshWeatherByZipCode(zipCode, country);
    }

    @Test
    void testGetTrackedCities_whenCitiesExist_thenReturnList() throws Exception {
        List<String> cities = Arrays.asList("London", "Paris", "New York");

        when(weatherService.getAllTrackedCities()).thenReturn(cities);

        mockMvc.perform(get("/api/weather/tracked/cities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]").value("London"))
                .andExpect(jsonPath("$[1]").value("Paris"))
                .andExpect(jsonPath("$[2]").value("New York"));

        verify(weatherService).getAllTrackedCities();
    }

    @Test
    void testGetTrackedCities_whenNoCities_thenReturnEmptyList() throws Exception {
        when(weatherService.getAllTrackedCities()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/weather/tracked/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetTrackedZipCodes_whenZipCodesExist_thenReturnList() throws Exception {
        List<String> zipCodes = Arrays.asList("94040", "10001", "75001");

        when(weatherService.getAllTrackedZipCodes()).thenReturn(zipCodes);

        mockMvc.perform(get("/api/weather/tracked/zipcodes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]").value("94040"))
                .andExpect(jsonPath("$[1]").value("10001"))
                .andExpect(jsonPath("$[2]").value("75001"));

        verify(weatherService).getAllTrackedZipCodes();
    }

    @Test
    void testHealthCheck_whenCalled_thenReturnOk() throws Exception {
        mockMvc.perform(get("/api/weather/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Weather service is running"));
    }

    private WeatherResponse createWeatherResponse(String cityName) {
        return WeatherResponse.builder()
                .city(cityName)
                .country("GB")
                .temperature(20.5)
                .feelsLike(18.3)
                .humidity(65)
                .pressure(1013)
                .description("Clear sky")
                .mainWeather("Clear")
                .windSpeed(3.2)
                .windDirection(180)
                .lastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}