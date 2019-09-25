package ch.fhnw.iot.connectedPlants.raspberry.service;

import ch.fhnw.iot.connectedPlants.raspberry.entity.ThingSpeakResult;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class CallService {


    private static Logger logger = LogManager.getLogger(CallService.class.getName());

    public static ThingSpeakResult httpGet(String url, List<Header> headers) throws IOException, HttpException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url is not specified");
        }

        logger.info(String.format("Starting Get on: %s", url));

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        if (headers != null && !headers.isEmpty()) {
            for (Header header : headers) {
                request.addHeader(header);
            }
        }

        HttpResponse response = client.execute(request);

        int resultCode = response.getStatusLine().getStatusCode();
        if (resultCode >= 200 && resultCode <= 300) {
            logger.info(String.format("Get was Successfull with Code: %s", resultCode));
        } else {
            logger.error(String.format("Get had an error with Code: %s and Error Message: %s", resultCode, response.getStatusLine().getReasonPhrase()));
            throw new HttpException("Error in Getter");
        }

        ThingSpeakResult result = convert(EntityUtils.toString(response.getEntity()));
        result.resultCode = resultCode;

        return result;
    }

    private static ThingSpeakResult convert(String entity) {
        Gson gson = new Gson();
        return gson.fromJson(entity, ThingSpeakResult.class);
    }
}