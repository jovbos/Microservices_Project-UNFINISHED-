package com.example.Back_Web.config;

import com.example.Back_Web.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Web.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Web.ticket.infracstructure.dto.TicketOutputDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LoginController {

    // --- Hace una peticion al login de empresa ---
    @GetMapping("/api/v0/login")
    public ResponseEntity<String> login(@RequestParam("email") String email,
                                        @RequestParam("password") String password) {
        try {
            ResponseEntity<String> responseEntity =
                    new RestTemplate().getForEntity("http://localhost:8080/api/v0/login?email=" + email
                            + "&password=" + password, String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("NAME AND/OR PASSWORD ARE WRONG");
        }
        ResponseEntity<String> responseEntity =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/login?email=" + email
                        + "&password=" + password, String.class);
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }
}
