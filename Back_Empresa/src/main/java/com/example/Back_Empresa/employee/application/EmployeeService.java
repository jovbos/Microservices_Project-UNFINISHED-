package com.example.Back_Empresa.employee.application;

import com.example.Back_Empresa.client.domain.ClientRepository;
import com.example.Back_Empresa.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Empresa.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Empresa.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Empresa.employee.domain.Employee;
import com.example.Back_Empresa.employee.domain.EmployeeRepository;
import com.example.Back_Empresa.employee.infracstructure.dto.EmployeeInputDto;
import com.example.Back_Empresa.employee.infracstructure.dto.EmployeeOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService implements EmployeeServicePort{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseEntity<EmployeeOutputDto> createEmployeeFunction(EmployeeInputDto employeeInputDto) {

        // --- Validacion de campos
        if (employeeInputDto.getEmail() == null || employeeInputDto.getPassword() == null || employeeInputDto.getAdmin() == null)
            throw new CustomErrorRequest400("SOME OBLIGATORY CAMPS ARE EMPTY");

        // --- Validacion de email ---
        if (employeeRepository.existsByEmail(employeeInputDto.getEmail()) || clientRepository.existsByEmail(employeeInputDto.getEmail())) {
            throw new CustomErrorRequest403("THIS EMAIL IS ALREADY TAKEN");
        } else {

            // --- Registro de empleado en todas las bases de datos ---
            Employee employee = modelMapper.map(employeeInputDto, Employee.class);
            employee.setEmployeeId(UUID.randomUUID().toString());
            employeeRepository.save(employee);
            kafkaTemplate.send("employeeTopic", employee);

            // --- Output ---
            EmployeeOutputDto employeeOutputDto = modelMapper.map(employee, EmployeeOutputDto.class);
            employeeOutputDto.setId(employee.getEmployeeId());
            return new ResponseEntity<>(employeeOutputDto, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<EmployeeOutputDto>> getEmployeeFunction() {
        List<Employee> employeeList = employeeRepository.findAll();
        List<EmployeeOutputDto> employeeOutputDtoList = new ArrayList<>();

        employeeList.forEach(employee -> {
            EmployeeOutputDto employeeOutputDto = modelMapper.map(employee, EmployeeOutputDto.class);
            employeeOutputDto.setId(employee.getEmployeeId());
            employeeOutputDtoList.add(employeeOutputDto);
        });
        return new ResponseEntity<>(employeeOutputDtoList, HttpStatus.OK);
    }

    public ResponseEntity<EmployeeOutputDto> updateEmployeeFunction(EmployeeInputDto employeeInputDto, String id) {
        if (employeeRepository.existsById(id)){

            // --- Se comprueba que existe el empleado
            Employee employee = modelMapper.map(employeeInputDto, Employee.class);
            employee.setEmployeeId(id);
            employee.setClient(employeeRepository.findById(id).get().getClient());
            employeeRepository.save(employee);
            kafkaTemplate.send("employeeTopic", employee);

            // --- Output ---
            EmployeeOutputDto employeeOutputDto = modelMapper.map(employee, EmployeeOutputDto.class);
            employeeOutputDto.setId(employee.getEmployeeId());
            return new ResponseEntity<>(employeeOutputDto, HttpStatus.OK);
        } else throw new CustomErrorRequest404("EMPLOYEE NOT FOUND");
    }

    public ResponseEntity<String> deleteEmployeeFunction(String id) {

        // --- Se comprueba que existe el empleado ---
        if (employeeRepository.existsById(id)) {

            // --- Se borra en todas las bases de datos ---
            employeeRepository.deleteById(id);
            String output = "Employee with ID: " + id + " has been remove.";
            kafkaTemplate.send("employeeTopic", "Delete employee " + id);

            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("EMPLOYEE NOT FOUND");
    }
}
