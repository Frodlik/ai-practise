package com.demo.travelbookingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchRequest {
    @NotBlank(message = "City code is required")
    @Size(min = 3, max = 3, message = "City code must be a 3-letter IATA code")
    private String cityCode;

    @NotBlank(message = "Check-in date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Check-in date must be in yyyy-MM-dd format")
    private String checkIn;

    @NotBlank(message = "Check-out date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Check-out date must be in yyyy-MM-dd format")
    private String checkOut;

    @Min(value = 1, message = "Adults must be at least 1")
    private int adults = 1;
}
