package com.example.Back_Web.ticket.domain;


import com.example.Back_Web.ticket.infracstructure.dto.TicketOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TicketRepositoryImp {

    @Autowired
    ModelMapper modelMapper;

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public TicketRepositoryImp(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    // ---
    public ResponseEntity<List<TicketOutputDto>> ticketsFilter(String destination,
                                                               LocalDate dateFrom,
                                                               LocalDate dateTo,
                                                               Time timeFrom,
                                                               Time timeTo) {
        CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
        Root<Ticket> ticketRoot = criteriaQuery.from(Ticket.class);
        Predicate predicate = getPredicate(ticketRoot, destination, dateFrom, dateTo, timeFrom, timeTo);
        criteriaQuery.where(predicate);

        TypedQuery<Ticket> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Ticket> ticketList = new ArrayList<>(typedQuery.getResultList());
        List<TicketOutputDto> ticketOutputDtoList = new ArrayList<>();
        ticketList.forEach(ticket -> {
            TicketOutputDto ticketOutputDto = modelMapper.map(ticket, TicketOutputDto.class);
            ticketOutputDtoList.add(ticketOutputDto);
        });
        return new ResponseEntity<>(ticketOutputDtoList, HttpStatus.OK);
    }
    private Predicate getPredicate(Root<Ticket> ticketRoot,
                                   String destination,
                                   LocalDate dateFrom,
                                   LocalDate dateTo,
                                   Time timeFrom,
                                   Time timeTo) {

        List<Predicate> predicateList = new ArrayList<>();
        if (Objects.nonNull(destination)){
            predicateList.add(
                    criteriaBuilder.like(ticketRoot.get("destination"),destination)
            );
        }

        if (Objects.nonNull(dateFrom)){
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(ticketRoot.get("date"), dateFrom));
        }

        if (Objects.nonNull(dateTo)){
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(ticketRoot.get("date"), dateTo));
        }

        if (Objects.nonNull(timeFrom)){
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(ticketRoot.get("time"), timeFrom));
        }

        if (Objects.nonNull(timeTo)){
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(ticketRoot.get("time"), timeTo));
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }
}

