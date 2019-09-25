package ch.fhnw.iot.connectedPlants.raspberry;

public class PlantProperties {
    //THINGSPEAK
    public final static String SERVICE_NAME = "service.name";
    public static final String SERVICE_INTERVALL = "service.intervall";
    public final static String URL_THINGSPEAK_GET_UPDATECHANNEL = "url.thingspeack.get.updateChannel";
    public final static String HEADER_THINGSPEAK_GET_UPDATECHANNEL_NAME1 = "header.thingspeack.get.updateChannel.name1";
    public final static String HEADER_THINGSPEAK_GET_UPDATECHANNEL_VALUE1 = "header.thingspeack.get.updateChannel.value1";

    //MQTT
    public final static String MQTT_URL = "mqtt.url";
    public final static String MQTT_PORT = "mqtt.port";
    public final static String MQTT_CHANNEL = "mqtt.channel";
    public final static String MQTT_USERNAME = "mqtt.username";
    public final static String MQTT_PASSWORD = "mqtt.password";

    public final static String PUSH_ACCESSKEY = "push.accesskey";
    public final static String PUSH_SECRETKEY = "push.secretkey";
    public final static String PUSH_DIFFSEND = "push.diffSend";


    //MONGODB
    public final static String MONGO_DB_NAME = "mongo.db.name";
}
