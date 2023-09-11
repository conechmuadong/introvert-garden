#include "BLEDevice.h"
#include <stdlib.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include "TimeLib.h"
#include <NTPClient.h>
#include "WiFiUdp.h"

//Wifi - Firebase
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

//Define the API key and RTDB URL
#define FIREBASE_HOST "https://introvert-s-garden-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "AIzaSyBdmudP2G2MzhOWmJTqmjkFDgaUhKQ326U"

//Define the Service Account credentials
#define FIREBASE_PROJECT_ID "introvert-s-garden"
#define FIREBASE_CLIENT_EMAIL "firebase-adminsdk-ko72h@introvert-s-garden.iam.gserviceaccount.com"
const char PRIVATE_KEY[] PROGMEM = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCu0adzcaBwShna\nQkiGtK/Fkh8qJVEj9OBxgStks/Tx3+JC32MOvOq3vlExf1O7httn4OVegxxa6dPe\nabt+d1Ave3XWtUfFiaMTE3F3OQgYIw+tvcZrQ19lm4uFPlTtcY6WFK2JHH2v8uUD\nTdLSo7pNEuX8enx8ASKSqN7WChBi2DyItqb6mOrSVNpv6Elk4+3u5mo47P7VEDVw\nimmOJKs9r09dPtQDGBUxoiGLfX+Oz2hjvqK89aBisNbcJhWWKMgv0UMyRIKk7m4C\n58GHtqMLYPnJZVmmBlI/OPMymz3eAS/56KjdAxpL+qkX9ZaC18hS3qGObAnU1tE4\nyXtIk9AVAgMBAAECggEAJDyzpeet/tCxYq9eKsQN8ll7achR9S7Xjlec19NAqRmE\nJGH7Pc0Vp1BFMsomoGbQGIyC5xAs4sB46S/IDGstfSYtkzvA/Jau7a+YU8MH5T7x\nS02c8VOGFQEEzvIQdyaUjJINNbBo8j2aUJDDbGPBZKQP9cGrB6ZRlQ55oGr2IhLC\nLeJTRo9UxpaAZlcllOUOCagZ4ozpeZJzMLI8ceqRjlrEX+XXM54CBQrSfiTLc/Wh\nHUa+Lfryw8Hn6V6QCtXEjxQErJn17dhguwZ/hSxMPsRrXNLCkhzZi40JeYiv5tKY\nfUfujBB2pBELUM0hPWDPl7veMY6X5ULn6dHW9hwOwQKBgQDjQMpJ6lV+ZUh51DVD\nPTuuzO8UTKbCSwFlmUQQjLswTVjJrb/8zkNpoGSVNIJT50ox7a360wx4vDoEOoMb\nnOeDjUCD/F47J58unMY58LA9qyYZDfWVEwxwcaAQhMEboG5sQMJ376Dc3Lipd7Px\nNfeReoOf5jEq4c6P3ZU4hKp5QQKBgQDE7t9tqcKy4gpKYkdce0S13Aa7HuF83HK4\nqMNKMgkkYwGgnbwrMW9MJrNNmNyCKSRNfIbZt0g8V69/5yGqgynr7AZw8khHxDKI\nG7HpcAeGBLGT8yJnMk8n5wRhL8raGSfydwxeJSZM+LFxgKLYfxKFRpj1rGE655QU\nUpxlU8yt1QKBgAGq+xEZ0DDSS3rhbNamf2akCGksutiYNlVpj49TiRgsqScpyEvx\ns5r122cg8IfvHaNKLEm3/NJyBzb6uvWb7DMR+VQ+qO5kNw3fhrDsLE73j5sFeDT0\n49V7cAJPBx/NyKGww/yfXJ3iXNSZlU+C5uNBc+/bf6bh62Mw7CRzDNFBAoGAUA9Y\nb8mq6HQygMjxf+xxI4m+hsXoH42zadTaB8jNpKhHWhemDt07BPdav3occyHsqz+r\nxsjXKgjvxQyeyCYoncvHVpV4wwxFCsZPjrpHpLk1xBL88n0mZu9o/WWjjwTZE55I\nPeHWb0ZPRPhpu5/1VI890epxYLQSpCxtflgSBr0CgYEAypV/bdphF/IN+XXCFu+f\nK2/u3IXTRCJXrwda9Akxt7USvQZQTUtFJRoC4BGAoUEe/1DR9O53NSHqY9CUI6tZ\nkT2NL5aOGSWpF1MO9/2sozY4z3aEqfwZfZCN4HuPJUc69L+SBmsLNw11tPRFcuI5\nM+0utf2S9lAz659GhgKxhiM=\n-----END PRIVATE KEY-----\n";

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Define Firebase Data object
FirebaseData firebaseData;
//Define FirebaseAuth data for authentication data
FirebaseAuth auth;
//Define FirebaseConfig data for config data
FirebaseConfig config;

