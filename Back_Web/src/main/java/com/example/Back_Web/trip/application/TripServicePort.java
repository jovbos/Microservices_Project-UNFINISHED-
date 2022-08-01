package com.example.Back_Web.trip.application;

import com.example.Back_Web.trip.infracstructure.dto.TripInputDto;
import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface TripServicePort {
    public ResponseEntity<List<TripOutputDto>> getCriteriaTrips(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo);

    ResponseEntity<TripOutputDto> createTripFunction(TripInputDto tripInputDto, String token);

    ResponseEntity<TripOutputDto> updateTripFunction(TripInputDto tripInputDto, String id, String token);

    ResponseEntity<String> deleteTripFunction(String id, String token);

}
