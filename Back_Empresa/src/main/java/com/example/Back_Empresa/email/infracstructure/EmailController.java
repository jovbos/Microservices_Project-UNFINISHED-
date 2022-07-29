package com.example.Back_Empresa.email.infracstructure;

import com.example.Back_Empresa.email.application.EmailServicePort;
import com.example.Back_Empresa.email.infracstructure.dto.EmailInputDto;
import com.example.Back_Empresa.email.infracstructure.dto.EmailOutputDto;
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
public class EmailController {

    @Autowired
    EmailServicePort emailServicePort;

    // --- Endpoint para llamar automaticamente desde createTicket ---
    @PostMapping("/api/v0/email")
    public void emailSender(@RequestBody  TicketOutputDto ticketOutputDto) {
        emailServicePort.sendEmail(ticketOutputDto);
    }

    // --- Endpoint para pedir que se reenvie un email, se aporta el ID del ticket ---
    @PutMapping("/api/v0/email")
    public ResponseEntity<String> emailReSender(EmailInputDto emailInputDto) {
        return emailServicePort.reSendEmail(emailInputDto);
    }

    // --- Endpoint que muestra la lista de correos enviados, se puede filtrar por destino, hora y fecha ---
    @GetMapping("api/v0/email/{destination}")
    public ResponseEntity<List<EmailOutputDto>> getFilteredTripList (
            @PathVariable(name = "destination") String destination,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(name = "timeFrom", required = false) Time timeFrom,
            @RequestParam(name = "timeTo", required = false) Time timeTo) {
        return emailServicePort.getCriteriaEmails(destination, dateFrom, dateTo, timeFrom, timeTo);
    }

    // --- Endpoint para borrar email por id ---
    @DeleteMapping("/api/v0/email/{id}")
    public ResponseEntity<String> deleteEmail(@PathVariable("id") String id) {
        return emailServicePort.deleteEmailFunction(id);
    }
}
