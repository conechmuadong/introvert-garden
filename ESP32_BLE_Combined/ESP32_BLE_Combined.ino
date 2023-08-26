#include <WiFi.h>
#include "BLEDevice.h"
#include <stdlib.h>

#include <FirebaseESP32.h>
#include "TimeLib.h"
#include <NTPClient.h>
#include "WiFiUdp.h"

// Wifi - Firebase
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

//Define the API key and RTDB URL
#define FIREBASE_HOST "https://testfirebase-aceba-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "MDey8L7qdfvtjKcwFNEnVuoDqhuKkEWHwuleIk9n"

// Define Firebase Data object
FirebaseData firebaseData;

//Path to irrigationCheck boolean
String irriPath = "/testUser/field1/irrigation_information/irrigationCheck";

//Define the WiFi credentials
// const char* ssid = "CUONG DOLA T3";
// const char* password = "V12397195T3";

const char* ssid = "AVITECH_IDS";
const char* password = "avitechuet";

String timeNow;
unsigned long dataMillisPut = 0;
unsigned long dataMillisGet = 0;

//BGM Sensor Server name 
#define BGMServerName "Introverts"


/* UUID's of the BGM-sensor, characteristic that we want to read*/
static BLEUUID BGMServiceUUID("de8a5aac-a99b-c315-0c80-60d4cbb51224");

// Temperature Characteristic
static BLEUUID temperatureCharacteristicUUID("61a885a4-41c3-60d0-9a53-6d652a70d29c");

// Soil Humidity Characteristic
static BLEUUID soil_humidityCharacteristicUUID("75f2c9d0-396f-4501-a884-4d2d2cb2ad51");

// Air humidity Characteristic
static BLEUUID air_humidityCharacteristicUUID("012c3ac1-6682-4d4d-913c-d193662799bd");

// Radiation Characteristic
static BLEUUID radiationCharacteristicUUID("5b026510-4088-c297-46d8-be6c736a087a");

/* Specify the Characteristic UUID of Server */
static BLEUUID RelayControlCharUUID("cfb6ff78-3dcc-444d-ace1-6c5ffd807401");

//Flags stating if should begin connecting and if the connection is up
static boolean DoConnect = false;
static boolean connected = false;
//Address of the BGM sensor device. Address will be found during scanning...
static BLEAddress *BGMAddress;

//Client init 
static BLEClient* pClient = BLEDevice::createClient();

//Characteristics from sensors that we want to read
static BLERemoteCharacteristic* temperatureCharacteristic;
static BLERemoteCharacteristic* soil_humidityCharacteristic;
static BLERemoteCharacteristic* air_humidityCharacteristic;
static BLERemoteCharacteristic* radiationCharacteristic;

//Irrigation control characteristics
static BLERemoteCharacteristic* RelayCharacteristic;

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

//Flag to keep old command -> compare and detect new command
static boolean oldIrri = false;

// Connect to the BLE Server that has the name, Service, and Characteristics
bool connectToServer(BLEAddress pAddress) {
  // Connect to the remove BLE Server.
  pClient->connect(pAddress);
  Serial.println(" - Connected to server");
  Serial.print("Check service UUID: ");
  Serial.println(BGMServiceUUID.toString().c_str());
 
  // Obtain a reference to the service we are after in the remote BLE server.
  BLERemoteService* pRemoteService = pClient->getService(BGMServiceUUID);
  if (pRemoteService == nullptr) {
    Serial.print("Failed to find our service UUID: ");
    Serial.println(BGMServiceUUID.toString().c_str());
    return false;
  }
  //If the service is BGM relay control service
  //Obtain a reference to the relay characteristic
  
  RelayCharacteristic = pRemoteService->getCharacteristic(RelayControlCharUUID);
  temperatureCharacteristic = pRemoteService->getCharacteristic(temperatureCharacteristicUUID);
  soil_humidityCharacteristic = pRemoteService->getCharacteristic(soil_humidityCharacteristicUUID);
  air_humidityCharacteristic = pRemoteService->getCharacteristic(air_humidityCharacteristicUUID);
  radiationCharacteristic = pRemoteService->getCharacteristic(radiationCharacteristicUUID);
  if (RelayCharacteristic == nullptr || temperatureCharacteristic == nullptr || soil_humidityCharacteristic == nullptr 
      || air_humidityCharacteristic == nullptr || radiationCharacteristic == nullptr )
    {
      Serial.print("Failed to find our characteristics UUID!");
     // pClient->disconnect();
      return false;
    } 
    Serial.println(" - Found our characteristics");

    //Assign callback function for the relay characteristic
   // RelayCharacteristic->registerForNotify(relayNotifyCallback);
    temperatureCharacteristic->registerForNotify(temperatureNotifyCallback);
    soil_humidityCharacteristic->registerForNotify(soil_humidityNotifyCallback);
    air_humidityCharacteristic->registerForNotify(air_humidityNotifyCallback);
    radiationCharacteristic->registerForNotify(radiationNotifyCallback);
  connected = true;
  return true;
}

//Callback function that gets called, when another device's advertisement has been received
//For BGM sensor server
class BGMDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    if (advertisedDevice.getName() == BGMServerName) { //Check if the name of the advertiser matches
      advertisedDevice.getScan()->stop(); //Scan can be stopped, we found what we are looking for
      BGMAddress = new BLEAddress(advertisedDevice.getAddress()); //Address of advertiser is the one we need
      DoConnect = true; //Set indicator, stating that we are ready to connect
      Serial.println("Found device. Connecting!");
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

//Relay control notify properties
static void relayNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic,
                            uint8_t* pData, size_t length, bool isNotify)
{
  Serial.print("Notify callback for characteristic ");
  Serial.print(pBLERemoteCharacteristic->getUUID().toString().c_str());
  Serial.print(" of data length ");
  Serial.println(length);
  Serial.print("data: ");
  Serial.println((char*)pData);
}

