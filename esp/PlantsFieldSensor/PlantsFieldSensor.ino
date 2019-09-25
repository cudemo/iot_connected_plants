// This plants field sensor uses various libraries from Adafruit which are given for free under the The MIT License (MIT)
// Also, this service, developed by @author Imbiscuso Mischa, Cudemo Vito, are also given for free use under the license The MIT License (MIT)
// Copyright (c) 2015 Adafruit Industries
// Copyright (c) 2019 Imbiscuso Mischa, Cudemo Vito
//
// For a full documentation, visit https://github.com/fhnw-iot-4ia/iot-project-iot_plants
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

#include <ChainableLED.h>      // v1.0.0
#include <DHTesp.h>            // v1.0.9
#include <ESP8266WiFi.h>       // v1.0.0
#include <ESP8266MQTTClient.h> // v1.0.4
#include <time.h>              // v2.5.0
#include <ThingSpeak.h>        // v1.5.0
#include "secrets.h"

// Enable or disable DEBUG Mode to sysout to console
bool debug = true;

// WIFI Settings from secrets.h
const char ssid[] = SECRET_SSID;
const char pass[] = SECRET_PASS;
WiFiClient wifiClient;

// ThingSpeak Channelnumber and API Key, from secrets.h
unsigned long myChannelNumber = SECRET_CH_ID;
const char *myWriteAPIKey = SECRET_WRITE_APIKEY;

// MQTT
MQTTClient client;
const char *mqttBrokerHost = MQTT_BROKER_HOST;
const char *mqttPortnumber = MQTT_PORT;
const char *mqttTopic = MQTT_TOPIC;
const char *mqttUser = MQTT_USER;
const char *mqttPassword = MQTT_PASSWORD;
char mqttBrokerUri[64];

/*********** Sensor Connectors ************/
// Humidity & Temperature
#define DHT_PIN 2
#define DHT_TYPE DHT11
DHTesp dht;

// Moisture http://wiki.seeedstudio.com/Grove-Moisture_Sensor/
#define MOISTURE_PIN A0

/*********** Actuator Connectors ************/
#define RGB_PIN 0
#define RGB_PIN2 15
#define NUM_LEDS 5
ChainableLED leds(RGB_PIN, RGB_PIN2, NUM_LEDS);

// Variables to update
float moisture = 0;
float humidity = 0;
float temperature = 0;
byte power = 0;
String myStatus = "";

bool giveSomeWater = false;

// Counter update interval ThingSpeak: ~2000 rounds = 0.5 Minute
int updateThingspeakCounter = 0;
int updateThingspeakRounds = 2000;

void connectWifi()
{
  WiFi.mode(WIFI_STA);

  Serial.print("Attempting to connect to SSID: ");
  Serial.println(ssid);
  while (WiFi.status() != WL_CONNECTED)
  {
    WiFi.begin(ssid, pass);
    Serial.print(".");
    delay(5000);
  }
  Serial.print("Connected, IP = ");
  Serial.println(WiFi.localIP());
}

void updateThinkSpeak()
{
  if (debug)
    Serial.println("ThingSpeak Update initiated");
  // Check for error and return
  if (isnan(humidity) || isnan(temperature))
  {
    return;
  }
  // set the fields with the values
  ThingSpeak.setField(1, moisture);
  ThingSpeak.setField(3, temperature);
  ThingSpeak.setField(4, humidity);
  ThingSpeak.setField(8, giveSomeWater);

  // set the status
  ThingSpeak.setStatus(myStatus);

  int x = ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);

  if (x >= 200 && x < 300)
  {
    Serial.println("Channel update successful.");
  }
  else
  {
    Serial.println("Problem updating channel. HTTP error code " + String(x));
  }
}

void handleConnected()
{
  if (debug)
    Serial.println("Connected to broker");
  client.subscribe(mqttTopic);
}

void handleSubscribed(int topicId)
{
  if (debug)
  {
    Serial.print("Subscribed with topicID ");
    Serial.println(topicId);
  }
}

void handleDataReceived(String topic, String data, bool b)
{
  Serial.print("Received topic: ");
  Serial.print(topic);
  Serial.print(", data: ");
  Serial.println(data);

  giveSomeWater = data == "1";
}

void printToConsole()
{
  // Check for error
  if (isnan(humidity) || isnan(temperature))
  {
    return;
  }

  Serial.print("Moisture = ");
  Serial.println(moisture);
  Serial.print("Temperature = ");
  Serial.println(temperature);
  Serial.print("Humidity = ");
  Serial.println(humidity);
}

void setup()
{
  Serial.begin(115200);
  if (debug)
  {
    Serial.println("Setup started");
  }

  // Setup DHTesp for Temperature and Humidity Sensor
  dht.setup(DHT_PIN, DHTesp::DHT_TYPE);

  // Set timezone
  const int timezone = 0;
  const int dst_off = 0;
  configTime(timezone * 3600, dst_off, "pool.ntp.org", "time.nist.gov");
  // wait for time() being adjusted, as a side effect of configTime
  while (time(NULL) < 28800 * 2)
  {
    delay(500);
  }

  connectWifi();

  ThingSpeak.begin(wifiClient); // Initialize ThingSpeak

  sprintf(mqttBrokerUri, "mqtt://%s:%s@%s:%s", mqttUser, mqttPassword, mqttBrokerHost, mqttPortnumber);
  if (debug)
  {
    Serial.println("Starting MQTT Setup");
    Serial.print("Connecting to ");
    Serial.println(mqttBrokerUri);
  }

  client.onConnect(handleConnected);
  client.onSubscribe(handleSubscribed);
  client.onData(handleDataReceived);
  client.begin(mqttBrokerUri);
}
void loop()
{
  if (WiFi.status() != WL_CONNECTED)
  {
    if (debug)
    {
      Serial.println(wifiClient.available());
      Serial.print(WiFi.status());
      Serial.println(" = status of wifi");
    }
    connectWifi();
  }
  if (giveSomeWater)
  {
    if (debug)
    {
      Serial.println("Have to give some water!");
    }

    for (byte i = 0; i < NUM_LEDS; i++)
    {
      if (i%  2 == 0)
        leds.setColorRGB(i, power, 0, 0);
      else
        leds.setColorRGB(i, 0, 255 - power, 0);
    }
    power += 10;
    delay(10);
  }
  else
  {
    leds.setColorRGB(0, 0, 0, 0);
  }
  moisture = analogRead(MOISTURE_PIN);
  humidity = dht.getHumidity();
  temperature = dht.getTemperature();

  if (debug)
  {
    printToConsole();
  }

  if (updateThingspeakCounter > updateThingspeakRounds)
  {
    updateThinkSpeak();
    updateThingspeakCounter = 0;
  }
  updateThingspeakCounter++;
  client.handle();
}
