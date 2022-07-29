package com.example.Back_Web.trip.domain;


import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
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
public class TripRepositoryImp {

    @Autowired
    ModelMapper modelMapper;

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public TripRepositoryImp(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    // ---
    public ResponseEntity<List<TripOutputDto>> tripsFilter(String destination,
                                                  LocalDate dateFrom,
                                                  LocalDate dateTo,
                                                  Time timeFrom,
                                                  Time timeTo) {
        CriteriaQuery<Trip> criteriaQuery = criteriaBuilder.createQuery(Trip.class);
        Root<Trip> tripRoot = criteriaQuery.from(Trip.class);
        Predicate predicate = getPredicate(tripRoot, destination, dateFrom, dateTo, timeFrom, timeTo);
        criteriaQuery.where(predicate);

        TypedQuery<Trip> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Trip> tripList = new ArrayList<>(typedQuery.getResultList());
        List<TripOutputDto> tripOutputDtoList = new ArrayList<>();
        tripList.forEach(trip -> {
            TripOutputDto tripOutputDto = modelMapper.map(trip, TripOutputDto.class);
            tripOutputDtoList.add(tripOutputDto);
        });

        return new ResponseEntity<>(tripOutputDtoList, HttpStatus.OK);
    }
    private Predicate getPredicate(Root<Trip> tripRoot,
                                   String destination,
                                   LocalDate dateFrom,
                                   LocalDate dateTo,
                                   Time timeFrom,
                                   Time timeTo) {

        List<Predicate> predicateList = new ArrayList<>();
        if (Objects.nonNull(destination)){
            predicateList.add(
                    criteriaBuilder.like(tripRoot.get("destination"),destination)
            );
        }

        if (Objects.nonNull(dateFrom)){
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(tripRoot.get("date"), dateFrom));
        }

        if (Objects.nonNull(dateTo)){
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(tripRoot.get("date"), dateTo));
        }

        if (Objects.nonNull(timeFrom)){
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(tripRoot.get("time"), timeFrom));
        }

        if (Objects.nonNull(timeTo)){
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(tripRoot.get("time"), timeTo));
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }
}

