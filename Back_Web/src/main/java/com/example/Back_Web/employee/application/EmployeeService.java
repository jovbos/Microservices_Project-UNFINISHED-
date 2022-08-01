package com.example.Back_Web.employee.application;

import com.example.Back_Web.client.domain.ClientRepository;
import com.example.Back_Web.config.customErrors.error400.CustomErrorRequest400;
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
import org.springframework.kafka.core.KafkaTemplate;
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

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseEntity<EmployeeOutputDto> createEmployeeFunction(EmployeeInputDto employeeInputDto, String token) {

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

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

        // --- Se validan los campos obligatorios ---
        if (employeeInputDto.getEmail() == null || employeeInputDto.getPassword() == null || employeeInputDto.getAdmin() == null)
            throw new CustomErrorRequest400("SOME OBLIGATORY CAMPS ARE EMPTY");

        // --- Se comprueba si existe ese email ---
        if (employeeRepository.existsByEmail(employeeInputDto.getEmail()) || clientRepository.existsByEmail(employeeInputDto.getEmail())) {
            throw new CustomErrorRequest403("THIS EMAIL IS ALREADY TAKEN");
        } else {
            Employee employee = modelMapper.map(employeeInputDto, Employee.class);
            employee.setEmployeeId(UUID.randomUUID().toString());
            employeeRepository.save(employee);
            kafkaTemplate.send("employeeTopic", employee);

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

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

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

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

        // --- Se comprueba si existe el empleado ---
        if (employeeRepository.existsById(id)){

            // --- Actualiza el registro en todas las bases de datos ---
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

    public ResponseEntity<String> deleteEmployeeFunction(String id, String token) {

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

        if (employeeRepository.existsByEmail(emailLogged)) {
            if (!employeeRepository.findByEmail(emailLogged).get(0).getAdmin().equals(true))
                throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");
        } else if (!employeeRepository.existsByEmail(emailLogged))
            throw new CustomErrorRequest403("YOU NEED ADMIN RIGHTS");

        // --- Comprueba si existe el empleado ---
        if (employeeRepository.existsById(id)) {

            // --- Borra el empleado en todas las bases de datos ---
            employeeRepository.deleteById(id);
            String output = "Employee with ID: " + id + " has been remove.";
            kafkaTemplate.send("employeeTopic", "Delete employee " + id);

            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("EMPLOYEE NOT FOUND");
    }
}
