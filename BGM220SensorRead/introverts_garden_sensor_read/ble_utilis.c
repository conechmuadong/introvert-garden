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

/***************************************************************************//**
 * Updates the Report Button characteristic.
 *
 * Checks the current button state and then writes it into the local GATT table.
 ******************************************************************************/
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
sl_status_t send_sensor_data_notification(void)
{
  sl_status_t sc;
  uint8_t data_send;
  size_t data_len;

  // Read report button characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_light_ambient,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              &data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_light_ambient,
                                    sizeof(data_send),
                                    &data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append(" Notification sent: 0x%02x\n", (int)data_send);
  }
  return sc;
}

