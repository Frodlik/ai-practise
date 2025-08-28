package com.demo.travelbookingapp.provider.rapidapi;

import com.demo.travelbookingapp.dto.CarDto;
import com.demo.travelbookingapp.dto.CarSearchRequest;
import com.demo.travelbookingapp.provider.CarProviderClient;
import com.demo.travelbookingapp.provider.rapidapi.client.PricelineCarClient;
import com.demo.travelbookingapp.provider.rapidapi.dto.PricelineCarResponse;
import com.demo.travelbookingapp.provider.rapidapi.dto.PricelineLocationResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RapidApiCarProvider implements CarProviderClient {
    private final PricelineCarClient pricelineCarClient;

    @Value("${car-api.key}")
    private String apiKey;

    @Value("${car-api.host}")
    private String apiHost;

    @Override
    public List<CarDto> searchCars(CarSearchRequest request) {
        try {
            String pickupLocationId = getLocationId(request.getPickupLocation());

            String dropoffLocationId = request.getDropoffLocation() != null ?
                    getLocationId(request.getDropoffLocation()) : pickupLocationId;

            String[] pickupParts = formatDateTimeForPriceline(request.getPickupDateTime());
            String[] dropoffParts = formatDateTimeForPriceline(request.getDropoffDateTime());

            PricelineCarResponse response = pricelineCarClient.searchCars(
                    apiKey,
                    apiHost,
                    pickupParts[0],
                    pickupParts[1],
                    dropoffParts[0],
                    dropoffParts[1],
                    pickupLocationId,
                    dropoffLocationId,
                    "JFK",
                    request.getDropoffLocation()
            );

            return response.getResults().getCars().stream()
                    .map(car -> mapToCarDto(car, request))
                    .toList();
        } catch (FeignException e) {
            return List.of();
        } catch (Exception e) {
            log.error("Unexpected error during car search: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private String getLocationId(String locationName) {
        log.info("Getting location ID for: '{}'", locationName);

        try {
            List<PricelineLocationResponse.Location> locations = pricelineCarClient.searchLocations(
                    apiKey,
                    apiHost,
                    locationName
            );

            log.info("Location search returned {} results", locations != null ? locations.size() : 0);

            if (locations != null && !locations.isEmpty()) {
                PricelineLocationResponse.Location location = locations.getFirst();

                return location.getId();
            }

            return locationName;
        } catch (FeignException e) {
            return locationName;
        } catch (Exception e) {
            log.error("Error searching location '{}': {}", locationName, e.getMessage(), e);
            return locationName;
        }
    }

    private String[] formatDateTimeForPriceline(String isoDateTime) {
        try {
            LocalDateTime dateTime = getLocalDateTime(isoDateTime);

            String date = dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            return new String[]{date, time};
        } catch (DateTimeParseException e) {
            return new String[]{"09/01/2025", "10:00"};
        }
    }

    private static LocalDateTime getLocalDateTime(String isoDateTime) {
        LocalDateTime dateTime;

        if (isoDateTime.contains("T")) {
            String cleanDateTime = isoDateTime;
            if (cleanDateTime.contains("+")) {
                cleanDateTime = cleanDateTime.split("\\+")[0];
            }
            if (cleanDateTime.endsWith("Z")) {
                cleanDateTime = cleanDateTime.substring(0, cleanDateTime.length() - 1);
            }
            dateTime = LocalDateTime.parse(cleanDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            dateTime = LocalDateTime.parse(isoDateTime + "T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        return dateTime;
    }

    private CarDto mapToCarDto(PricelineCarResponse.Car car, CarSearchRequest request) {
        log.debug("Mapping car to DTO: {}", car);

        CarDto dto = new CarDto();
        dto.setVendor("Priceline");

        if (car.getVehicle() != null) {
            dto.setCarType(car.getVehicle().getBrand());
            String model = car.getVehicle().getModel() != null ?
                    car.getVehicle().getModel() : car.getVehicle().getExample();
            dto.setModel(model);
        }

        if (car.getRate() != null) {
            dto.setPrice(car.getRate().getTotalPrice());
            String currency = car.getRate().getCurrencyCode() != null ?
                    car.getRate().getCurrencyCode() : "USD";
            dto.setCurrency(currency);
        }

        dto.setPickupLocation(request.getPickupLocation());
        dto.setDropoffLocation(request.getDropoffLocation() != null ?
                request.getDropoffLocation() : request.getPickupLocation());
        dto.setPickupDateTime(request.getPickupDateTime());
        dto.setDropoffDateTime(request.getDropoffDateTime());

        return dto;
    }
}