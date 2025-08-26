package com.demo.travelbookingapp.provider.amadeus;

import com.demo.travelbookingapp.dto.FlightDto;
import com.demo.travelbookingapp.dto.FlightSearchRequest;
import com.demo.travelbookingapp.provider.FlightProviderClient;
import com.demo.travelbookingapp.provider.amadeus.client.AmadeusFlightClient;
import com.demo.travelbookingapp.provider.amadeus.dto.FlightOffersRequest;
import com.demo.travelbookingapp.provider.amadeus.dto.FlightOffersResponse;
import com.demo.travelbookingapp.provider.amadeus.service.AmadeusTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmadeusFlightProvider implements FlightProviderClient {
    private final AmadeusFlightClient flightClient;
    private final AmadeusTokenService tokenService;

    @Override
    public List<FlightDto> searchFlights(FlightSearchRequest request) {
        try {
            String token = tokenService.getAccessToken();
            FlightOffersRequest amadeusRequest = buildFlightOffersRequest(request);
            FlightOffersResponse response = flightClient.searchFlightOffers(token, "application/json", amadeusRequest);

            return mapToFlightDtos(response);
        } catch (Exception e) {
            log.error("Error searching flights: {}", e.getMessage());

            return List.of();
        }
    }

    private FlightOffersRequest buildFlightOffersRequest(FlightSearchRequest request) {
        FlightOffersRequest amadeusRequest = new FlightOffersRequest();

        amadeusRequest.setCurrencyCode("USD");
        amadeusRequest.setSources(List.of("GDS"));

        FlightOffersRequest.OriginDestination originDestination = new FlightOffersRequest.OriginDestination();
        originDestination.setId("1");
        originDestination.setOriginLocationCode(request.getOrigin());
        originDestination.setDestinationLocationCode(request.getDestination());
        originDestination.setDepartureDateTimeRange(new FlightOffersRequest.DepartureDateTimeRange(request.getDepartureDate()));

        amadeusRequest.setOriginDestinations(List.of(originDestination));

        List<FlightOffersRequest.Traveler> travelers = IntStream.range(0, request.getAdults())
                .mapToObj(i -> new FlightOffersRequest.Traveler(String.valueOf(i + 1)))
                .toList();
        amadeusRequest.setTravelers(travelers);

        return amadeusRequest;
    }

    private List<FlightDto> mapToFlightDtos(FlightOffersResponse response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData().stream()
                .map(this::mapToFlightDto)
                .toList();
    }

    private FlightDto mapToFlightDto(FlightOffersResponse.FlightOffer offer) {
        FlightDto dto = new FlightDto();

        mapPrice(dto, offer);
        offer.getItineraries().stream().findFirst().ifPresent(itinerary -> {
            dto.setDuration(itinerary.getDuration());
            mapFirstSegment(dto, itinerary);
        });

        return dto;
    }

    private void mapPrice(FlightDto dto, FlightOffersResponse.FlightOffer offer) {
        if (offer.getPrice() == null) {
            return;
        }
        dto.setPrice(offer.getPrice().getTotal());
        dto.setCurrency(offer.getPrice().getCurrency());
    }

    private void mapFirstSegment(FlightDto dto, FlightOffersResponse.Itinerary itinerary) {
        itinerary.getSegments().stream().findFirst().ifPresent(segment -> {
            dto.setAirline(segment.getCarrierCode());
            dto.setFlightNumber(segment.getCarrierCode() + segment.getNumber());

            if (segment.getDeparture() != null) {
                dto.setOrigin(segment.getDeparture().getIataCode());
                dto.setDepartureTime(segment.getDeparture().getAt());
            }

            if (segment.getArrival() != null) {
                dto.setDestination(segment.getArrival().getIataCode());
                dto.setArrivalTime(segment.getArrival().getAt());
            }
        });
    }
}
