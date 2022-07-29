package com.example.Back_Web.ticket.infracstructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketKafkaDto {

    // --- Clase para transportar datos de manera mas comoda en peticiones KafkaTemplate al hacer reservas ---

    private String id;

    private String clientId;

    private String tripId;

    private Integer seat;
}
