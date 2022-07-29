package com.example.Back_Web.employee.application;

import com.example.Back_Web.client.domain.ClientRepository;
import com.example.Back_Web.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Web.employee.domain.Employee;
import com.example.Back_Web.employee.domain.EmployeeRepository;
import com.example.Back_Web.employee.infracstructure.dto.EmployeeInputDto;
import com.example.Back_Web.employee.infracstructure.dto.EmployeeOutputDto;
import com.example.Back_Web.config.customErrors.error404.CustomErrorRequest404;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService implements EmployeeServicePort {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ClientRepository clientRepository;

    public ResponseEntity<EmployeeOutputDto> createEmployeeFunction(EmployeeInputDto employeeInputDto) {
        if (employeeRepository.existsByEmail(employeeInputDto.getEmail()) || clientRepository.existsByEmail(employeeInputDto.getEmail())) {
            throw new CustomErrorRequest403("THIS EMAIL IS ALREADY TAKEN");
        } else {
            Employee employee = modelMapper.map(employeeInputDto, Employee.class);
            employee.setEmployeeId(UUID.randomUUID().toString());
            employeeRepository.save(employee);

            EmployeeOutputDto employeeOutputDto = modelMapper.map(employee, EmployeeOutputDto.class);
            employeeOutputDto.setId(employee.getEmployeeId());
            return new ResponseEntity<>(employeeOutputDto, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<EmployeeOutputDto>> getEmployeeFunction(String token) {

        // --- Se valida el token aportado y se recibe el mail del usuario loggeado ---
        try {
            new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                    String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }
        ResponseEntity<String> responseToken =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                        String.class);
        String emailLogged = responseToken.getBody();

        if (!clientRepository.existsByEmail(emailLogged)) throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        List<Employee> employeeList = employeeRepository.findAll();
        List<EmployeeOutputDto> employeeOutputDtoList = new ArrayList<>();

        employeeList.forEach(employee -> {
            EmployeeOutputDto employeeOutputDto = modelMapper.map(employee, EmployeeOutputDto.class);
            employeeOutputDto.setId(employee.getEmployeeId());
            employeeOutputDtoList.add(employeeOutputDto);
        });
        return new ResponseEntity<>(employeeOutputDtoList, HttpStatus.OK);
    }

    public ResponseEntity<EmployeeOutputDto> updateEmployeeFunction(EmployeeInputDto employeeInputDto, String id, String token) {

        // --- Se valida el token aportado y se recibe el mail del usuario loggeado ---
        try {
            new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                    String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }
        ResponseEntity<String> responseToken =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                        String.class);
        String emailLogged = responseToken.getBody();

        if (!clientRepository.existsByEmail(emailLogged)) throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        if (employeeRepository.existsById(id)){
            Employee employee = modelMapper.map(employeeInputDto, Employee.class);
            employee.setEmployeeId(id);
            employee.setClient(employeeRepository.findById(id).get().getClient());
            employeeRepository.save(employee);

            EmployeeOutputDto employeeOutputDto = modelMapper.map(employee, EmployeeOutputDto.class);
            employeeOutputDto.setId(employee.getEmployeeId());
            return new ResponseEntity<>(employeeOutputDto, HttpStatus.OK);
        } else throw new CustomErrorRequest404("EMPLOYEE NOT FOUND");
    }

    public ResponseEntity<String> deleteEmployeeFunction(String id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            String output = "Employee with ID: " + id + " has been remove.";

            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("EMPLOYEE NOT FOUND");
    }
}
