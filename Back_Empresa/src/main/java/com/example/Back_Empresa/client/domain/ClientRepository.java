package com.example.Back_Empresa.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, String> {
    public List<Client> findByEmail(String email);

    public Boolean existsByEmail(String email);
}
