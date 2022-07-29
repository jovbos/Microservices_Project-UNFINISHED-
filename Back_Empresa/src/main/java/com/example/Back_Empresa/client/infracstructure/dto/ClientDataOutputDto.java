package com.example.Back_Empresa.client.infracstructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDataOutputDto {
    private String name;

    private String surname;

    private Integer phoneNumber;

    private String email;
}
