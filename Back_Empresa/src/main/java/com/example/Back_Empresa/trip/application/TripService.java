package com.example.Back_Empresa.trip.application;

import com.example.Back_Empresa.config.customErrors.error400.CustomErrorRequest400;
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

    // --- Para comprobar si quedan plazas desde back web ---
    public ResponseEntity<Integer> getSeats(String tripId) {
        System.out.println(tripId);
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomErrorRequest404("TRIP NOT FOUND"));
        return new ResponseEntity<>(trip.getSeatsAvailable(), HttpStatus.OK);
    }

    public ResponseEntity<TripOutputDto> createTripFunction(TripInputDto tripInputDto) {

        // --- Se validan los campos ---
        if (tripInputDto.getDestination() == null || tripInputDto.getDate() == null || tripInputDto.getTime() == null)
            throw new CustomErrorRequest400("SOME OBLIGATORY CAMPS ARE EMPTY");

        // --- Se registra el viaje en todas las bases de datos ---
        Trip trip = modelMapper.map(tripInputDto, Trip.class);
        trip.setTripId(UUID.randomUUID().toString());
        trip.setSeatsAvailable(40);
        tripRepository.save(trip);
        kafkaTemplate.send("tripTopic", trip);

        // --- Output ---
        TripOutputDto tripOutputDto = modelMapper.map(trip, TripOutputDto.class);
        tripOutputDto.setId(trip.getTripId());
        return new ResponseEntity<>(tripOutputDto, HttpStatus.OK);
    }

    public ResponseEntity<TripOutputDto> updateTripFunction(TripInputDto tripInputDto, String id) {

        // --- Se comprueba que existe el viaje ---
        if (tripRepository.existsById(id)) {

            // --- Se registra el viaje ---
            Trip trip = modelMapper.map(tripInputDto, Trip.class);
            trip.setTripId(id);
            trip.setTicketList(tripRepository.findById(id).get().getTicketList());
            trip.setSeatsAvailable(tripRepository.findById(id).get().getSeatsAvailable());
            tripRepository.save(trip);
            kafkaTemplate.send("tripTopic", trip);

            // --- Output ---
            TripOutputDto tripOutputDto = modelMapper.map(trip, TripOutputDto.class);
            tripOutputDto.setId(trip.getTripId());
            return new ResponseEntity<>(tripOutputDto, HttpStatus.OK);
        } else throw new CustomErrorRequest404("TRIP NOT FOUND.");
    }

    public ResponseEntity<String> deleteTripFunction(String id) {

        // --- Se comprueba que existe el viaje ---
        if (tripRepository.existsById(id)) {

            // --- Se borra el viaje ---
            tripRepository.deleteById(id);

            String output = "Trip with ID: " + id + " has been remove.";
            kafkaTemplate.send("tripTopic", "Delete trip " + id);
            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("TRIP NOT FOUND.");
    }
}
