package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FlightController.class)
class FlightControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FlightService flightService;

    @Test
    void searchFlights_ValidRequest_ReturnsOk() throws Exception {
        when(flightService.searchFlights(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/flights")
                        .param("origin", "NYC")
                        .param("destination", "LAX")
                        .param("departureDate", "2024-12-01")
                        .param("adults", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void searchFlights_InvalidOrigin_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .param("origin", "N") // Invalid: too short
                        .param("destination", "LAX")
                        .param("departureDate", "2024-12-01")
                        .param("adults", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Invalid input parameters"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void searchFlights_InvalidDate_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .param("origin", "NYC")
                        .param("destination", "LAX")
                        .param("departureDate", "invalid-date")
                        .param("adults", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.message").value("Invalid input parameters"));
    }

    @Test
    void searchFlights_InvalidAdults_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .param("origin", "NYC")
                        .param("destination", "LAX")
                        .param("departureDate", "2024-12-01")
                        .param("adults", "0")) // Invalid: must be at least 1
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    void searchFlights_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .param("adults", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }
}