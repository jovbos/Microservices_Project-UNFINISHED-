package com.example.Back_Web.employee.infracstructure;

import com.example.Back_Web.employee.application.EmployeeServicePort;
import com.example.Back_Web.employee.infracstructure.dto.EmployeeInputDto;
import com.example.Back_Web.employee.infracstructure.dto.EmployeeOutputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeServicePort employeeServicePort;

    @PostMapping("/api/v0/employee")
    public ResponseEntity<EmployeeOutputDto> createEmployee(EmployeeInputDto employeeInputDto) {
        return employeeServicePort.createEmployeeFunction(employeeInputDto);
    }

    @GetMapping("/api/v0/employee")
    public ResponseEntity<List<EmployeeOutputDto>> getEmployee(@RequestHeader String token) {
        return employeeServicePort.getEmployeeFunction(token);
    }

    @PutMapping("/api/v0/employee/{id}")
    public ResponseEntity<EmployeeOutputDto> updateEmployee(@RequestBody EmployeeInputDto employeeInputDto,
                                                            @PathVariable("id") String id,
                                                            @RequestHeader String token) {
        return employeeServicePort.updateEmployeeFunction(employeeInputDto, id, token);
    }

    @DeleteMapping("/api/v0/employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") String id, @RequestHeader String token) {
        return employeeServicePort.deleteEmployeeFunction(id, token);
    }
}
