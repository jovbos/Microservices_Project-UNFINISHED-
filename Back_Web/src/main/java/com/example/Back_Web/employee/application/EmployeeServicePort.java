package com.example.Back_Web.employee.application;

import com.example.Back_Web.employee.infracstructure.dto.EmployeeInputDto;
import com.example.Back_Web.employee.infracstructure.dto.EmployeeOutputDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeServicePort {
    public ResponseEntity<EmployeeOutputDto> createEmployeeFunction(EmployeeInputDto employeeInputDto);

    public ResponseEntity<List<EmployeeOutputDto>> getEmployeeFunction(String token);

    public ResponseEntity<EmployeeOutputDto> updateEmployeeFunction(EmployeeInputDto employeeInputDto, String id, String token);

    public ResponseEntity<String> deleteEmployeeFunction(String id, String token);
}
