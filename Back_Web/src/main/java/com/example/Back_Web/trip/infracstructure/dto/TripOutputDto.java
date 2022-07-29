package com.example.Back_Web.trip.infracstructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripOutputDto {
    String id;

    String destination;

    Integer seatsAvailable;

    LocalDate date;

    Time time;
}