//Path to irriState boolean
String irriPath = "/users/field1/irrigation_information/";

//Define the WiFi credentials
const char* ssid = "320b";
const char* password = "12345sau";

String timeNow;
//Current epoch time (ms) for uploading data to database
unsigned long BGMSensorDataMillis = 0;
//Current epoch time (ms) for retrieving data from database
unsigned long BGMRelayControlDataMillis = 0;

//BGM Server name 
#define BGMServerName "Introverts"

//Specify UUID's of the BGM-sensor, characteristic that we want to read as follows:
static BLEUUID BGMServiceUUID("de8a5aac-a99b-c315-0c80-60d4cbb51224");
//Temperature Characteristic
static BLEUUID temperatureCharacteristicUUID("1d8cff17-ff7c-47f6-b1e7-01b374a687cb");
//Soil Humidity Characteristic
static BLEUUID soil_humidityCharacteristicUUID("6b6ef948-3e83-4c70-a473-76ad06d42e54");
//Air humidity Characteristic
static BLEUUID air_humidityCharacteristicUUID("435a7c88-9f8b-4487-878b-bee16913b435");
//Radiation Characteristic
static BLEUUID radiationCharacteristicUUID("63a269a4-74f2-45d9-b538-ba5c8c5cdc41");

//Specify the Characteristic UUID of Server 
static BLEUUID RelayControlCharacristicUUID("5b026510-4088-c297-46d8-be6c736a087a");

//Flag stating if the connection is up
static boolean connected = false;

//Address of the BGM device. Address will be found during scanning...
static BLEAddress *BGMAddress;

//Client init 
static BLEClient* pClient = BLEDevice::createClient();

//Characteristics from sensors that we want to read
static BLERemoteCharacteristic* temperatureCharacteristic;
static BLERemoteCharacteristic* soil_humidityCharacteristic;
static BLERemoteCharacteristic* air_humidityCharacteristic;
static BLERemoteCharacteristic* radiationCharacteristic;

//Relay control characteristics that we want to write
static BLERemoteCharacteristic* relayControlCharacteristic;

//Activate notify
const uint8_t notificationOn[] = {0x1, 0x0};
const uint8_t notificationOff[] = {0x0, 0x0};

//Variables to store raw data from sensor
uint16_t temperatureRaw;
uint32_t soil_humidityRaw;
uint16_t air_humidityRaw;
uint16_t radiationRaw;

//Flags to check whether new data readings are available
boolean newTemperature = false;
boolean newSoilHumidity = false;
boolean newAirHumidity = false;
boolean newRadiation = false;

//Flag to keep old relay control command -> compare and detect new one
static boolean haveNewIrriState = false;

//Connect to the BLE Server that has the name, Service, and Characteristics
bool connectToRelayServer(BLEAddress pAddress) {
  //Connect to the remote BLE Server
  pClient->connect(pAddress);
  Serial.println(" - Connected to server");
  Serial.print("Check service UUID: ");
  Serial.println(BGMServiceUUID.toString().c_str());
 
  //Obtain a reference to the service we are after in the remote BLE server
  BLERemoteService* pRemoteService = pClient->getService(BGMServiceUUID);
  if (pRemoteService == nullptr) {
    Serial.print("Failed to find our service UUID: ");
    Serial.println(BGMServiceUUID.toString().c_str());
    return false;
  }
  //Obtain a reference to the relay characteristic
  relayControlCharacteristic = pRemoteService->getCharacteristic(RelayControlCharacristicUUID);
  if (relayControlCharacteristic == nullptr) {
    Serial.print("Failed to find our relay characteristic UUID: ");
    Serial.println(RelayControlCharacristicUUID.toString().c_str());
    return false;
  }
  Serial.println(" - Found our relay characteristic");
  connected = true;
  return true;
}

