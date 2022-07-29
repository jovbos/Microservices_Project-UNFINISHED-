package com.example.Back_Web.ticket.application;

import com.example.Back_Web.client.domain.Client;
import com.example.Back_Web.client.domain.ClientRepository;
//import com.example.Back_Web.utilities.Principal;
import com.example.Back_Web.client.infracstructure.dto.ClientDataOutputDto;
import com.example.Back_Web.config.customErrors.error403.CustomErrorRequest403;
import com.example.Back_Web.config.customErrors.error404.CustomErrorRequest404;
import com.example.Back_Web.employee.domain.Employee;
import com.example.Back_Web.employee.domain.EmployeeRepository;
import com.example.Back_Web.ticket.domain.TicketRepositoryImp;
import com.example.Back_Web.ticket.infracstructure.dto.TicketKafkaDto;
import com.example.Back_Web.trip.infracstructure.dto.TripOutputDto;
import com.example.Back_Web.config.customErrors.error400.CustomErrorRequest400;
import com.example.Back_Web.ticket.domain.Ticket;
import com.example.Back_Web.ticket.domain.TicketRepository;
import com.example.Back_Web.trip.domain.Trip;
import com.example.Back_Web.trip.domain.TripRepository;
import com.example.Back_Web.ticket.infracstructure.dto.TicketInputDto;
import com.example.Back_Web.ticket.infracstructure.dto.TicketOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.time.LocalDate;
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
    ModelMapper modelMapper;

    @Autowired
    TicketRepositoryImp ticketRepositoryImp;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseEntity<TicketOutputDto> createTicket(TicketInputDto ticketInputDto, String token) {

        // --- Se valida el token aportado y se recibe el mail del usuario loggeado ---
        try {
            new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                            String.class);
        } catch (Exception e) {
            throw new CustomErrorRequest403("INVALID TOKEN");
        }
        ResponseEntity<String> responseToken =
                new RestTemplate().getForEntity("http://localhost:8080/api/v0/token?token=" + token,
                        String.class);
        String emailLogged = responseToken.getBody();

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
        // --- Para las plazas se comprueba Back Empresa y luego actualiza la database propia ---
        Trip trip = tripRepository.findById(ticketInputDto.getTripId()).orElseThrow(() -> new CustomErrorRequest400("TRIP NOT FOUND"));
        try {
            ResponseEntity<Integer> responseSeats =
                    new RestTemplate().getForEntity("http://localhost:8080/api/v0/trip/seats/" + trip.getTripId(), Integer.class);
            trip.setSeatsAvailable(responseSeats.getBody());
        } catch (Exception e) {
            throw new CustomErrorRequest404("TRIP NOT FOUND");
        }
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

            // --- Peticion a Back Empresa para enviar mail de confirmacion ---
            TicketOutputDto ticketOutputDto = modelMapper.map(ticket, TicketOutputDto.class);
            ticketOutputDto.setClientData(modelMapper.map(client, ClientDataOutputDto.class));
            ticketOutputDto.setTripData(modelMapper.map(trip, TripOutputDto.class));
            try {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                ResponseEntity<TicketOutputDto> responseEntity =
                        new RestTemplate().postForEntity("http://localhost:8080/api/v0/email",
                                ticketOutputDto, TicketOutputDto.class, httpHeaders);
            } catch (Exception e) {
                throw new CustomErrorRequest400("YOUR TICKET READY BUT CONFIRMATION EMAIL FAILED. " +
                        "INTRODUCE YOUR TICKET ID: " + ticket.getTicketId() +
                        " IN PUT:localhost:8080/api/v0/email TO GET A CONFIRMATION EMAIL.");
            }

            return new ResponseEntity<>(ticketOutputDto, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<TicketOutputDto>> getCriteriaTickets(String destination,
                                                                    LocalDate dateFrom,
                                                                    LocalDate dateTo,
                                                                    Time timeFrom,
                                                                    Time timeTo,
                                                                    String token) {
        return ticketRepositoryImp.ticketsFilter(destination, dateFrom, dateTo, timeFrom, timeTo);
    }


}
