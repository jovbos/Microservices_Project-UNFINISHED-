package com.example.Back_Web.employee.infracstructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInputDto {
    private String name;

    private String surname;

    private Integer phoneNumber;

    private String email;

    private String password;

    private Boolean admin;
}
