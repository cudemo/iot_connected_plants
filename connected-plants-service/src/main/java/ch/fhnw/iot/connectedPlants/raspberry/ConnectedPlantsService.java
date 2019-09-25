package ch.fhnw.iot.connectedPlants.raspberry;

import ch.fhnw.iot.connectedPlants.raspberry.factory.ServiceFactory;
import ch.fhnw.iot.connectedPlants.raspberry.factory.ThingSpeakFactory;
import ch.fhnw.iot.connectedPlants.raspberry.service.Service;
import ch.fhnw.iot.connectedPlants.raspberry.util.ServiceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebListener;
import java.util.Properties;

@WebListener
public class ConnectedPlantsService {
    private static Logger logger = LogManager.getLogger(ConnectedPlantsService.class.getName());


    public static void main(String[] args) {
        logger.info("Application started");

        try {
            initServices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initServices() throws Exception {

        logger.info("Start refresh service");
        Properties props = ServiceUtil.loadProperty();

        ServiceFactory serviceFactory = new ThingSpeakFactory();
        Service thingSpeakService = serviceFactory.getService((String) props.get(PlantProperties.SERVICE_NAME));


        while (true) {
            try {
                // Pullt Daten von Matlab und verarbeitet diese
                thingSpeakService.runService();
                int intervall = Integer.parseInt(props.getProperty(PlantProperties.SERVICE_INTERVALL));
                Thread.sleep(intervall);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw e;
            }
        }
    }

}
