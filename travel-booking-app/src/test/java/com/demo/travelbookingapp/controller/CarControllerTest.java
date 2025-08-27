package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.service.CarService;
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

@WebMvcTest(CarController.class)
class CarControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CarService carService;

    @Test
    void searchCars_ValidRequest_ReturnsOk() throws Exception {
        when(carService.searchCars(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars")
                        .param("pickupLocation", "NYC")
                        .param("pickupDateTime", "2024-12-01T10:00:00")
                        .param("dropoffDateTime", "2024-12-05T10:00:00")
                        .param("driverAge", "25"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void searchCars_WithDropoffLocation_ReturnsOk() throws Exception {
        when(carService.searchCars(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars")
                        .param("pickupLocation", "NYC")
                        .param("dropoffLocation", "LAX")
                        .param("pickupDateTime", "2024-12-01T10:00:00")
                        .param("dropoffDateTime", "2024-12-05T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void searchCars_InvalidDriverAge_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("pickupLocation", "NYC")
                        .param("pickupDateTime", "2024-12-01T10:00:00")
                        .param("dropoffDateTime", "2024-12-05T10:00:00")
                        .param("driverAge", "16")) // Invalid: must be at least 18
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.message").value("Invalid input parameters"));
    }

    @Test
    void searchCars_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    void searchCars_EmptyPickupLocation_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("pickupLocation", "") // Invalid: empty
                        .param("pickupDateTime", "2024-12-01T10:00:00")
                        .param("dropoffDateTime", "2024-12-05T10:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));
    }
}