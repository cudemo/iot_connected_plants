package ch.fhnw.iot.connectedPlants.raspberry.util;

import ch.fhnw.iot.connectedPlants.raspberry.PlantProperties;
import ch.fhnw.iot.connectedPlants.raspberry.service.Service;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class ServiceUtil {

    private static final Properties props = loadProperty();
    private static final String dbName = props.getProperty(PlantProperties.MONGO_DB_NAME);

    private static MongoCollection<Document> collection;

    public static Properties loadProperty() {
        Properties result = new Properties();
        try {
            //load a properties file from class path, inside static method
            result.load(Service.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (
                IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static Document getDocument() {


        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        Document result = null;

        while (it.hasNext()) {
            result = (Document) it.next();
        }

        return result;
    }

    public static void saveDocument(Document toSave, double moisture, String mqtt) {
        if (toSave == null) {
            throw new IllegalArgumentException("toSave is not specified");
        }

        Object id = toSave.get("_id");

        collection.updateOne(Filters.eq("_id",id), Updates.set("measuredMoistureValue", moisture));
        collection.updateOne(Filters.eq("_id",id), Updates.set("mqtt", mqtt));
    }

    public static void saveDocument(Document toSave, String lastEx) {
        if (toSave == null) {
            throw new IllegalArgumentException("toSave is not specified");
        }

        Object id = toSave.get("_id");

        collection.updateOne(Filters.eq("_id",id), Updates.set("last", lastEx));
    }

    static {

        // Creating a Mongo client
        MongoClient mongo = new MongoClient("localhost", 27017);

        // Accessing the database
        MongoDatabase database = mongo.getDatabase(dbName);

        // Retrieving a collection collectionName in this case == dbName
        collection = database.getCollection(dbName);
    }
}
