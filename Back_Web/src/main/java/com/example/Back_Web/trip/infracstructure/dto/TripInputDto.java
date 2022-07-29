package com.example.Back_Web.trip.infracstructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripInputDto {
    private String destination;

    private LocalDate date;

    private Time time;

    private Integer seatsAvailable;
}
