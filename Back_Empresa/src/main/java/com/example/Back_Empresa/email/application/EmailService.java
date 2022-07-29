package com.example.Back_Empresa.email.application;

import com.example.Back_Empresa.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Empresa.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Empresa.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Empresa.email.domain.Email;
import com.example.Back_Empresa.email.domain.EmailRepository;
import com.example.Back_Empresa.email.domain.EmailRepositoryImp;
import com.example.Back_Empresa.email.infracstructure.dto.EmailInputDto;
import com.example.Back_Empresa.email.infracstructure.dto.EmailOutputDto;
import com.example.Back_Empresa.ticket.domain.Ticket;
import com.example.Back_Empresa.ticket.domain.TicketRepository;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketOutputDto;
import com.example.Back_Empresa.trip.domain.TripRepository;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService implements EmailServicePort{
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    EmailRepositoryImp emailRepositoryImp;

    @Autowired
    ModelMapper modelMapper;

    // --- Envia un correo tras la creacion de un ticket, registra los datos del correo en la database ---
    public void sendEmail(TicketOutputDto ticketOutputDto) {
        System.out.println(ticketOutputDto);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tallarines880@gmail.com");
        message.setTo(ticketOutputDto.getClientData().getEmail());
        message.setSubject("Your reservation ticket is ready");
        message.setText("Thank you for making your reservation, " + ticketOutputDto.getClientData().getName() +
                ". Your trip is for " + ticketOutputDto.getTripData().getDestination() + ", on " + ticketOutputDto.getTripData().getDate() +
                " at " + ticketOutputDto.getTripData().getTime() +
                ". Your seat is the number " + ticketOutputDto.getSeat() +
                ".\n\nYou have to keep this ID number and submit it in the bus: " + ticketOutputDto.getTicketId());

        // --- Se registran los datos del email ---
        Ticket ticket = ticketRepository.findById(ticketOutputDto.getTicketId())
                .orElseThrow(() -> new CustomErrorRequest400("TICKET DATA DIDN'T GET SAVED CORRECTLY"));
        Email email = new Email();
        email.setTicket(ticket);
        System.out.println("AAAAAAAAAAAAA");
        emailRepository.save(email);

        mailSender.send(message);
    }

    // --- Reenvia un correo existente en la database ---
    public ResponseEntity<String> reSendEmail(EmailInputDto emailInputDto) {

        // --- Se busca el ticket asociado al correo y se crea el ticket output para llamar a la funcion sendEmail() ---
        Ticket ticket = ticketRepository.findById(emailInputDto.getTicketId())
                .orElseThrow(() -> new CustomErrorRequest404("TICKET NOT FOUND"));

        TicketOutputDto ticketOutputDto = modelMapper.map(ticket, TicketOutputDto.class);
        ticketOutputDto.setClientData(modelMapper.map(ticket.getClient(), ClientDataOutputDto.class));
        ticketOutputDto.setTripData(modelMapper.map(ticket.getTrip(), TripOutputDto.class));

        sendEmail(ticketOutputDto);

        // --- Devuelve una confirmacion ---
        String output = "The Email with the ticket ID: " + ticket.getTicketId() +
                " has been re-send to the client: " + ticket.getClient().getName();
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    public ResponseEntity<List<EmailOutputDto>> getCriteriaEmails(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo) {
        return emailRepositoryImp.emailsFilter(destination, dateFrom, dateTo, timeFrom, timeTo);
    }

    public ResponseEntity<String> deleteEmailFunction(String id) {
        if (emailRepository.existsById(id)){
            emailRepository.deleteById(id);

            String output = "The email with ID: " + id + " has been remove.";
            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("EMAIL NOT FOUND");
    }

}
