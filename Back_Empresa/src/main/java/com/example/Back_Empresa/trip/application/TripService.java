package com.example.Back_Empresa.trip.application;

import com.example.Back_Empresa.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Empresa.trip.domain.Trip;
import com.example.Back_Empresa.trip.domain.TripRepository;
import com.example.Back_Empresa.trip.domain.TripRepositoryImp;
import com.example.Back_Empresa.trip.infracstructure.dto.TripInputDto;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TripService implements TripServicePort {

    @Autowired
    TripRepositoryImp tripRepositoryImp;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseEntity<List<TripOutputDto>> getCriteriaTrips(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo) {
        return tripRepositoryImp.tripsFilter(destination, dateFrom, dateTo, timeFrom, timeTo);
    }

    public ResponseEntity<Integer> getSeats(String tripId) {
        System.out.println(tripId);
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomErrorRequest404("TRIP NOT FOUND"));
        return new ResponseEntity<Integer>(trip.getSeatsAvailable(), HttpStatus.OK);
    }

    public ResponseEntity<TripOutputDto> createTripFunction(TripInputDto tripInputDto) {
        Trip trip = modelMapper.map(tripInputDto, Trip.class);
        trip.setTripId(UUID.randomUUID().toString());
        trip.setSeatsAvailable(40);
        tripRepository.save(trip);
        kafkaTemplate.send("tripTopic", trip);

        TripOutputDto tripOutputDto = modelMapper.map(trip, TripOutputDto.class);
        tripOutputDto.setId(trip.getTripId());
        return new ResponseEntity<>(tripOutputDto, HttpStatus.OK);
    }

    public ResponseEntity<TripOutputDto> updateTripFunction(TripInputDto tripInputDto, String id) {
        if (tripRepository.existsById(id)) {
            Trip trip = modelMapper.map(tripInputDto, Trip.class);
            trip.setTripId(id);
            trip.setTicketList(tripRepository.findById(id).get().getTicketList());
            trip.setSeatsAvailable(tripRepository.findById(id).get().getSeatsAvailable());
            tripRepository.save(trip);

            TripOutputDto tripOutputDto = modelMapper.map(trip, TripOutputDto.class);
            tripOutputDto.setId(trip.getTripId());
            return new ResponseEntity<TripOutputDto>(tripOutputDto, HttpStatus.OK);
        } else throw new CustomErrorRequest404("TRIP NOT FOUND.");
    }

    public ResponseEntity<String> deleteTripFunction(String id) {
        if (tripRepository.existsById(id)) {
            tripRepository.deleteById(id);

            String output = "Trip with ID: " + id + " has been remove.";
            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("TRIP NOT FOUND.");
    }
}
