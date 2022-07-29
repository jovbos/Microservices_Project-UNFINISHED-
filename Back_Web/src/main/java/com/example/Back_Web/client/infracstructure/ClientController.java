package com.example.Back_Web.client.infracstructure;

import com.example.Back_Web.client.application.ClientServicePort;
import com.example.Back_Web.client.infracstructure.dto.ClientInputDto;
import com.example.Back_Web.client.infracstructure.dto.ClientOutputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ClientController {

    @Autowired
    ClientServicePort clientServicePort;

    @PostMapping("/api/v0/client")
    public ResponseEntity<ClientOutputDto> createClient(ClientInputDto clientInputDto) {
        return clientServicePort.createClientFunction(clientInputDto);
    }

    @GetMapping("/api/v0/client")
    public ResponseEntity<List<ClientOutputDto>> getClientList(@RequestHeader String token) {
        return clientServicePort.getClientFunction(token);
    }

    @PutMapping("/api/v0/client/{id}")
    public ResponseEntity<ClientOutputDto> updateClient(@RequestBody ClientInputDto clientInputDto,
                                                        @PathVariable("id") String id,
                                                        @RequestHeader String token
    ) {
        return clientServicePort.updateClientFunction(clientInputDto, id, token);
    }

    @DeleteMapping("/api/v0/client/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable("id") String id, @RequestHeader String token) {
        return clientServicePort.deleteClientFunction(id, token);
    }
}
