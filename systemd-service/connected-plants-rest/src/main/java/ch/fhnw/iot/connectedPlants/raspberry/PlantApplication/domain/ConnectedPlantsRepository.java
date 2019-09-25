package ch.fhnw.iot.connectedPlants.raspberry.PlantApplication.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectedPlantsRepository extends MongoRepository<ConnectedPlants, String> {

}
