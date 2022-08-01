package com.example.Back_Empresa.ticket.application;

import com.example.Back_Empresa.client.domain.Client;
import com.example.Back_Empresa.client.domain.ClientRepository;
import com.example.Back_Empresa.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Empresa.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Empresa.config.customErrors.error404.CustomErrorRequest404;
//import com.example.Back_Empresa.config.kafka.KMessageProducer;
import com.example.Back_Empresa.email.application.EmailServicePort;
import com.example.Back_Empresa.employee.domain.Employee;
import com.example.Back_Empresa.employee.domain.EmployeeRepository;
import com.example.Back_Empresa.ticket.domain.Ticket;
import com.example.Back_Empresa.ticket.domain.TicketRepository;
//import com.example.Back_Empresa.ticket.domain.TicketRepositoryImp;
import com.example.Back_Empresa.ticket.domain.TicketRepositoryImp;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketInputDto;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketKafkaDto;
import com.example.Back_Empresa.ticket.infracstructure.dto.TicketOutputDto;
import com.example.Back_Empresa.trip.domain.Trip;
import com.example.Back_Empresa.trip.domain.TripRepository;
import com.example.Back_Empresa.trip.infracstructure.dto.TripOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService implements TicketServicePort {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    TicketRepositoryImp ticketRepositoryImp;

    @Autowired
    EmailServicePort emailServicePort;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<String> createTicket(TicketInputDto ticketInputDto) {

        if (ticketInputDto.getTripId() == null) throw new CustomErrorRequest400("TRIP ID MUST NOT BE NULL");

        // --- Se obtiene el email del cliente loggeado ---
        String emailLogged = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (emailLogged.equals("anonymousUser")) throw new CustomErrorRequest400("PLEASE LOG IN BEFORE MAKING A RESERVATION");

        // --- Se busca los datos del cliente en el repositorio, si es un empleado sin datos de cliente se le crean automaticamente ---
        Client client = new Client();
        if (clientRepository.existsByEmail(emailLogged)) {
            client = (clientRepository.findByEmail(emailLogged).get(0));
        } else if (employeeRepository.existsByEmail(emailLogged)) {
            Employee employee = employeeRepository.findByEmail(emailLogged).get(0);
            client.setClientId(employee.getEmployeeId());
            client.setName(employee.getName());
            client.setSurname(employee.getSurname());
            client.setPhoneNumber(employee.getPhoneNumber());
            client.setEmail(employee.getEmail());
            client.setPassword(employee.getPassword());
            clientRepository.save(client);
            kafkaTemplate.send("clientTopic", client);

            employee.setClient(client);
            employeeRepository.save(employee);
            kafkaTemplate.send("employeeTopic", employee);
        } else throw new CustomErrorRequest404("YOUR PERSONAL DATA IS NOT FOUND");

        // --- Se comprueba que exista el viaje y haya plazas en el bus ---
        Trip trip = tripRepository.findById(ticketInputDto.getTripId()).orElseThrow(() -> new CustomErrorRequest404("TRIP NOT FOUND"));
        if (trip.getSeatsAvailable() < 1) throw new CustomErrorRequest400("NO SEATS AVAILABLE");
        else {

            // --- Se actualiza el numero de plazas disponibles ---
            trip.setSeatsAvailable(trip.getSeatsAvailable()-1);
            tripRepository.save(trip);

            // --- Se construye el ticket y se guarda en el respositorio ---
            Ticket ticket = new Ticket();
            ticket.setTicketId(UUID.randomUUID().toString());
            ticket.setClient(client);
            ticket.setTrip(trip);
            ticket.setSeat(40 - trip.getSeatsAvailable());
            ticket.setDestination(trip.getDestination());
            ticket.setDate(trip.getDate());
            ticket.setTime(trip.getTime());
            ticketRepository.save(ticket);

            // --- Dto para kafka ---
            TicketKafkaDto ticketKafkaDto = new TicketKafkaDto(
                    ticket.getTicketId(),
                    ticket.getClient().getClientId(),
                    ticket.getTrip().getTripId(),
                    ticket.getSeat()
            );
            kafkaTemplate.send("ticketTopic", ticketKafkaDto);

            // --- Se construye el output ---
            String output = "Thank you for using the application, " +
                    client.getName() + ". Your ticket has been successfully created. You will receive a confirmation email soon.";

//            // --- Se envia el email con el ticket al cliente ---
//            emailServicePort.sendEmail(ticketOutputDto);


            return new ResponseEntity<String>(output, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<TicketOutputDto>> getCriteriaTickets(String destination,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                Time timeFrom,
                                                                Time timeTo) {
        return ticketRepositoryImp.ticketsFilter(destination, dateFrom, dateTo, timeFrom, timeTo);
    }


    public ResponseEntity<String> deleteTicketFunction(String id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);

            String output = "The ticket with ID: " + id + " has been remove.";
            kafkaTemplate.send("ticketTopic", "Delete ticket " + id);
            return new ResponseEntity<>(output, HttpStatus.OK);
        } else throw new CustomErrorRequest404("TICKET NOT FOUND");
    }
}
