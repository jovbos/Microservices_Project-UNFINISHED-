package com.example.Back_Web.client.application;

import com.example.Back_Web.client.domain.Client;
import com.example.Back_Web.client.domain.ClientRepository;
import com.example.Back_Web.client.infracstructure.dto.ClientInputDto;
import com.example.Back_Web.client.infracstructure.dto.ClientOutputDto;
import com.example.Back_Web.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Web.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Web.employee.domain.EmployeeRepository;
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
public class ClientService implements  ClientServicePort{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public ResponseEntity<ClientOutputDto> createClientFunction(ClientInputDto clientInputDto) {

        if (clientRepository.existsByEmail(clientInputDto.getEmail()) || employeeRepository.existsByEmail(clientInputDto.getEmail())) {
            throw new CustomErrorRequest403("THIS EMAIL IS ALREADY TAKEN");
        } else {
            Client client = modelMapper.map(clientInputDto, Client.class);
            client.setClientId(UUID.randomUUID().toString());
            clientRepository.save(client);

            ClientOutputDto clientOutputDto = modelMapper.map(client, ClientOutputDto.class);
            clientOutputDto.setId(client.getClientId());
            return new ResponseEntity<>(clientOutputDto, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<ClientOutputDto>> getClientFunction(String token) {

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

        List<Client> clientList = clientRepository.findAll();
        List<ClientOutputDto> clientOutputDtoList = new ArrayList<>();

        clientList.forEach(client -> {
            ClientOutputDto clientOutputDto = modelMapper.map(client, ClientOutputDto.class);
            clientOutputDto.setId(client.getClientId());
            clientOutputDtoList.add(clientOutputDto);
        });
        return new ResponseEntity<>(clientOutputDtoList, HttpStatus.OK);
    }

    public ResponseEntity<ClientOutputDto> updateClientFunction(ClientInputDto clientInputDto, String id, String token) {

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
        if (clientRepository.existsById(id)){
            Client client = modelMapper.map(clientInputDto, Client.class);
            client.setClientId(id);
            client.setEmployee(clientRepository.findById(id).get().getEmployee());
            clientRepository.save(client);

            ClientOutputDto clientOutputDto = modelMapper.map(client, ClientOutputDto.class);
            clientOutputDto.setId(client.getClientId());
            return new ResponseEntity<>(clientOutputDto, HttpStatus.OK);
        } else throw new CustomErrorRequest404("CLIENT NOT FOUND");
    }

    public ResponseEntity<String> deleteClientFunction(String id, String token) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            String output = "Client with ID: " + id + " has been remove.";

            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("CLIENT NOT FOUND");
    }
}
