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
public class FlightSearchRequest {
    @NotBlank(message = "Origin is required")
    @Size(min = 3, max = 3, message = "Origin must be a 3-letter IATA code")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(min = 3, max = 3, message = "Destination must be a 3-letter IATA code")
    private String destination;

    @NotBlank(message = "Departure date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Departure date must be in yyyy-MM-dd format")
    private String departureDate;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Return date must be in yyyy-MM-dd format")
    private String returnDate;

    @Min(value = 1, message = "Adults must be at least 1")
    private int adults = 1;
}
