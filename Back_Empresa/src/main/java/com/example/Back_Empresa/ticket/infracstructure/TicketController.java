package com.example.Back_Empresa.ticket.infracstructure;

import com.example.Back_Empresa.ticket.application.TicketServicePort;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketInputDto;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketOutputDto;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@RestController
public class TicketController {

    @Autowired
    TicketServicePort ticketServicePort;

    // --- Endpoint para hacer una reserva (el cliente debe registrarse previamente) ---
    @PostMapping("/api/v0/ticket")
    public ResponseEntity<String> createTicketController(@RequestBody TicketInputDto ticketInputDto){
        return ticketServicePort.createTicket(ticketInputDto);
    }


    //--- Endpoint para consultar lista de reservas realizadas (solo para personal autorizado) ---
    @GetMapping("api/v0/ticket/{destination}")
    public ResponseEntity<List<TicketOutputDto>> getFilteredTicketList (
            @PathVariable(name = "destination") String destination,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(name = "timeFrom", required = false) Time timeFrom,
            @RequestParam(name = "timeTo", required = false) Time timeTo) {
        return ticketServicePort.getCriteriaTickets(destination, dateFrom, dateTo, timeFrom, timeTo);
    }

    // --- Borrar tickets (solo personal autorizado) ---
    @DeleteMapping("api/v0/ticket/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable("id") String id) {
        return ticketServicePort.deleteTicketFunction(id);
    }
}
