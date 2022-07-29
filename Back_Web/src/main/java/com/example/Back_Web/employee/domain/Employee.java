package com.example.Back_Web.employee.domain;

import com.example.Back_Web.client.domain.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

// --- Clase para acceder a los endpoints a los cuales no deberian poder acceder los clientes ---
// --- El empleado debe ser admin para acceder a estos endpoints ---
@Entity(name = "Employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @Column(name = "Employee_Id")
    private String employeeId;

    @NotNull@NotEmpty
    @Column(name="Name")
    private String name;

    @NotNull@NotEmpty
    @Column(name="Surname")
    private String surname;

    @NotNull@NotEmpty
    @Column(name="PhoneNumber")
    private Integer phoneNumber;

    @NotNull@NotEmpty
    @Column(unique=true, name="Email")
    private String email;

    @NotNull@NotEmpty
    @Column(name = "Password")
    private String password;

    @NotNull@NotEmpty
    @Column
    private Boolean admin;

    @OneToOne
    @JoinColumn(name="Client_Id")
    private Client client;
}
