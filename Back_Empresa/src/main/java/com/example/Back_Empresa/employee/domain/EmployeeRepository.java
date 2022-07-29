package com.example.Back_Empresa.employee.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    public List<Employee> findByEmail (String email);

    public Boolean existsByEmail (String email);
}
