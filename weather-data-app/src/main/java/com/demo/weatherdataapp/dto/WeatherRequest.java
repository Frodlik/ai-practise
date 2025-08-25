package com.demo.weatherdataapp.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WeatherRequest {
    private static final String ZIP_CODE_REGEX = "^\\d{5}(-\\d{4})?$|^[A-Z]{1,3}\\d[A-Z0-9]\\s?\\d[A-Z]{2}$";
    private static final String COUNTRY_REGEX = "^[A-Z]{2}$";

    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
    private String city;

    @Pattern(regexp = ZIP_CODE_REGEX,
            message = "Invalid zip code format. Use 5 digits (US) or postal code format")
    private String zipCode;

    @Pattern(regexp = COUNTRY_REGEX, message = "Country code must be 2 uppercase letters (e.g., US, UA)")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    private String country;

    @AssertTrue(message = "Either city or zipCode must be provided")
    public boolean isValid() {
        return (city != null && !city.trim().isEmpty()) ||
                (zipCode != null && !zipCode.trim().isEmpty());
    }
}
