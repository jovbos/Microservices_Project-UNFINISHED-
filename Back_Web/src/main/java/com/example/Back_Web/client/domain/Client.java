package com.example.Back_Web.client.domain;

import javax.validation.constraints.NotNull;

import com.example.Back_Web.employee.domain.Employee;
import com.example.Back_Web.ticket.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

// --- Clientes registrados en la base de datos ---
@Entity(name="Clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    @Column(name = "Client_Id")
    private String clientId;

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
    @Column(unique = true, name="Email")
    private String email;

    @NotNull@NotEmpty
    @Column(name = "Password")
    private String password;

    @OneToOne(mappedBy = "client")
    private Employee employee;

    @OneToMany(mappedBy = "client")
    private List<Ticket> ticketList = new ArrayList<>();
}
