package com.example.Back_Empresa.config.kafka;

import com.example.Back_Empresa.client.domain.Client;
import com.example.Back_Empresa.client.domain.ClientRepository;
import com.example.Back_Empresa.employee.domain.Employee;
import com.example.Back_Empresa.employee.domain.EmployeeRepository;
import com.example.Back_Empresa.ticket.domain.Ticket;
import com.example.Back_Empresa.ticket.domain.TicketRepository;
import com.example.Back_Empresa.trip.domain.Trip;
import com.example.Back_Empresa.trip.domain.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Arrays;

@Component
public class KafkaListeners {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    // --- Recibe registros de nuevos viajes como string ---
    @KafkaListener(topics = "tripTopic")
    void tripListener(String tripString) {

        // --- Se comprueba si se tienen que borrar o guardar un registro ---
        if (tripString.split("[\" ]")[1].equals("Delete")) {
            if (tripRepository.existsById(tripString.split("[\" ]")[3]))
            {
                String id = tripString.split("[\" ]")[3];
                tripRepository.deleteById(id);
                System.out.println("Trip with ID: " + id + " has been removed.");
            }
        } else {

            // --- Separo cada campo del string ---
            String[] stringList = tripString.split("\":\"|\",\"|\":|,\"|\"");

            System.out.println("RECEIVED TRIP: " + tripString);

            // --- Separo cada numero del string de la hora para parsearlos luego a int ---
            String time = stringList[8];
            String[] timeList = time.split(":");

            // --- Separo tambien los numeros de la fecha ---
            LocalDate localDate = LocalDate.of(1, 1, 1);
            String date = stringList[6];
            String[] dateList = date.split("[,\\]\\[]");


            // --- Construyo el registro del viaje ---
            Trip trip = new Trip(
                    stringList[2],
                    stringList[4],
                    localDate.of(Integer.parseInt(dateList[1]), Integer.parseInt(dateList[2]), Integer.parseInt(dateList[3])),
                    new Time(Integer.parseInt(timeList[0]), Integer.parseInt(timeList[1]), Integer.parseInt(timeList[2])),
                    Integer.parseInt(stringList[10]),
                    null
            );
            tripRepository.save(trip);
            System.out.println("REGISTERED TRIP: " + tripString);
        }
    }

    // --- Recibo datos de reserva y se registra ---
    @KafkaListener(topics = "ticketTopic")
    void ticketListener(String ticketString) {

        // --- Se comprueba si se tienen que borrar o guardar un registro ---
        if (ticketString.split("[\" ]")[1].equals("Delete")) {
            System.out.println(ticketString.split("[\" ]")[3]);
            if (ticketRepository.existsById(ticketString.split("[\" ]")[3])) {
                String id = ticketString.split("[\" ]")[3];
                ticketRepository.deleteById(id);
                System.out.println("Ticket with ID: " + id + " has been removed.");
            }
        } else {
            String[] stringList = ticketString.split("\":\"|\",\"|\":|,\"|\"|}");

            System.out.println("RECEIVED TICKET: " + ticketString);

            Trip trip = tripRepository.findById(stringList[6]).get();
            Client client = clientRepository.findById(stringList[4]).get();
            Ticket ticket = new Ticket(
                    stringList[2],
                    trip,
                    client,
                    Integer.parseInt(stringList[8]),
                    null,
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime()
            );
            ticketRepository.save(ticket);
            System.out.println("REGISTERED TICKET: " + ticketString);
        }
    }


    // --- Recibo cliente como string y divido los datos que necesito ---
    @KafkaListener(topics = "clientTopic")
    void clientListener(String clientString) {

        // --- Se comprueba si se tienen que borrar o guardar un registro ---
        if (clientString.split("[\" ]")[1].equals("Delete")) {
            if (clientRepository.existsById(clientString.split("[\" ]")[3])) {
                String id = clientString.split("[\" ]")[3];
                clientRepository.deleteById(id);
                System.out.println("Client with ID: " + id + " has been removed.");
            }
        } else {
            String[] stringList = clientString.split("\":\"|\",\"|\":|,\"|\"|}");
            System.out.println("RECEIVED CLIENT: " + clientString);

            Client client = new Client(
                    stringList[2],
                    stringList[4],
                    stringList[6],
                    Integer.parseInt(stringList[8]),
                    stringList[10],
                    stringList[12],
                    null,
                    null
            );
            clientRepository.save(client);
        }
    }

    @KafkaListener(topics = "employeeTopic")
    void employeeListener(String employeeString) {

        // --- Se comprueba si se tienen que borrar o guardar un registro ---
        if (employeeString.split("[\" ]")[1].equals("Delete")) {
            if (employeeRepository.existsById(employeeString.split("[\" ]")[3])) {
                String id = employeeString.split("[\" ]")[3];
                employeeRepository.deleteById(id);
                System.out.println("Employee with ID: " + id + " has been removed.");
            }
        } else {
            String[] stringList = employeeString.split("\":\"|\",\"|\":|,\"|\"|}");
            System.out.println("RECEIVED EMPLOYEE: " + employeeString);

            if (Arrays.stream(stringList).count() == 17) {
                Employee employee = new Employee(
                        stringList[2],
                        stringList[4],
                        stringList[6],
                        Integer.parseInt(stringList[8]),
                        stringList[10],
                        stringList[12],
                        Boolean.parseBoolean(stringList[14]),
                        null
                );
                employeeRepository.save(employee);
            } else {
                Employee employee = new Employee(
                        stringList[2],
                        stringList[4],
                        stringList[6],
                        Integer.parseInt(stringList[8]),
                        stringList[10],
                        stringList[12],
                        Boolean.parseBoolean(stringList[14]),
                        clientRepository.findById(stringList[18]).get()
                );
                employeeRepository.save(employee);
                System.out.println("REGISTERED EMPLOYEE: " + employeeString);
            }
        }
    }
}
