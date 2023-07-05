/*
 * ble_utilis.c
 *
 *  Created on: Jun 30, 2023
 *      Author: Duy Hung Nguyen
 */

#include "em_common.h"
#include "app_assert.h"
#include "app_log.h"
#include "sl_bluetooth.h"
#include "gatt_db.h"
#include "ble_utilis.h"
#include "bh1750.h"
#include "pcf8591.h"
#include "si7021.h"

sl_status_t update_light_ambient_data(void)
{
  sl_status_t sc;
  uint16_t data_send = measure_high_resolution2_once();
  // Write attribute in the local GATT database.
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_light_ambient,
                                               0,
                                               sizeof(data_send),
                                               (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_info("Light ambient attribute written: 0x%02x", (int)data_send);
  }

  return sc;
}

sl_status_t update_soil_humidity_data(void)
{
  sl_status_t sc;
  uint8_t data_send = read_soil_humidity_sensor_voltage_value();
  // Write attribute in the local GATT database.
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_soil_humidity,
                                               0,
                                               sizeof(data_send),
                                               (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_info("Soil Humidity attribute written: 0x%02x", (int)data_send);
  }

  return sc;
}

sl_status_t update_relative_humidity_data(void)
{
  sl_status_t sc;
  uint16_t data_send = measure_relative_humidity();
  // Write attribute in the local GATT database.
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_relative_humidity,
                                               0,
                                               sizeof(data_send),
                                               (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_info("Soil Humidity attribute written: 0x%02x", (int)data_send);
  }

  return sc;
}

sl_status_t update_temperature_data(void)
{
  sl_status_t sc;
  uint16_t data_send = measure_relative_humidity();
  // Write attribute in the local GATT database.
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_temperature,
                                               0,
                                               sizeof(data_send),
                                               (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_info("Soil Humidity attribute written: 0x%02x", (int)data_send);
  }

  return sc;
}

/***************************************************************************//**
 * Sends notification of the light ambient sensor characteristic.
 *
 * Reads the current button state from the local GATT database and sends it as a
 * notification.
 ******************************************************************************/
sl_status_t send_light_ambient_notification(void)
{
  sl_status_t sc;
  uint16_t *data_send;
  size_t data_len;

  // Read report button characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_light_ambient,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_light_ambient,
                                    sizeof(data_send),
                                    (uint8_t*)data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Light ambient data sent: 0x%04x\n", (int)data_send);
  }
  return sc;
}

sl_status_t send_relative_humidity_notification(void)
{
  sl_status_t sc;
  uint16_t *data_send;
  size_t data_len;

  // Read report button characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_relative_humidity,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_relative_humidity,
                                    sizeof(data_send),
                                    (uint8_t*)data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Relative humidity data sent: 0x%04x\n", (int)data_send);
  }
  return sc;
}

sl_status_t send_temperature_notification(void)
{
  sl_status_t sc;
  uint16_t *data_send;
  size_t data_len;

  // Read report button characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_temperature,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_temperature,
                                    sizeof(data_send),
                                    (uint8_t*)data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Temperature data sent: 0x%04x\n", (int)data_send);
  }
  return sc;
}

sl_status_t send_soil_humidity_notification(void)
{
  sl_status_t sc;
  uint8_t data_send;
  size_t data_len;

  // Read report button characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_soil_humidity,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              &data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_soil_humidity,
                                    sizeof(data_send),
                                    &data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Temperature data sent: 0x%04x\n", (int)data_send);
  }
  return sc;
}

sl_status_t update_sensor_data(void){
  sl_status_t sc = SL_STATUS_OK;
  bool status = true;
  sc = update_light_ambient_data();
  if (sc != SL_STATUS_OK){
     app_log("An Error occurs while update light ambient data\n");
     app_log_append(sc);
     status = false;
  }
  sl_udelay_wait(10000000);
  sc = update_relative_humidity_data();
  if (sc != SL_STATUS_OK){
     app_log("An Error occurs while update relative humidity data\n");
     app_log_append(sc);
     status = false;
  }
  sl_udelay_wait(10000000);
  sc = update_soil_humidity_data();
  if (sc != SL_STATUS_OK){
     app_log("An Error occurs while update soil humidity data\n");
     app_log_append(sc);
     status = false;
  }
  sl_udelay_wait(10000000);
  sc = update_temperature_data();
  if (sc != SL_STATUS_OK){
       app_log("An Error occurs while update temperature data\n");
       app_log_append(sc);
       status = false;
  }
  if (!status)
     return SL_STATUS_FAIL;
  return sc;
}

sl_status_t send_sensor_data_notification(void){
  sl_status_t sc = SL_STATUS_OK;
  bool status = true;
  sc = send_light_ambient_notification();
  if (sc != SL_STATUS_OK){
     app_log("An Error occurs while send light ambient notification\n");
     app_log_append(sc);
     status = false;
  }
  sl_udelay_wait(10000000);
  sc = send_relative_humidity_notification();
  if (sc != SL_STATUS_OK){
     app_log("An Error occurs while send relative humidity notification\n");
     app_log_append(sc);
     status = false;
  }
  sl_udelay_wait(10000000);
  sc = send_soil_humidity_notification();
  if (sc != SL_STATUS_OK){
     app_log("An Error occurs while send soil humidity notification\n");
     app_log_append(sc);
     status = false;
  }
  sl_udelay_wait(10000000);
  sc = send_temperature_notification();
  if (sc != SL_STATUS_OK){
       app_log("An Error occurs while send temperature notification\n");
       app_log_append(sc);
       status = false;
  }
  if (!status)
     return SL_STATUS_FAIL;
  return sc;
}