void printReadings() {
  Serial.print("Temperature: ");
  Serial.println(*temperatureRaw);
 
  Serial.print("Soil Humidity: ");
  Serial.println(*soil_humidityRaw); 

  Serial.print("Air Humidity: ");
  Serial.println(*air_humidityRaw); 

  Serial.print("Radiation: ");
  Serial.println(*radiationRaw); 
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
  Serial.print(F("\nEpoch Time: "));
  Serial.print(epochTime);

  String formattedTime = timeClient.getFormattedTime();
  Serial.print(F("\nFormatted Time: "));
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

void setDataField (float air_humidity, float radiation, float soil_humidity, float temperature) {
  //Path to measured data from sensors
  String dataPath = "/testUser/field1/measured_data/" + timeNow;
  Firebase.setFloat(firebaseData, dataPath + "/air_humidity", air_humidity);
  //delay(5000);
  Firebase.setFloat(firebaseData, dataPath + "/radiation", radiation);
  //delay(5000);
  Firebase.setFloat(firebaseData, dataPath + "/soil_humidity", soil_humidity);
  //delay(5000);
  Firebase.setFloat(firebaseData, dataPath + "/temperature", temperature);
}

void setup() {
  //Start serial communication
  Serial.begin(115200);
  Serial.println("Starting Arduino BLE Client application...");
  
  BLEDevice::init("ESP32-BLE-Client");

  // Scan for 30 seconds until we have found the relay control device 
  BLEScan* pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new BGMDeviceCallbacks());
  pBLEScan->setInterval(1349);
  pBLEScan->setWindow(499);
  pBLEScan->setActiveScan(true);
  pBLEScan->start(20);
}

void loop() {
  //Retrieve irrigation check from database in order to control relay
  if ((millis() - dataMillisGet > 5000) && DoConnect == true)
  {
    dataMillisGet = millis();
    connectWifi();
    connectFirebase();
    /* If we are connected to a peer BLE Server, update the characteristic each time we are reached
       with the current time since boot */
    bool newIrri;
    Firebase.getBool(firebaseData, irriPath, newIrri);
    uint8_t irr;
    Serial.print("\nString: ");
    Serial.print(newIrri);
    if (newIrri)
    {
      irr = 0x01;
      Serial.println("\nTime to water!");
    }
    else 
    {
      irr = 0x00;
      Serial.println("\nStandby...");
    }
    WiFi.disconnect();
    if (newIrri != oldIrri)
    {
      oldIrri = newIrri;
      if (connectToServer(*BGMAddress))
      {
        Serial.println("We are now connected to the BLE Server.");
      } 
      else
      {
        Serial.println("We have failed to connect to the BLE server; please restart your device.");
      }

      if (connected)
      {
        RelayCharacteristic->writeValue(&irr, 1, true);
      }
    }
    //pClient->disconnect();
  }

  // If the flag "doConnect" is true then we have scanned for and found the desired
  // BLE Server with which we wish to connect.  Now we connect to it.  Once we are
  // connected we set the connected flag to be true.
  if (DoConnect == true && (millis() - dataMillisPut > 30000)) 
  { 
    //delay(5000);
    if (connectToServer(*BGMAddress)) { 
      // delay 5s for waiting bgm220 get new data
      //delay(5000);
      
      temperatureCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
      delay(50);
      soil_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn,2 , true);
      delay(50);
      air_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
      delay(50);
      radiationCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);

      // temperatureCharacteristic->registerForNotify(soil_humidityNotifyCallback);
      // air_humidityCharacteristic->registerForNotify(temperatureNotifyCallback);
      // soil_humidityCharacteristic->registerForNotify(air_humidityNotifyCallback);
      // radiationCharacteristic->registerForNotify(radiationNotifyCallback);

      Serial.println("We are now connected to the BLE Server.");
      //Activate the Notify property of each Characteristic
    } 
    else {
      Serial.println("\nFailed to connect to the sensor server. Restart your device.");
    }
  
    //if new characteristic readings are available, print 
    if (newTemperature && newSoilHumidity && newAirHumidity && newRadiation)
    {
      newTemperature = false;
      newSoilHumidity = false;
      newAirHumidity = false;
      newRadiation = false;
      printReadings();
    }
    float radiation = (float)*radiationRaw * 0.0185 / 0.42;
    float soil_humid = (((float)*soil_humidityRaw*25.19-70390)/(1-0.018*(float)*soil_humidityRaw) - 550)/137.5;
    float air_humid = (float)*air_humidityRaw * 125 / 65536 - 6;
    float temperature = (float)(*temperatureRaw) * 155.72 / 65536 - 46.85; 
    
    Serial.println("\nData that prepared to upload: ");
    Serial.print("Temperature: ");
    Serial.print(temperature);
  
    Serial.print("\nSoil Humidity: ");
    Serial.print(soil_humid); 

    Serial.print("\nAir Humidity: ");
    Serial.print(air_humid); 

    Serial.print("\nRadiation: ");
    Serial.print(radiation); 
    
    pClient->disconnect();

    connectWifi();
    connectFirebase();
    //set the current time  
    dataMillisPut = millis();
    setTimeDay();
    setDataField(air_humid, radiation, soil_humid, temperature);   
    Serial.println("Set data done!");
    WiFi.disconnect();
    delay(5000);
  }
  pClient->disconnect();
  WiFi.disconnect();
}