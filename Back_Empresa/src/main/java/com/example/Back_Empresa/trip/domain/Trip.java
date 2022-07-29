package com.example.Back_Empresa.trip.domain;

import com.example.Back_Empresa.ticket.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity(name="Trips")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    @Column(name = "Trip_Id")
    private String tripId;

    @NotNull@NotEmpty
    @Column(name="Destination")
    private String destination;

    @NotNull@NotEmpty
    @Column(name="Date")
    private LocalDate date;

    @NotNull@NotEmpty
    @Column(name="Time")
    private Time time;

    @NotNull@NotEmpty
    @Column(name="Seats")
    private Integer seatsAvailable;

    @OneToMany(mappedBy = "trip")
    private List<Ticket> ticketList;

}

