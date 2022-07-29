package com.example.Back_Web.trip.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, String> {
    public List<Trip> findByDestinationAndDateAndTime(String destination, LocalDate date, Time time);
}
