package com.example.Back_Web.ticket.infracstructure.dto;

import com.example.Back_Web.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketOutputDto {

    private String ticketId;

//    private String destination;

    private ClientDataOutputDto clientData;

    private TripOutputDto tripData;

//    private LocalDate date;
//
//    private Time time;

    private Integer seat;

}
