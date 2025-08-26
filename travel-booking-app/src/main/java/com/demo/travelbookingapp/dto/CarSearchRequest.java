package com.demo.travelbookingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarSearchRequest {
    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    private String dropoffLocation;

    @NotBlank(message = "Pickup date time is required")
    private String pickupDateTime;

    @NotBlank(message = "Dropoff date time is required")
    private String dropoffDateTime;

    @Min(value = 18, message = "Driver age must be at least 18")
    private Integer driverAge;
}
