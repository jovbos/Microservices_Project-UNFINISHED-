package com.example.Back_Empresa.email.application;

import com.example.Back_Empresa.email.infracstructure.dto.EmailInputDto;
import com.example.Back_Empresa.email.infracstructure.dto.EmailOutputDto;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketOutputDto;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface EmailServicePort {
    void sendEmail(TicketOutputDto ticketOutputDto);

    ResponseEntity<String> reSendEmail(EmailInputDto emailInputDto);

    ResponseEntity<List<EmailOutputDto>> getCriteriaEmails(String destination,
                                                                  LocalDate dateFrom,
                                                                  LocalDate dateTo,
                                                                  Time timeFrom,
                                                                  Time timeTo);
    ResponseEntity<String> deleteEmailFunction(String id);
}
