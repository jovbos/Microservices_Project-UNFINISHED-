package com.example.Back_Empresa.employee.application;

import com.example.Back_Empresa.employee.infracstructure.dto.EmployeeInputDto;
import com.example.Back_Empresa.employee.infracstructure.dto.EmployeeOutputDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeServicePort {
    public ResponseEntity<EmployeeOutputDto> createEmployeeFunction(EmployeeInputDto employeeInputDto);

    public ResponseEntity<List<EmployeeOutputDto>> getEmployeeFunction();

    public ResponseEntity<EmployeeOutputDto> updateEmployeeFunction(EmployeeInputDto employeeInputDto, String id);

    public ResponseEntity<String> deleteEmployeeFunction(String id);
}
