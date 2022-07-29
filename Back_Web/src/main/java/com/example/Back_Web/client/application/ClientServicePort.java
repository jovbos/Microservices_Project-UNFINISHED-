package com.example.Back_Web.client.application;

import com.example.Back_Web.client.infracstructure.dto.ClientInputDto;
import com.example.Back_Web.client.infracstructure.dto.ClientOutputDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClientServicePort {
    public ResponseEntity<ClientOutputDto> createClientFunction(ClientInputDto clientInputDto);

    public ResponseEntity<List<ClientOutputDto>> getClientFunction(String token);

    public ResponseEntity<ClientOutputDto> updateClientFunction(ClientInputDto clientInputDto, String id, String token);

    public ResponseEntity<String> deleteClientFunction(String id, String token);
}
