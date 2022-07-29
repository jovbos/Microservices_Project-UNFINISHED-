package com.example.Back_Empresa.client.infracstructure;

import com.example.Back_Empresa.client.application.ClientServicePort;
import com.example.Back_Empresa.client.infracstructure.dto.ClientInputDto;
import com.example.Back_Empresa.client.infracstructure.dto.ClientOutputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientController {

    @Autowired
    ClientServicePort clientServicePort;

    @PostMapping("/api/v0/client")
    public ResponseEntity<ClientOutputDto> createClient(@RequestBody ClientInputDto clientInputDto) {
        return clientServicePort.createClientFunction(clientInputDto);
    }

    @GetMapping("/api/v0/client")
    public ResponseEntity<List<ClientOutputDto>> getClientList() {
        return clientServicePort.getClientFunction();
    }

    @PutMapping("/api/v0/client/{id}")
    public ResponseEntity<ClientOutputDto> updateClient(@RequestBody ClientInputDto clientInputDto, @PathVariable("id") String id) {
        return clientServicePort.updateClientFunction(clientInputDto, id);
    }

    @DeleteMapping("/api/v0/client/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable("id") String id) {
        return clientServicePort.deleteClientFunction(id);
    }
}
