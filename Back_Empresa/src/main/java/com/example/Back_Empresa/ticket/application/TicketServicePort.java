package com.example.Back_Empresa.ticket.application;

import com.example.Back_Empresa.ticket.infracstructure.dto.TicketInputDto;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketOutputDto;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface TicketServicePort {
    ResponseEntity<String> createTicket(TicketInputDto ticketInputDto);

    ResponseEntity<List<TicketOutputDto>> getCriteriaTickets(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo);
    ResponseEntity<String> deleteTicketFunction(String id);
}
