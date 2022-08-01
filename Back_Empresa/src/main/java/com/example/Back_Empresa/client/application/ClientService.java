package com.example.Back_Empresa.client.application;

import com.example.Back_Empresa.client.domain.Client;
import com.example.Back_Empresa.client.domain.ClientRepository;
import com.example.Back_Empresa.client.infracstructure.dto.ClientInputDto;
import com.example.Back_Empresa.client.infracstructure.dto.ClientOutputDto;
import com.example.Back_Empresa.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Empresa.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Empresa.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Empresa.config.kafka.KafkaProducerConfig;
import com.example.Back_Empresa.employee.domain.EmployeeRepository;
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
public class ClientService implements  ClientServicePort{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseEntity<ClientOutputDto> createClientFunction(ClientInputDto clientInputDto) {

        // --- Validacion de campos ---
        if (clientInputDto.getEmail() == null || clientInputDto.getPassword() == null)
            throw new CustomErrorRequest400("EMAIL AND PASSWORD MUST NOT BEING EMPTY");

        // --- Validacion de email ---
        if (clientRepository.existsByEmail(clientInputDto.getEmail()) || employeeRepository.existsByEmail(clientInputDto.getEmail())) {
            throw new CustomErrorRequest403("THIS EMAIL IS ALREADY TAKEN");
        } else {

            // --- Registro de cliente en todas las bases de datos ---
            Client client = modelMapper.map(clientInputDto, Client.class);
            client.setClientId(UUID.randomUUID().toString());
            clientRepository.save(client);
            kafkaTemplate.send("clientTopic", client);

            // --- Output ---
            ClientOutputDto clientOutputDto = modelMapper.map(client, ClientOutputDto.class);
            clientOutputDto.setId(client.getClientId());
            return new ResponseEntity<>(clientOutputDto, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<ClientOutputDto>> getClientFunction() {
        List<Client> clientList = clientRepository.findAll();
        List<ClientOutputDto> clientOutputDtoList = new ArrayList<>();

        clientList.forEach(client -> {
            ClientOutputDto clientOutputDto = modelMapper.map(client, ClientOutputDto.class);
            clientOutputDto.setId(client.getClientId());
            clientOutputDtoList.add(clientOutputDto);
        });
        return new ResponseEntity<>(clientOutputDtoList, HttpStatus.OK);
    }

    public ResponseEntity<ClientOutputDto> updateClientFunction(ClientInputDto clientInputDto, String id) {

        // --- Se comprueba que existe el cliente ---
        if (clientRepository.existsById(id)){

            // --- Actualizacion
            Client client = modelMapper.map(clientInputDto, Client.class);
            client.setClientId(id);
            client.setEmployee(clientRepository.findById(id).get().getEmployee());
            clientRepository.save(client);
            kafkaTemplate.send("clientTopic", client);

            // --- Output ---
            ClientOutputDto clientOutputDto = modelMapper.map(client, ClientOutputDto.class);
            clientOutputDto.setId(client.getClientId());
            return new ResponseEntity<>(clientOutputDto, HttpStatus.OK);
        } else throw new CustomErrorRequest404("CLIENT NOT FOUND");
    }

    public ResponseEntity<String> deleteClientFunction(String id) {

        // --- Se comprueba que existe el cliente ---
        if (clientRepository.existsById(id)) {

            // --- Se borra en todas las bases de datos ---
            clientRepository.deleteById(id);
            String output = "Client with ID: " + id + " has been remove.";

            kafkaTemplate.send("clientTopic", "Delete client " + id);

            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("CLIENT NOT FOUND");
    }
}
