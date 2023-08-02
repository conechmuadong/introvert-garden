#include <ArduinoJson.h>
#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>
#include <SPI.h>         // for communication with Ethernet Shield
#include "TimeLib.h"     // for update/display of time
#include <Ethernet.h>    // for communication with NTP Server via UDP
#include <NTPClient.h>
#include <WiFiUdp.h>


#define WIFI_SSID "Duc Canh"
#define WIFI_PASSWORD "123456789"

#define FIREBASE_HOST "https://testfirebase-aceba-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "MDey8L7qdfvtjKcwFNEnVuoDqhuKkEWHwuleIk9n"

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

FirebaseData firebaseData;
FirebaseData fbd0o;
//WiFiClient client
String path = "/";
FirebaseJson json;
String timeNow;
String pathFirebase = "users/field1/measured_data/";

void connectWifi() {
  Serial.printf("Connecting to %s \n", WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("Connected, IP address: ");
  Serial.println(WiFi.localIP());
}

void connectFirebase() {
  Serial.printf("Start connect Firebase: ");
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  if (Firebase.beginStream(firebaseData, path)) {
    Serial.println("REASON: " + firebaseData.errorReason());
  }
  Serial.println("Connect Firecbase success");
  Serial.println();
}

void setDataField( String fieldName, float air_humidity, float radiation, float soil_humidity30, float soil_humidity60, float temperature){
  Serial.println("Start setDataField " + fieldName);
  String measured_data = fieldName + "/measured_data/" + timeNow;
  Firebase.setFloat(firebaseData, measured_data + "/air_humidity",air_humidity);
  Serial.println(measured_data + "/air_humidity : " + air_humidity);
  Firebase.setFloat(firebaseData, measured_data + "/radiation",radiation);
  Serial.println(measured_data + "/radiation : " + radiation);
  Firebase.setFloat(firebaseData, measured_data + "/soil_humidity30",soil_humidity30);
  Serial.println(measured_data + "soil_humidity30 : " + soil_humidity30);
  Firebase.setFloat(firebaseData, measured_data + "/soil_humidity60",soil_humidity60);
  Serial.println(measured_data + "soil_humidity60 : " + soil_humidity60);
  Firebase.setFloat(firebaseData, measured_data + "/temperature", temperature);
  Serial.println(measured_data + "temperation : " + temperature);
  
  Serial.println("End setDataField " + fieldName);
}

void getJsonFirebase() {
  Serial.println("Start getJsonData");
  Serial.println("End getJsonData");
}
void setTimeDay() {
  timeClient.begin();
  // Set offset time in seconds to adjust for your timezone, for example:
  timeClient.setTimeOffset(25200);// GMT +7
  timeClient.update();
  time_t epochTime = timeClient.getEpochTime();
  Serial.print("Epoch Time: ");
  Serial.println(epochTime);

  String formattedTime = timeClient.getFormattedTime();
  Serial.print("Formatted Time: ");
  Serial.println(formattedTime);

  //Get a time structure
  struct tm *ptm = gmtime ((time_t *)&epochTime);

  int monthDay = ptm->tm_mday;
  int currentMonth = ptm->tm_mon + 1;
  int currentYear = ptm->tm_year + 1900;

  //Print complete date:
  String currentDate = String(currentYear) + "-" + String(currentMonth) + "-" + String(monthDay);
  Serial.print("Current date: ");
  Serial.println(currentDate);

  timeNow = currentDate + "/" + formattedTime;
  Serial.print("timeNow: ");
  Serial.println(timeNow);
  delay(5000);
}

void setup() {
  Serial.begin(9600);
  connectWifi();
  connectFirebase();
  setTimeDay();
  setDataField("testUser/field1", 8.14, 2.5, 30.5, 15.5, 31.0); 

 // sleep arduino
  Serial.println("Sleep Arduino");
  ESP.deepSleep(15e6);

 /* printFirebase();
  getJsonFirebase();
    while(!Serial) { }
  // Deep sleep mode for 30 seconds, the ESP8266 wakes up by itself when GPIO 16 (D0 in NodeMCU board) is connected to the RESET pin
    Serial.println("I'm awake, but I'm going into deep sleep mode for 10 seconds");

    if (Firebase.RTDB.getInt(&firebaseData, "/data")) {
        if (firebaseData.dataType() == "int") {
          int intValue = firebaseData.intData();
          Serial.print("data: ");
         Serial.println(intValue);
        }
    }
    if (Firebase.RTDB.getString(&firebaseData, "/name")) {
      if (firebaseData.dataType() == "string") {
        String stringValue = firebaseData.stringData();
        Serial.print("name: ");
        Serial.println(stringValue);
      }
    }
    if (Firebase.RTDB.getJSON(&firebaseData, "/name")) {
      if (firebaseData.dataType() == "string") {
        String stringValue = firebaseData.stringData();
        Serial.print("name: ");
        Serial.println(stringValue);
      }
    }
    ESP.deepSleep(300e6);*/
}

void loop() {
  // put your main code here, to run repeatedly:

}
