package com.example.Back_Empresa.config.security;


import com.example.Back_Empresa.client.domain.ClientRepository;
import com.example.Back_Empresa.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Empresa.employee.domain.Employee;
import com.example.Back_Empresa.employee.domain.EmployeeRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SecurityController {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    private final String PREFIX = "Bearer ";
    private final String SECRET = "mySecretKey";


    // --- Se comprueba si existe el usuario en las tablas de cliente y empleado(y si este es admin) ---
    // --- Primero comprobara que sea empleado, ya que puede ser empleado y cliente al mismo tiempo ---
    @PostMapping("/api/v0/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password) {

        if (!employeeRepository.findByEmail(email).isEmpty()) {
            Employee employee = employeeRepository.findByEmail(email).get(0);
            if (password.equals(employee.getPassword())) {
                if (employee.getAdmin().equals(true)) {
                    return getJWTToken(email, "ROLE_ADMIN");
                } else return getJWTToken(email, "ROLE_USER");
            } else throw new CustomErrorRequest403("WRONG PASSWORD");
        } else if (!clientRepository.findByEmail(email).isEmpty()) {
            if (password.equals(clientRepository.findByEmail(email).get(0).getPassword())) {
                return getJWTToken(email, "ROLE_USER");
            } else throw new CustomErrorRequest403("WRONG PASSWORD");
        } else throw new CustomErrorRequest403("NO USERS FOUND FOR THIS EMAIL");
    }

    // --- Se genera el tokken que devuelve el login segun los datos introducidos ---
    private String getJWTToken(String username, String role) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList(role);

        String token = Jwts
                .builder()
                .setId("JWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }

    // --- Endpoint para comprobar la validez de un token desde el Back Web ---
    // --- Devuelve el email del usuario registrado con ese token ---
    @GetMapping("/api/v0/token")
    public ResponseEntity<String> checkToken(String token){

        // --- Se comprueba si el token es valido ---
        try {
            return new ResponseEntity<>(validateToken(token).getSubject(), HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }
    }

    public Claims validateToken(String token) {
        String jwtToken = token.replace(PREFIX, "");
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
    }
}