package com.example.Back_Web.employee.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    public List<Employee> findByEmail (String email);

    public Boolean existsByEmail (String email);
}
