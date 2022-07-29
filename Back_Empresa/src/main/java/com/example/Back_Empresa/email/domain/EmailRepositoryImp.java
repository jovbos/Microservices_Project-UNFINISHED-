package com.example.Back_Empresa.email.domain;


import com.example.Back_Empresa.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Empresa.email.domain.Email;
import com.example.Back_Empresa.email.infracstructure.dto.EmailOutputDto;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
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
public class EmailRepositoryImp {

    @Autowired
    ModelMapper modelMapper;

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public EmailRepositoryImp(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    // ---
    public ResponseEntity<List<EmailOutputDto>> emailsFilter(String destination,
                                                           LocalDate dateFrom,
                                                           LocalDate dateTo,
                                                           Time timeFrom,
                                                           Time timeTo) {
        CriteriaQuery<Email> criteriaQuery = criteriaBuilder.createQuery(Email.class);
        Root<Email> emailRoot = criteriaQuery.from(Email.class);
        Predicate predicate = getPredicate(emailRoot, destination, dateFrom, dateTo, timeFrom, timeTo);
        criteriaQuery.where(predicate);

        TypedQuery<Email> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Email> emailList = new ArrayList<>(typedQuery.getResultList());
        List<EmailOutputDto> emailOutputDtoList = new ArrayList<>();
        emailList.forEach(email -> {
            EmailOutputDto emailOutputDto = modelMapper.map(email, EmailOutputDto.class);
            emailOutputDto.setClientData(modelMapper.map(email.getTicket().getClient(), ClientDataOutputDto.class));
            emailOutputDto.setTripData(modelMapper.map(email.getTicket().getTrip(), TripOutputDto.class));
            emailOutputDtoList.add(emailOutputDto);
        });
        return new ResponseEntity<>(emailOutputDtoList, HttpStatus.OK);
    }
    private Predicate getPredicate(Root<Email> emailRoot,
                                   String destination,
                                   LocalDate dateFrom,
                                   LocalDate dateTo,
                                   Time timeFrom,
                                   Time timeTo) {

        List<Predicate> predicateList = new ArrayList<>();
        if (Objects.nonNull(destination)){
            predicateList.add(
                    criteriaBuilder.like(emailRoot.get("destination"),destination)
            );
        }

        if (Objects.nonNull(dateFrom)){
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(emailRoot.get("date"), dateFrom));
        }

        if (Objects.nonNull(dateTo)){
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(emailRoot.get("date"), dateTo));
        }

        if (Objects.nonNull(timeFrom)){
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(emailRoot.get("time"), timeFrom));
        }

        if (Objects.nonNull(timeTo)){
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(emailRoot.get("time"), timeTo));
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }
}

