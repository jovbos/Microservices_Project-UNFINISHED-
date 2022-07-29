package com.example.Back_Web.ticket.application;

import com.example.Back_Web.ticket.infracstructure.dto.TicketInputDto;
import com.example.Back_Web.ticket.infracstructure.dto.TicketOutputDto;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface TicketServicePort {
    public ResponseEntity<TicketOutputDto> createTicket(TicketInputDto ticketInputDto, String token) ;

    ResponseEntity<List<TicketOutputDto>> getCriteriaTickets(String destination,
                                                             LocalDate dateFrom,
                                                             LocalDate dateTo,
                                                             Time timeFrom,
                                                             Time timeTo,
                                                             String token);

}
