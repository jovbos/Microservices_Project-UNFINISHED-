package com.example.Back_Empresa.email.domain;

import com.example.Back_Empresa.ticket.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;

@Entity(name = "Emails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    @Id
    @GeneratedValue(generator = "uuidTicket")
    @GenericGenerator(name = "uuidTicket", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "Email_Id")
    private String emailId;

    @OneToOne
    @JoinColumn(name = "Ticket_Id")
    private Ticket ticket;

    // --- Añadi las siguientes columnas unicamente para filtrar con queries ---
    @Column(name = "Destination")
    private String destination;

    @Column(name = "Date")
    private LocalDate date;

    @Column(name = "Time")
    private Time time;
}
