package com.example.Back_Web.trip.application;

import com.example.Back_Web.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Web.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Web.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Web.employee.domain.EmployeeRepository;
import com.example.Back_Web.trip.domain.Trip;
import com.example.Back_Web.trip.domain.TripRepository;
import com.example.Back_Web.trip.infracstructure.dto.TripInputDto;
import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
import com.example.Back_Web.trip.domain.TripRepositoryImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TripService implements TripServicePort {

    @Autowired
    TripRepositoryImp criteriaRepository;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseEntity<List<TripOutputDto>> getCriteriaTrips(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo) {
        return criteriaRepository.tripsFilter(destination, dateFrom, dateTo, timeFrom, timeTo);
    }

    public ResponseEntity<TripOutputDto> createTripFunction(TripInputDto tripInputDto, String token) {

        if (tripInputDto.getDestination() == null || tripInputDto.getDate() == null || tripInputDto.getTime() == null)
            throw new CustomErrorRequest400("SOME OBLIGATORY CAMPS ARE EMPTY");

        // --- Se valida el token aportado y se recibe el mail del usuario loggeado ---
        try {
            new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                    String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }

        ResponseEntity<String> responseToken =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                        String.class);
        String emailLogged = responseToken.getBody();

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

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

    public ResponseEntity<TripOutputDto> updateTripFunction(TripInputDto tripInputDto, String id, String token) {

        // --- Se valida el token aportado y se recibe el mail del usuario loggeado ---
        try {
            new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                    String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }

        ResponseEntity<String> responseToken =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                        String.class);
        String emailLogged = responseToken.getBody();

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

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

    public ResponseEntity<String> deleteTripFunction(String id, String token) {

        // --- Se valida el token aportado y se recibe el mail del usuario loggeado ---
        try {
            new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                    String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }

        ResponseEntity<String> responseToken =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                        String.class);
        String emailLogged = responseToken.getBody();

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

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