bool connectToSensorServer(BLEAddress pAddress) {
  //Connect to the remote BLE Server
  pClient->connect(pAddress);
  Serial.println(" - Connected to server");
 
  //Obtain a reference to the service we are after in the remote BLE server
  BLERemoteService* pRemoteService = pClient->getService(BGMServiceUUID);
  if (pRemoteService == nullptr) {
    Serial.print("Failed to find our service UUID: ");
    Serial.println(BGMServiceUUID.toString().c_str());
    return false;
  }
  //Obtain a reference to the sensor characteristics 
  temperatureCharacteristic = pRemoteService->getCharacteristic(temperatureCharacteristicUUID);
  soil_humidityCharacteristic = pRemoteService->getCharacteristic(soil_humidityCharacteristicUUID);
  air_humidityCharacteristic = pRemoteService->getCharacteristic(air_humidityCharacteristicUUID);
  radiationCharacteristic = pRemoteService->getCharacteristic(radiationCharacteristicUUID);
  if (temperatureCharacteristic == nullptr || soil_humidityCharacteristic == nullptr 
      || air_humidityCharacteristic == nullptr || radiationCharacteristic == nullptr 
      ) 
  {
    Serial.print("Failed to find our sensor characteristic UUID");
    return false;
  }
  Serial.println(" - Found our sensor characteristics");

  //Assign callback functions for the sensor characteristic
  Serial.println("Getting air humidity info...");
  delay(50);
  air_humidityCharacteristic->registerForNotify(air_humidityNotifyCallback);
  Serial.println("Getting radiation info...");
  delay(50);
  radiationCharacteristic->registerForNotify(radiationNotifyCallback);
  Serial.println("Getting Temperature Info..");
  delay(50);
  temperatureCharacteristic->registerForNotify(temperatureNotifyCallback);
  Serial.println("Getting soil humidity info..");
  delay(50);
  soil_humidityCharacteristic->registerForNotify(soil_humidityNotifyCallback);
  return true;
}

//Callback function that gets called, when another device's advertisement has been received
class BGMDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    if (advertisedDevice.getName() == BGMServerName ) { //Check if the name of the advertiser matches
      advertisedDevice.getScan()->stop(); //Scan can be stopped, we found what we are looking for
      BGMAddress = new BLEAddress(advertisedDevice.getAddress()); //Address of advertiser is the one we need
      Serial.println("Found BGM device. Connecting!");
    }
  }
};
 
//When the BLE Server sends a new temperature reading with the notify property
static void temperatureNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                        uint8_t* pData, size_t length, bool isNotify) {
  //store temperature value
  temperatureRaw = (uint16_t)*pData|(uint16_t)*(pData+1)<<8;
  newTemperature = true;
}

//When the BLE Server sends a new soil_humidity reading with the notify property
static void soil_humidityNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                    uint8_t* pData, size_t length, bool isNotify) {
  //store humidity value
  soil_humidityRaw = (uint32_t)*pData|(uint32_t)*(pData+1)<<8|(uint32_t)*(pData+2)<<16|(uint32_t)*(pData+3)<<24;
  newSoilHumidity = true;
}

//When the BLE Server sends a new air_humidity reading with the notify property
static void air_humidityNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                    uint8_t* pData, size_t length, bool isNotify) {
  //store air humidity value
  air_humidityRaw = (uint16_t)*pData|(uint16_t)*(pData+1)<<8;
  newAirHumidity = true;
}

//When the BLE Server sends a new radiation reading with the notify property
static void radiationNotifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, 
                                    uint8_t* pData, size_t length, bool isNotify) {
  //store radiation value
  radiationRaw = (uint16_t)*pData|(uint16_t)*(pData+1)<<8;
  newRadiation = true;
}

//Print out our reading results
void printReadings() {
  Serial.print("Temperature: ");
  Serial.println(temperatureRaw);-
 
  Serial.print("Soil Humidity: ");
  Serial.println(soil_humidityRaw); 

  Serial.print("Air Humidity: ");
  Serial.println(air_humidityRaw); 

  Serial.print("Radiation: ");
  Serial.println(radiationRaw); 
}

