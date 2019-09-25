package ch.fhnw.iot.connectedPlants.raspberry.service.observer;

import ch.fhnw.iot.connectedPlants.raspberry.PlantProperties;
import ch.fhnw.iot.connectedPlants.raspberry.util.ServiceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class MQTTService implements ObserverObject {
    private final static Logger logger =  LogManager.getLogger(MQTTService.class.getName());

    private final String userName;
    private final String password;
    private final String url;
    private final String port;
    private final String channel;

    public MQTTService() {
        Properties properties = ServiceUtil.loadProperty();
        this.userName = properties.getProperty(PlantProperties.MQTT_USERNAME);
        this.password = properties.getProperty(PlantProperties.MQTT_PASSWORD);
        this.url = properties.getProperty(PlantProperties.MQTT_URL);
        this.port = properties.getProperty(PlantProperties.MQTT_PORT);
        this.channel = properties.getProperty(PlantProperties.MQTT_CHANNEL);
    }

    @Override
    public void run() throws Exception {
        try {
            logger.info(String.format("Started MQQT Service on URL: %s, Port: %s", url, port));
            String serviceURI = url + ":" + port;
            MqttClient client = new MqttClient(serviceURI, MqttClient.generateClientId());
            MqttConnectOptions connOpts = setUpConnectionOptions(userName, password);
            client.connect(connOpts);
            MqttMessage message = new MqttMessage();
            message.setPayload(getChannelInfos().getBytes());
            client.publish(channel, message);
            client.disconnect();
            logger.info(String.format("Finished MQQT Service on URL: %s, Port: %s", url, port));
        } catch (
                MqttException e) {
            logger.error(String.format("Error during MQQT Service  on URL: %s, Port: %s with Error %s", url, port, e.getMessage()));
            throw new Exception(e);
        }
    }

    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }

    private String getChannelInfos() {
        Document document = ServiceUtil.getDocument();
        Set<Map.Entry<String, Object>> set = document.entrySet();

        AtomicReference<String> mqtt = new AtomicReference<>();
        set.forEach(v -> {
            if (v.getKey().equals("mqtt")) {
                mqtt.set((String) v.getValue());
            }
        });
        return mqtt.get();
    }
}
