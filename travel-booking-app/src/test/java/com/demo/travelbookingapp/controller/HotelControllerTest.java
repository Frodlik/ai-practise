package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.service.HotelService;
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

@WebMvcTest(HotelController.class)
class HotelControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private HotelService hotelService;

    @Test
    void searchHotels_ValidRequest_ReturnsOk() throws Exception {
        when(hotelService.searchHotels(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/hotels")
                        .param("cityCode", "NYC")
                        .param("checkIn", "2024-12-01")
                        .param("checkOut", "2024-12-05")
                        .param("adults", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void searchHotels_InvalidCityCode_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/hotels")
                        .param("cityCode", "NY") // Invalid: too short
                        .param("checkIn", "2024-12-01")
                        .param("checkOut", "2024-12-05")
                        .param("adults", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.message").value("Invalid input parameters"));
    }

    @Test
    void searchHotels_InvalidDates_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/hotels")
                        .param("cityCode", "NYC")
                        .param("checkIn", "invalid-date")
                        .param("checkOut", "2024-12-05")
                        .param("adults", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    void searchHotels_InvalidAdults_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/hotels")
                        .param("cityCode", "NYC")
                        .param("checkIn", "2024-12-01")
                        .param("checkOut", "2024-12-05")
                        .param("adults", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    void searchHotels_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/hotels")
                        .param("adults", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }
}