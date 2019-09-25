package ch.fhnw.iot.connectedPlants.raspberry.PlantApplication.web;

import ch.fhnw.iot.connectedPlants.raspberry.PlantApplication.domain.ConnectedPlants;
import ch.fhnw.iot.connectedPlants.raspberry.PlantApplication.domain.ConnectedPlantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/connected-plants")
@CrossOrigin
public class ConnectedPlantsRestController {

    @Autowired
    private ConnectedPlantsRepository connectedPlantRepository;

    @GetMapping
    @CrossOrigin
    public ResponseEntity<List<ConnectedPlants>> findAll() {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<ConnectedPlants> result = connectedPlantRepository.findAll(sort);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PutMapping(value = "{id}")
    @CrossOrigin
    public ResponseEntity putNewThreshold(@RequestBody ConnectedPlants newPlant, @PathVariable String id) {
        if (newPlant.getId() != null) {
            Optional<ConnectedPlants> toUpdate = connectedPlantRepository.findById(newPlant.getId());
            if (toUpdate.isPresent()) {
                ConnectedPlants result = toUpdate.get();
                result.setThreshold(newPlant.getThreshold());
                result.setLast("01.01.1970 00:00:00");
                connectedPlantRepository.save(result);
            }
        } else {
            ConnectedPlants result = new ConnectedPlants();
            result.setThreshold(newPlant.getThreshold());
            result.setMeasuredMoistureValue(newPlant.getMeasuredMoistureValue());
        }
        return findAll();
    }
}
