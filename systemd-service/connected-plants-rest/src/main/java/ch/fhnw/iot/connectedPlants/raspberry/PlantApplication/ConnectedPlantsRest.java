package ch.fhnw.iot.connectedPlants.raspberry.PlantApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ConnectedPlantsRest extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ConnectedPlantsRest.class, args);
	}

}
