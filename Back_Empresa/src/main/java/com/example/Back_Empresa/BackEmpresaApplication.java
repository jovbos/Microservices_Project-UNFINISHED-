package com.example.Back_Empresa;

import com.example.Back_Empresa.client.domain.Client;
import com.example.Back_Empresa.client.domain.ClientRepository;
import com.example.Back_Empresa.employee.domain.Employee;
import com.example.Back_Empresa.employee.domain.EmployeeRepository;
import com.example.Back_Empresa.trip.domain.Trip;
import com.example.Back_Empresa.trip.domain.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class BackEmpresaApplication {
	@Autowired
	ClientRepository clientRepository;

	@Autowired
	TripRepository tripRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	public static void main(String[] args) {
		SpringApplication.run(BackEmpresaApplication.class, args);
	}

	// --- Viajes por defecto para probar la aplicacion ---
	@PostConstruct
	public void defaultTrips() throws Exception {
		LocalDate localDate = LocalDate.of(1, 1, 1);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(new Trip("e448bd8a-ec76-41ab-bfcc-7fed98963593", "Valencia", localDate.of(2022, 7, 5), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("4846a7f1-1ccb-452b-9435-5c4561204b2e", "Valencia", localDate.of(2022, 7, 28), new Time(14, 0, 0), 40, null));
		tripList.add(new Trip("ece8e999-baf6-4d7c-80aa-d4144245952f", "Valencia", localDate.of(2022, 8, 17), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("5cfb5f96-5e43-469b-855c-08d9c8ab5051", "Valencia", localDate.of(2022, 8, 17), new Time(16, 0, 0), 40, null));
		tripList.add(new Trip("df77f008-6f5f-40c5-9a17-ae93d7c9b78e", "Valencia", localDate.of(2022, 9, 3), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("2287e38d-3c78-4e83-a309-63647e04e3b8", "Madrid", localDate.of(2022, 7, 8), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("1752710e-67ee-40fb-a752-2c661cbc16f5", "Madrid", localDate.of(2022, 7, 8), new Time(14, 0, 0), 40, null));
		tripList.add(new Trip("24c4481b-4225-41d5-ad66-a554f1eebfce\t", "Madrid", localDate.of(2022, 8, 17), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("5d1e7edd-f52f-47c0-810d-ea4e8da43023", "Madrid", localDate.of(2022, 8, 23), new Time(16, 0, 0), 40, null));
		tripList.add(new Trip("adb1459b-ba72-465e-947c-f735c8dcae1f", "Madrid", localDate.of(2022, 9, 17), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("609c0bcc-6c83-4c42-90a3-00460263380c", "Barcelona", localDate.of(2022, 7, 5), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("f0983007-c7d0-4ca4-9751-8dd0081cffa1", "Barcelona", localDate.of(2022, 7, 5), new Time(14, 0, 0), 40, null));
		tripList.add(new Trip("db765896-58df-4c71-a0cf-6124f957eeb7", "Barcelona", localDate.of(2022, 8, 17), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("e7abbe48-40e7-4ce3-8d12-407659c356c1\t", "Barcelona", localDate.of(2022, 8, 17), new Time(12, 0, 0), 40, null));
		tripList.add(new Trip("62f12ed3-1484-4f9b-9783-8a243503ba16", "Barcelona", localDate.of(2022, 9, 17), new Time(16, 0, 0), 40, null));
		tripList.add(new Trip("2e71a934-baed-40d6-99d0-ab1a0e079e88", "Bilbao", localDate.of(2022, 7, 5), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("e55ed2a8-9701-4c48-8b64-1b95e6e2d555", "Bilbao", localDate.of(2022, 8, 5), new Time(14, 0, 0), 40, null));
		tripList.add(new Trip("9c569da6-bdec-482d-9ee5-e76dd2dee219", "Bilbao", localDate.of(2022, 8, 17), new Time(9, 0, 0), 40, null));
		tripList.add(new Trip("47e409af-b582-4505-acb0-998493ec483b", "Bilbao", localDate.of(2022, 8, 23), new Time(12, 0, 0), 40, null));
		tripList.add(new Trip("cd7b8f97-da1d-48e3-bcd4-a58bee23a57f", "Bilbao", localDate.of(2022, 9, 5), new Time(16, 0, 0), 40, null));
		tripRepository.saveAll(tripList);
		System.out.println(tripList);
	}

	// --- Cliente por defecto, estos podran comprar tickets ---
	@PostConstruct
	public void defaultClient() {
		Client client = new Client("472011e3-6f0e-4c38-8ee3-80fff6cfb81e", "Tallarines", "Calientes", 123123123, "tallarines880@gmail.com", "secret", null, null);
		clientRepository.save(client);
	}

	// --- Empleado admin por defecto, este podra acceder al resto de endpoints ---
	@PostConstruct
	public void defaultAdmin() {
		Employee admin = new Employee("c72ab7b7-523d-4349-810d-33950dcd9dc4", "admin", "default", 666666666, "admin@hotmail.com", "secret", true, null);
		employeeRepository.save(admin);
	}

	// --- Las personas sin loggear podran acceder a la lista de viajes disponibles ---}



//	@Bean
//	CommandLineRunner commandLineRunner2(KafkaTemplate<String, Object> kafkaTemplate) {
//		return args -> {
//			kafkaTemplate.send("employeeTopic", new Employee("c72ab7b7-523d-4349-810d-33950dcd9dc4", "admin", "default", 666666666, "admin@hotmail.com", "secret", true, null));
//		};
//	}
}