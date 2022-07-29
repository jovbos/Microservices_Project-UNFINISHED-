package com.example.Back_Web.client.infracstructure.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOutputDto {

    private String id;

    private String name;

    private String surname;

    private Integer phoneNumber;

    private String email;

    private String password;

}
