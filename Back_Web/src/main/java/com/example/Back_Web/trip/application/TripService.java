package com.example.Back_Web.trip.application;

import com.example.Back_Web.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Web.trip.domain.Trip;
import com.example.Back_Web.trip.domain.TripRepository;
import com.example.Back_Web.trip.infracstructure.dto.TripInputDto;
import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
import com.example.Back_Web.trip.domain.TripRepositoryImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Service
public class TripService implements TripServicePort {

    @Autowired
    TripRepositoryImp criteriaRepository;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    ModelMapper modelMapper;



    public ResponseEntity<List<TripOutputDto>> getCriteriaTrips(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo) {
        return criteriaRepository.tripsFilter(destination, dateFrom, dateTo, timeFrom, timeTo);
    }
}
