package com.example.Back_Web.trip.domain;

import com.example.Back_Web.ticket.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;


@Entity(name="Trips")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    @Column(name = "Trip_Id")
    private String tripId;

    @Column(name="Destination")
    private String destination;

    @Column(name="Date")
    private LocalDate date;

    @Column(name="Time")
    private Time time;

    @Column(name="Seats")
    private Integer seatsAvailable;

    @OneToMany(mappedBy = "trip")
    private List<Ticket> ticketList = new ArrayList<>();

}

