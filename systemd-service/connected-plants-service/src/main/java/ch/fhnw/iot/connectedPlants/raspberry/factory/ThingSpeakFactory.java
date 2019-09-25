package ch.fhnw.iot.connectedPlants.raspberry.factory;

import ch.fhnw.iot.connectedPlants.raspberry.service.Service;
import ch.fhnw.iot.connectedPlants.raspberry.service.ThingSpeakService;
import ch.fhnw.iot.connectedPlants.raspberry.service.observer.MQTTService;
import ch.fhnw.iot.connectedPlants.raspberry.service.observer.ObserverObject;
import ch.fhnw.iot.connectedPlants.raspberry.service.observer.PushService;

import java.util.Arrays;

public class ThingSpeakFactory extends ServiceFactory {
    @Override
    Service createService(String serviceName) {
        if (serviceName.contains("ThingSpeak")) {
            ObserverObject mqttService = new MQTTService();
            return new ThingSpeakService(Arrays.asList(mqttService));
        }
        return null;
    }
}
