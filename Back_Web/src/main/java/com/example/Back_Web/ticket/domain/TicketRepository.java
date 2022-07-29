package com.example.Back_Web.ticket.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    public List<Ticket> findByTripDestination (String destination);
}
