/**
 * @file ble_utilis.c
 * @brief BLE utilities source file
 * 
 * @author Duy Hung Nguyen
 * @date June 25th 2023
*/

#include "irrometter200SS.h"
#include "em_common.h"
#include "app_assert.h"
#include "app_log.h"
#include "sl_bluetooth.h"
#include "gatt_db.h"
#include "ble_utilis.h"
#include "bh1750.h"
#include "si7021.h"
#include "em_timer.h"
#include "sl_sleeptimer.h"

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
    app_log_info("Light ambient attribute written: 0x%04x\n", (int)data_send);
  }

  return sc;
}

sl_status_t update_soil_humidity_data(void)
{
  sl_status_t sc;
  TIMER_Enable(TIMER1, true);
  sl_sleeptimer_delay_millisecond(100);
  TIMER_Enable(TIMER1, false);
  uint32_t data_send = calculatePeriod();
  // Write attribute in the local GATT database.
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_soil_humidity,
                                               0,
                                               sizeof(data_send),
                                               (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_info("Soil Humidity attribute written: 0x%08x\n", (int)data_send);
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
    app_log_info("Relative Humidity attribute written: 0x%04x\n", (int)data_send);
  }

  return sc;
}

sl_status_t update_temperature_data(void)
{
  sl_status_t sc;
  uint16_t data_send = measure_temperature();
  // Write attribute in the local GATT database.
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_temperature,
                                               0,
                                               sizeof(data_send),
                                               (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_info("Temperature attribute written: 0x%04x \n", (int)data_send);
  }

  return sc;
}

sl_status_t send_light_ambient_notification(void)
{
  sl_status_t sc;
  uint16_t data_send = (uint16_t*)calloc(1,sizeof(uint16_t));
  size_t data_len;

  // Read light ambient characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_light_ambient,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)&data_send);
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
  uint16_t data_send;
  size_t data_len;

  // Read relative humidity characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_relative_humidity,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)&data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_relative_humidity,
                                    sizeof(data_send),
                                    (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Relative humidity data sent: 0x%04x\n", (int)data_send);
  }
  return sc;
}

sl_status_t send_temperature_notification(void)
{
  sl_status_t sc;
  uint16_t data_send;
  size_t data_len;

  // Read tempurature characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_temperature,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)&data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_temperature,
                                    sizeof(data_send),
                                    (uint8_t*)&data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Temperature data sent: 0x%04x\n", (int)data_send);
  }
  return sc;
}

sl_status_t send_soil_humidity_notification(void)
{
  sl_status_t sc;
  uint32_t data_send;
  size_t data_len;

  // Read soil humidity characteristic stored in local GATT database.
  sc = sl_bt_gatt_server_read_attribute_value(gattdb_soil_humidity,
                                              0,
                                              sizeof(data_send),
                                              &data_len,
                                              (uint8_t*)&data_send);
  if (sc != SL_STATUS_OK) {
    return sc;
  }

  // Send characteristic notification.
  sc = sl_bt_gatt_server_notify_all(gattdb_soil_humidity,
                                    sizeof(data_send),
                                    &data_send);
  if (sc == SL_STATUS_OK) {
    app_log_append("Soil humidity data sent: %d\n", (int)data_send);
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

sl_status_t send_sensor_data_notification(sl_bt_msg_t *evt){
  sl_status_t sc = SL_STATUS_FAIL;
  switch(evt->data.evt_gatt_server_characteristic_status.characteristic){
    case gattdb_light_ambient:
      if (evt->data.evt_gatt_server_characteristic_status.client_config_flags
        & sl_bt_gatt_notification)
      {
        // The client just enabled the notification. Send notification of the
        // current light ambient data stored in the local GATT table.
        app_log_info("Light ambient notification enabled. ");

        sc = send_light_ambient_notification();
        app_log_status_error(sc);
      }
      else
      {
        app_log_info("Light ambient notification disabled.\n");
      }
      break;
    case gattdb_temperature:
      if (evt->data.evt_gatt_server_characteristic_status.client_config_flags
        & sl_bt_gatt_notification)
      {
        // The client just enabled the notification. Send notification of the
        // current temperature data stored in the local GATT table.
        app_log_info("Temperature notification enabled. ");

        sc = send_temperature_notification();
        app_log_status_error(sc);
      }
      else
      {
        app_log_info("Temperature notification disabled.\n");
      }
      break;
    case gattdb_soil_humidity:
      if (evt->data.evt_gatt_server_characteristic_status.client_config_flags
        & sl_bt_gatt_notification)
      {
        // The client just enabled the notification. Send notification of the
        // current soil humidity data stored in the local GATT table.
        app_log_info("Soil humidity notification enabled. ");

        sc = send_soil_humidity_notification();
        app_log_status_error(sc);
      }
      else
      {
        app_log_info("Soil humidity notification disabled.\n");
      }
      break;
    case gattdb_relative_humidity:
      if (evt->data.evt_gatt_server_characteristic_status.client_config_flags
        & sl_bt_gatt_notification)
      {
        // The client just enabled the notification. Send notification of the
        // current relative humidity data stored in the local GATT table.
        app_log_info("Relative humidity notification enabled. ");

        sc = send_relative_humidity_notification();
        app_log_status_error(sc);
      }
      else
      {
        app_log_info("Relative humidity notification disabled.\n");
      }
      break;
  }
  return sc;
}
