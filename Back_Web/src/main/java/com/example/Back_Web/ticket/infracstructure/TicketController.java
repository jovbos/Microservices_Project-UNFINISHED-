package com.example.Back_Web.ticket.infracstructure;

import com.example.Back_Web.ticket.application.TicketServicePort;
import com.example.Back_Web.ticket.infracstructure.dto.TicketInputDto;
import com.example.Back_Web.ticket.infracstructure.dto.TicketOutputDto;
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
    // --- Se debe aportar token por los headers ---
    @PostMapping("/api/v0/ticket")
    public ResponseEntity<TicketOutputDto> createTicketController(@RequestBody TicketInputDto ticketInputDto,
                                                                  @RequestHeader String token) {
        return ticketServicePort.createTicket(ticketInputDto, token);
    }

    // --- Endpoint para consultar lista de reservas realizadas (solo para personal autorizado) ---
    // --- Se debe aportar token por los headers
    @GetMapping("api/v0/ticket/{destination}")
    public ResponseEntity<List<TicketOutputDto>> getFilteredTicketList (
            @PathVariable(name = "destination") String destination,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(name = "timeFrom", required = false) Time timeFrom,
            @RequestParam(name = "timeTo", required = false) Time timeTo,
            @RequestHeader String token) {
        return ticketServicePort.getCriteriaTickets(destination, dateFrom, dateTo, timeFrom, timeTo, token);
    }

    @DeleteMapping("api/v0/ticket/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable("id") String id, @RequestHeader String token) {
        return ticketServicePort.deleteTicketFunction(id, token);
    }
}
