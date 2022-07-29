package com.example.Back_Empresa.client.application;

import com.example.Back_Empresa.client.infracstructure.dto.ClientInputDto;
import com.example.Back_Empresa.client.infracstructure.dto.ClientOutputDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClientServicePort {
    public ResponseEntity<ClientOutputDto> createClientFunction(ClientInputDto clientInputDto);

    public ResponseEntity<List<ClientOutputDto>> getClientFunction();

    public ResponseEntity<ClientOutputDto> updateClientFunction(ClientInputDto clientInputDto, String id);

    public ResponseEntity<String> deleteClientFunction(String id);
}
