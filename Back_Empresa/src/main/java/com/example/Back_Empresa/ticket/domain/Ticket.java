package com.example.Back_Empresa.ticket.domain;

import com.example.Back_Empresa.client.domain.Client;
import com.example.Back_Empresa.email.domain.Email;
import com.example.Back_Empresa.trip.domain.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;

// --- Clase para reservas realizadas ---
@Entity(name = "Tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @Column(name = "Ticket_Id")
    private String ticketId;

    @ManyToOne
    @JoinColumn(name = "Trip_Id")
    private Trip trip;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Client_Id")
    private Client client;

    @Column(name="Seat")
    private Integer seat;

    @OneToOne(mappedBy = "ticket")
    private Email email;

    // --- Añadi las siguientes columnas unicamente para filtrar con queries ---
    @Column(name="Destination")
    private String destination;

    @Column(name = "Date")
    private LocalDate date;

    @Column(name = "Time")
    private Time time;
}
