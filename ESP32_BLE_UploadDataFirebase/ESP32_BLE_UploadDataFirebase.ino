#include "BLEDevice.h"
#include <Wire.h>
#include <stdlib.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include "TimeLib.h"
#include <NTPClient.h>
#include "WiFiUdp.h"

//BLE Server name (the other ESP32 name running the server sketch)
#define bleServerName "Blinky Example"

/* UUID's of the service, characteristic that we want to read*/
// BLE Service
static BLEUUID bgmServiceUUID("de8a5aac-a99b-c315-0c80-60d4cbb51224");

// BLE Characteristics
static BLEUUID temperatureCharacteristicUUID("e975b265-56bb-4ebb-b732-4bd809afa7a7");

// Soil Humidity Characteristic
static BLEUUID soil_humidityCharacteristicUUID("02a2923c-911c-47c1-9544-8a2f2b21c68e");

// Air humidity
static BLEUUID air_humidityCharacteristicUUID("b0a0ce9f-53c7-4696-b6ce-52ddc6e679cd");

// Radiation
static BLEUUID radiationCharacteristicUUID("61a885a4-41c3-60d0-9a53-6d652a70d29c");

//Flags stating if should begin connecting and if the connection is up
static boolean doConnect = false;
static boolean connected = false;

//Address of the peripheral device. Address will be found during scanning...
static BLEAddress *pServerAddress;
 
//Characteristicd that we want to read
static BLERemoteCharacteristic* temperatureCharacteristic;
static BLERemoteCharacteristic* soil_humidityCharacteristic;
static BLERemoteCharacteristic* air_humidityCharacteristic;
static BLERemoteCharacteristic* radiationCharacteristic;

//Activate notify
const uint8_t notificationOn[] = {0x1, 0x0};
const uint8_t notificationOff[] = {0x0, 0x0};

//Variables to store temperature and humidity
uint16_t* temperatureRaw = (uint16_t*)calloc(2,sizeof(uint8_t));
uint32_t* soil_humidityRaw = (uint32_t*)calloc(4,sizeof(uint8_t));
uint16_t* air_humidityRaw = (uint16_t*)calloc(2,sizeof(uint8_t));
uint16_t* radiationRaw = (uint16_t*)calloc(2,sizeof(uint8_t));

//Flags to check whether new temperature and humidity readings are available
boolean newTemperature = false;
boolean newSoilHumidity = false;
boolean newAirHumidity = false;
boolean newRadiation = false;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

//Define the API key and RTDB URL
#define FIREBASE_HOST "https://esp32-a353d-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "S6hdsUp7SarRX3QxQP2iphDJIjHM4dEtGlAUqKBf"

// Define Firebase Data object
FirebaseData firebaseData;

//Define the WiFi credentials
const char* ssid = "CUONG DOLA T3";
const char* password = "V12397195T3";

String timeNow;
unsigned long dataMillis = 0;

//Connect to the BLE Server that has the name, Service, and Characteristics
bool connectToServer(BLEAddress pAddress) {
   BLEClient* pClient = BLEDevice::createClient();
 
  // Connect to the remove BLE Server.
  pClient->connect(pAddress);
  Serial.println(F(" - Connected to server"));
 
  // Obtain a reference to the service we are after in the remote BLE server.
  BLERemoteService* pRemoteService = pClient->getService(bgmServiceUUID);
  if (pRemoteService == nullptr) {
    Serial.print(F("Failed to find our service UUID: "));
    Serial.println(bgmServiceUUID.toString().c_str());
    return (false);
  }
 
  // Obtain a reference to the characteristics in the service of the remote BLE server.
  temperatureCharacteristic = pRemoteService->getCharacteristic(temperatureCharacteristicUUID);
  soil_humidityCharacteristic = pRemoteService->getCharacteristic(soil_humidityCharacteristicUUID);
  air_humidityCharacteristic = pRemoteService->getCharacteristic(air_humidityCharacteristicUUID);
  radiationCharacteristic = pRemoteService->getCharacteristic(radiationCharacteristicUUID);

  if (temperatureCharacteristic == nullptr || soil_humidityCharacteristic == nullptr 
       || air_humidityCharacteristic == nullptr || radiationCharacteristic == nullptr) 
   {
    Serial.print(F("Failed to find our characteristic UUID"));
    return false;
  }
  Serial.println(F(" - Found our characteristics"));
 
  //Assign callback functions for the Characteristics
  temperatureCharacteristic->registerForNotify(temperatureNotifyCallback);
  soil_humidityCharacteristic->registerForNotify(soil_humidityNotifyCallback);
  air_humidityCharacteristic->registerForNotify(air_humidityNotifyCallback);
  radiationCharacteristic->registerForNotify(radiationNotifyCallback);
  return true;
}

//Callback function that gets called, when another device's advertisement has been received
class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    if (advertisedDevice.getName() == bleServerName) { //Check if the name of the advertiser matches
      advertisedDevice.getScan()->stop(); //Scan can be stopped, we found what we are looking for
      pServerAddress = new BLEAddress(advertisedDevice.getAddress()); //Address of advertiser is the one we need
      doConnect = true; //Set indicator, stating that we are ready to connect
      Serial.println(F("Device found. Connecting!"));
    }
  }
};
 
