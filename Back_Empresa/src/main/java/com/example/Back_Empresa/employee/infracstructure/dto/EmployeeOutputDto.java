package com.example.Back_Empresa.employee.infracstructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOutputDto {

    private String id;

    private String name;

    private String surname;

    private Integer phoneNumber;

    private String email;

    private String password;

    private Boolean admin;
}