//Connect to WiFi with given SSID and password
void connectWifi() {
  WiFi.begin(ssid, password);
  int startTime = millis();
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
    if(millis()-startTime > 60000)
      ESP.restart();
  }
  Serial.println("");
  Serial.println(F("Wifi connected"));
}

//Connect to Firebase with given RTDB URL and API Key
void connectFirebase() {
  /* Assign the api key (required) */
  config.api_key = FIREBASE_AUTH;
  /* Assign the RTDB URL */
  config.database_url = FIREBASE_HOST;

  /* Assign the sevice account credentials and private key (required) */
  config.service_account.data.client_email = FIREBASE_CLIENT_EMAIL;
  config.service_account.data.project_id = FIREBASE_PROJECT_ID;
  config.service_account.data.private_key = PRIVATE_KEY;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h
  Firebase.begin(&config, &auth);  
  Firebase.reconnectWiFi(true);
}

//Getting current time for uploading data
void setTimeDay() {
  timeClient.begin();
  //Set offset time in seconds to adjust for your timezone, for example:
  timeClient.setTimeOffset(25200); //GMT +7
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

//Upload the read data to Firebase
void setDataField (float air_humidity, float radiation, float soil_humidity, float temperature) {
  //Path to measured data from sensors
  String dataPath = "/users/field1/measured_data/" + timeNow;

  Firebase.setFloat(firebaseData, dataPath + "/air_humidity", air_humidity);
  Firebase.setFloat(firebaseData, dataPath + "/radiation", radiation);
  Firebase.setFloat(firebaseData, dataPath + "/soil_humidity", soil_humidity);
  Firebase.setFloat(firebaseData, dataPath + "/temperature", temperature);
}

void bleScan() {
  //Init BLE device
  BLEDevice::init("ESP32-BLE-Client");
  
  //Scan for 10 seconds until we have found the relay control device 
  BLEScan* pBLEScan = BLEDevice::getScan();
  Serial.println("Scan for BLE Device...");
  pBLEScan->setAdvertisedDeviceCallbacks(new BGMDeviceCallbacks());
  pBLEScan->start(10);
}

//Check if epochTime is within the startTime and endTime of irrigation process. 
//Return true if it is, otherwise return false.
bool inIrrigationTime() {
    Firebase.getString(firebaseData, irriPath + "startTime");
    String startTimestr = firebaseData.stringData();
    int hh, mm, ss;
    int YY, MM, DD;
    struct tm when = {0};

    sscanf(startTimestr.c_str(), "%d-%d-%d %d:%d:%d",&YY, &MM, &DD, &hh, &mm, &ss);

    when.tm_year = YY - 1900;
    when.tm_mon = MM - 1;
    when.tm_mday = DD;
    when.tm_hour = hh;
    when.tm_min = mm;
    when.tm_sec = ss;

    time_t startTime;
    startTime = mktime(&when);
    Firebase.getString(firebaseData, irriPath + "endTime");
    String endTimestr = firebaseData.stringData();
    Serial.println(endTimestr);
    Serial.println(startTimestr);
    sscanf(endTimestr.c_str(), "%d-%d-%d %d:%d:%d",&YY, &MM, &DD, &hh, &mm, &ss);

    when.tm_year = YY - 1900;
    when.tm_mon = MM - 1;
    when.tm_mday = DD;
    when.tm_hour = hh;
    when.tm_min = mm;
    when.tm_sec = ss;

    time_t endTime;
    endTime = mktime(&when);
    Serial.println(endTime);
    Serial.println(startTime);
    timeClient.begin();
    //Set offset time in seconds to adjust for your timezone, for example:
    timeClient.setTimeOffset(25200); //GMT +7
    timeClient.update();
    time_t epochTime = timeClient.getEpochTime();
    Serial.println(epochTime);
    if (epochTime < endTime && epochTime > startTime){
      return true;
    }
    return false;
}

void setup() {
  //Start serial communication
  Serial.begin(115200);
  Serial.println("Starting Arduino BLE Client application...");
}

void loop() {
  //Compare the result of the function inIrrigationTime with the boolean irriState from Firebase 
  //to obtain the relay command
  if (millis() - BGMRelayControlDataMillis > 5000)
  {
    BGMRelayControlDataMillis = millis();
    connectWifi();
    connectFirebase();
    uint8_t irr;
    while(!Firebase.ready());
    Firebase.getBool(firebaseData, irriPath + "irriState");
    bool oldIrri = firebaseData.boolData();
    bool newIrri = inIrrigationTime();
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

    if (newIrri != oldIrri) {
      haveNewIrriState = true;
      Firebase.setBool(firebaseData, irriPath + "irriState", newIrri);
    }
    Serial.println("Disconnecting Wifi...");
    WiFi.disconnect(true, false);

    //If we either detect a new relay control command or it's time to upload data to Firebase
    //Start scanning to find the desired BLE Server with which we wish to connect
    if (haveNewIrriState || millis() - BGMSensorDataMillis > 150000)
    {
      bleScan();
      if (haveNewIrriState) 
      {
        haveNewIrriState = false;
        //If we are connected to a peer BLE Server, update the relay control characteristic
        if (connectToRelayServer(*BGMAddress))
        {
          Serial.println("We are now connected to the BLE Server.");
          Serial.println("Writing irrigation info...");
          relayControlCharacteristic->writeValue(irr,true);
          Serial.println("Set info done!");
        } 
        else
        {
          Serial.println("We have failed to connect to the server.");
        }

        pClient->disconnect();
        ESP.restart();
      }

      if (millis() - BGMSensorDataMillis > 150000) 
      {
        if (connectToSensorServer(*BGMAddress)) { 
          Serial.println("We are now connected to the BLE Server. Enabling notifications");
          temperatureCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
          delay(50);
          soil_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn,2 , true);
          delay(50);
          air_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
          delay(50);
          radiationCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOn, 2, true);
          
          Serial.println("Getting notifications done, now unenabling notifications");
          //Activate the Notify property of each Characteristic
          temperatureCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOff, 2, true);
          delay(50);
          soil_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOff,2 , true);
          delay(50);
          air_humidityCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOff, 2, true);
          delay(50);
          radiationCharacteristic->getDescriptor(BLEUUID((uint16_t)0x2902))->writeValue((uint8_t*)notificationOff, 2, true);
        } 
        else {
          Serial.println("\nFailed to connect to the sensor server.");
          ESP.restart();
        }
      pClient->disconnect();
      }

      BLEDevice::deinit(true);
      Serial.println("Disable BLE");
      delay(5000);
    }
    Serial.println(ESP.getFreeHeap());
  }
  //If 4 new characteristic readings are available, print 
  if (newTemperature && newSoilHumidity && newAirHumidity && newRadiation)
  {
    newTemperature = false;
    newSoilHumidity = false;
    newAirHumidity = false;
    newRadiation = false;
    printReadings();

    //Preprocessing data to later upload to Firebase
    float radiation = (float)radiationRaw * 0.0185 / 0.42;
    float soil_humid = 200;
    if (soil_humidityRaw != 0){
      soil_humid = (((float)(1000000/(soil_humidityRaw))*25.19-81390)/(1-0.025*(float)(1000000/(soil_humidityRaw))) - 550)/137.5;
    }
    float air_humid = ((float)air_humidityRaw *125/65536) - 6;
    float temperature = (float)(temperatureRaw) * 175.72 / 65536 - 46.85; 
  
    Serial.println("\nData that prepared to upload: ");
    Serial.print("Temperature: ");
    Serial.print(temperature);
  
    Serial.print("\nSoil Humidity: ");
    Serial.print(soil_humid); 

    Serial.print("\nAir Humidity: ");
    Serial.print(air_humid); 

    Serial.print("\nRadiation: ");
    Serial.print(radiation); 

    delay(1000);
    connectWifi();
    delay(1000);
    connectFirebase();
    //Set the current time and upload data to Firebase
    BGMRelayControlDataMillis = millis();
    while(!Firebase.ready());
    setTimeDay();
    setDataField(air_humid, radiation, soil_humid, temperature);   
    Serial.println("Set data done!");
    WiFi.disconnect(true, false);
    ESP.restart();
  }
}