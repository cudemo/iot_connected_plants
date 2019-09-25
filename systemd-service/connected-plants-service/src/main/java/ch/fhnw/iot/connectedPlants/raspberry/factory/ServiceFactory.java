package ch.fhnw.iot.connectedPlants.raspberry.factory;

import ch.fhnw.iot.connectedPlants.raspberry.service.Service;

public abstract class ServiceFactory {

    public Service getService(String serviceName) {
        if (serviceName == null)
            throw new IllegalArgumentException("serviceName is not specified");

        return createService(serviceName);
    }

    abstract Service createService(String serviceName);
}
