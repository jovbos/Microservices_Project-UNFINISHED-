package com.example.Back_Web.trip.infracstructure;

import com.example.Back_Web.trip.application.TripServicePort;
import com.example.Back_Web.trip.infracstructure.dto.TripInputDto;
import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@RestController
public class TripController {

    @Autowired
    TripServicePort tripServicePort;

    // --- Endpoint para consultar viajes en una ventana de fecha y hora indicadas, se debe completar la url con la ciudad de destino ---
    @GetMapping("/api/v0/trip/{destination}")
    public ResponseEntity<List<TripOutputDto>> getFilteredTripList (
            @PathVariable(name = "destination") String destination,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(name = "timeFrom", required = false) Time timeFrom,
            @RequestParam(name = "timeTo", required = false) Time timeTo) {
        return tripServicePort.getCriteriaTrips(destination, dateFrom, dateTo, timeFrom, timeTo);
    }

    // --- Para crear nuevos viajes ---
    @PostMapping("/api/v0/trip")
    public ResponseEntity<TripOutputDto> createTrip(@RequestBody TripInputDto tripInputDto,
                                                    @RequestHeader String token) {
        return tripServicePort.createTripFunction(tripInputDto, token);
    }

    // --- Modificar viajes por id ---
    @PutMapping("/api/v0/trip/{id}")
    public ResponseEntity<TripOutputDto> updateTrip(@RequestBody TripInputDto tripInputDto,
                                                    @PathVariable("id") String id,
                                                    @RequestHeader String token) {
        return tripServicePort.updateTripFunction(tripInputDto, id, token);
    }

    // --- Borrar viajes por id ---
    @DeleteMapping("api/v0/trip/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable("id") String id,
                                             @RequestHeader String token) {
        return tripServicePort.deleteTripFunction(id, token);
    }
}
