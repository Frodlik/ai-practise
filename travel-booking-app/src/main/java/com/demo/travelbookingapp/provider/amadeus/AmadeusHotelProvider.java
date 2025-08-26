package com.demo.travelbookingapp.provider.amadeus;

import com.demo.travelbookingapp.dto.HotelDto;
import com.demo.travelbookingapp.dto.HotelSearchRequest;
import com.demo.travelbookingapp.provider.HotelProviderClient;
import com.demo.travelbookingapp.provider.amadeus.client.AmadeusHotelClient;
import com.demo.travelbookingapp.provider.amadeus.dto.HotelOffersResponse;
import com.demo.travelbookingapp.provider.amadeus.service.AmadeusTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmadeusHotelProvider implements HotelProviderClient {
    private final AmadeusHotelClient hotelClient;
    private final AmadeusTokenService tokenService;

    @Override
    public List<HotelDto> searchHotels(HotelSearchRequest request) {
        try {
            String token = tokenService.getAccessToken();
            HotelOffersResponse response = hotelClient.searchHotelOffers(
                    token,
                    request.getCityCode(),
                    request.getAdults(),
                    request.getCheckIn(),
                    request.getCheckOut()
            );

            return mapToHotelDtos(response);
        } catch (Exception e) {
            log.error("Error searching hotels: {}", e.getMessage());

            return List.of();
        }
    }

    private List<HotelDto> mapToHotelDtos(HotelOffersResponse response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData().stream()
                .map(this::mapToHotelDto)
                .toList();
    }

    private HotelDto mapToHotelDto(HotelOffersResponse.HotelOffer hotelOffer) {
        HotelDto dto = new HotelDto();

        mapHotelInfo(dto, hotelOffer.getHotel());
        hotelOffer.getOffers().stream().findFirst().ifPresent(offer -> mapOfferInfo(dto, offer));

        return dto;
    }

    private void mapHotelInfo(HotelDto dto, HotelOffersResponse.Hotel hotel) {
        if (hotel == null) {
            return;
        }

        dto.setHotelName(hotel.getName());

        if (hotel.getAddress() != null) {
            List<String> lines = hotel.getAddress().getLines() != null ? hotel.getAddress().getLines() : List.of();
            dto.setAddress(String.join(", ", lines));
        }

        if (hotel.getRating() != null) {
            dto.setRating(hotel.getRating().getRating());
        }
    }

    private void mapOfferInfo(HotelDto dto, HotelOffersResponse.Offer offer) {
        dto.setCheckIn(offer.getCheckInDate());
        dto.setCheckOut(offer.getCheckOutDate());

        if (offer.getPrice() != null) {
            dto.setPrice(offer.getPrice().getTotal());
            dto.setCurrency(offer.getPrice().getCurrency());
        }
    }
}
