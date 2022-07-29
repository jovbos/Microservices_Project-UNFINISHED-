package com.example.Back_Empresa.email.infracstructure.dto;

import com.example.Back_Empresa.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import lombok.Data;

@Data
public class EmailOutputDto {

    private String emailId;

    private ClientDataOutputDto clientData;

    private TripOutputDto tripData;
}
