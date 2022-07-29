package com.example.Back_Empresa.trip.application;

import com.example.Back_Empresa.trip.infracstructure.dto.TripInputDto;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface TripServicePort {
    ResponseEntity<List<TripOutputDto>> getCriteriaTrips(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo);


    ResponseEntity<Integer> getSeats(String tripId);

    ResponseEntity<TripOutputDto> createTripFunction(TripInputDto tripInputDto);

    ResponseEntity<TripOutputDto> updateTripFunction(TripInputDto tripInputDto, String id);

    ResponseEntity<String> deleteTripFunction(String id);
}
