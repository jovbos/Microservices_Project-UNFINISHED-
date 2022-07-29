package com.example.Back_Empresa.trip.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, String> {
    public List<Trip> findByDestinationAndDateAndTime(String destination, LocalDate date, Time time);
}
