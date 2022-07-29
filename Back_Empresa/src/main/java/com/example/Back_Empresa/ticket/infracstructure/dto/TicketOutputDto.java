package com.example.Back_Empresa.ticket.infracstructure.dto;

import com.example.Back_Empresa.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketOutputDto {

    private String ticketId;

    private ClientDataOutputDto clientData;

    private TripOutputDto tripData;

    private Integer seat;

}