//When the BLE Server sends a new temperature reading with the notify property
static void temperatureNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                        uint8_t* pData, size_t length, bool isNotify) {
  //store temperature value
  *temperatureRaw = (uint16_t)*pData|(uint16_t)*(pData+1)<<8;
  newTemperature = true;
}

//When the BLE Server sends a new soil_humidity reading with the notify property
static void soil_humidityNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                    uint8_t* pData, size_t length, bool isNotify) {
  //store humidity value
  *soil_humidityRaw = (uint32_t)*pData|(uint32_t)*(pData+1)<<8|(uint32_t)*(pData+2)<<16|(uint32_t)*(pData+3)<<24;
  newSoilHumidity = true;
}


//When the BLE Server sends a new air_humidity reading with the notify property
static void air_humidityNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                    uint8_t* pData, size_t length, bool isNotify) {
  //store air humidity value
  *air_humidityRaw = (uint16_t)*pData|(uint16_t)*(pData+1)<<8;
  newAirHumidity = true;
}

//When the BLE Server sends a new radiation reading with the notify property
static void radiationNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                    uint8_t* pData, size_t length, bool isNotify) {
  //store radiation value
  *radiationRaw = (uint16_t)*pData|(uint16_t)*(pData+1)<<8;
  newRadiation = true;
}

void printReadings(){
  Serial.print(F("Temperature: "));
  Serial.print(*temperatureRaw);
 
  Serial.print(F("\nSoil Humidity: "));
  Serial.print(*soil_humidityRaw); 

  Serial.print(F("\nAir Humidity: "));
  Serial.print(*air_humidityRaw); 

  Serial.print(F("\nRadiation: "));
  Serial.print(*radiationRaw); 
}

//Connect to WiFi with given SSID and password
void connectWifi() {
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println(F("Wifi connected"));
}

//Connect to Firebase with given RTDB URL and API Key
void connectFirebase() {
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
}

//Getting current time for uploading data
void setTimeDay() {
  timeClient.begin();
  // Set offset time in seconds to adjust for your timezone, for example:
  timeClient.setTimeOffset(25200);// GMT +7
  timeClient.update();
  time_t epochTime = timeClient.getEpochTime();
  Serial.print(F("Epoch Time: "));
  Serial.println(epochTime);

  String formattedTime = timeClient.getFormattedTime();
  Serial.print(F("Formatted Time: "));
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

void setDataField(float air_humidity, float radiation, float soil_humidity, float temperature) {
  String path = "/testUser/field1/" + timeNow;
  Firebase.setFloat(firebaseData, path + "/air_humidity", air_humidity);
  Firebase.setFloat(firebaseData, path + "/radiation", radiation);
  Firebase.setFloat(firebaseData, path + "/soil_humidity", soil_humidity);
  Firebase.setFloat(firebaseData, path + "/temperature", temperature);
}

void setup() {
  //Start serial communication
  Serial.begin(115200);
  Serial.println(F("Starting Arduino BLE Client application..."));

  //Init BLE device
  BLEDevice::init("");
 
  //Retrieve a Scanner and set the callback we want to use to be informed when we
  //have detected a new device. Specify that we want active scanning and start the
  //scan to run for 30 seconds.
  BLEScan* pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setActiveScan(true);
  pBLEScan->start(30);

  //Connect to WiFi and Firebase
  connectWifi();
  connectFirebase();
}

void loop() {
  //here we repeat the loop every 10s
  //If the flag "doConnect" is true then we have scanned for and found the desired
  //BLE Server with which we wish to connect. Now we connect to it. Once we are
  //connected we set the connected flag to be true.
  if (millis() - dataMillis > 10000 && doConnect == true) {
    if (connectToServer(*pServerAddress)) {
      Serial.println(F("We are now connected to the BLE Server."));
      //Activate the Notify property of each Characteristic
      temperatureCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
      soil_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
      air_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
      radiationCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
      connected = true;
    } 
    else {
      Serial.println(F("We have failed to connect to the server; Restart your device to scan for nearby BLE server again."));
    }
    doConnect = false;

    //set the current time  
    dataMillis = millis();
    setTimeDay();
  
    //if new characteristic readings are available, print and upload them to firebase
    if (newTemperature && newSoilHumidity && newAirHumidity && newRadiation){
      newTemperature = false;
      newSoilHumidity = false;
      newAirHumidity = false;
      newRadiation = false;
      printReadings();
    }
    float radiation = (float)*radiationRaw * 0.0185 / 0.42;
    float soil_humid = (float)*soil_humidityRaw;
    float air_humid = (float)*air_humidityRaw * 125 / 65536 - 6;
    float temperature = (float)*temperatureRaw * 155.72 / 65536 - 46.85; 
    setDataField(air_humid, radiation, soil_humid, temperature);
    Serial.println(F("Set data done!"));
  }
}
