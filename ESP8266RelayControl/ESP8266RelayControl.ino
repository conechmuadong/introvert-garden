#include <PubSubClient.h>
#include "TimeLib.h"     // for update/display of time
#include <ESP8266WiFi.h>


#define GPIO5 5
#define GPIO4 4
#define GPIO12 12
#define GPIO13 13

#define WIFI_SSID "K_CNNN"
#define WIFI_PASSWORD "congnghenn"

#define MQTT_PASSW "libe"
#define MQTT_USERNAME "123456"

#define MQTT_SERVER "192.168.1.13"

WiFiClient espClient;
PubSubClient mqttClient(espClient);

void MQTTConfig(){
  mqttClient.setServer(MQTT_SERVER,1883);
  mqttClient.setCallback(callback); 
  reconnect();
}

void connectWifi() {
  Serial.println();
  WiFi.disconnect();
  delay(10);
  Serial.println("Start connect Wifi");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.println("Start program");
  connectWifi();
  MQTTConfig();
  // setTimeDay();
  pinMode(GPIO4, OUTPUT);
  pinMode(GPIO5, OUTPUT);
  digitalWrite(GPIO4, LOW);
  digitalWrite(GPIO5, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(!mqttClient.connected()){
    reconnect();
  }
  if(!mqttClient.loop()){
    mqttClient.connect("ID1408");//,MQTT_USERNAME, MQTT_PASSW);
  }
}
void callback(char* topic, byte* payload, unsigned int length){ //handle message received interupt flag
  Serial.print("Message is received from ");
  Serial.println(topic);
  Serial.print("Message: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
  if (strcmp(topic, "Pump3/Status") == 0)
    if ((char)payload[0]=='1'){
      Serial.println("Turn on Pump 3");
      digitalWrite(GPIO4,HIGH);
    }
    else{
      Serial.println("Turn off Pump 3");
      digitalWrite(GPIO4,LOW);
    }
 if (strcmp(topic, "Pump4/Status") == 0){
   if ((char)payload[0]=='1'){
     Serial.println("Turn on Pump 4");
     digitalWrite(GPIO5,HIGH);
   }
   else{
     Serial.println("Turn off Pump 4");
     digitalWrite(GPIO5,LOW);
   }
 }
}
void reconnect() {
  while (!mqttClient.connected()) {
    Serial.println("Attempting MQTT connection...");
    if (mqttClient.connect("ID1408",MQTT_USERNAME,MQTT_PASSW)) {
      Serial.println("Connected.");
      mqttClient.publish("ESP8266/connection/board", "Connected!");
      mqttClient.subscribe("Pump3/Status");
      mqttClient.subscribe("Pump4/Status");
    } else {
      Serial.print("Conection failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}