/*
 * ble_utilis.h
 *
 *  Created on: Jun 30, 2023
 *      Author: Duy Hung Nguyen
 */

#ifndef BLE_UTILIS_H_
#define BLE_UTILIS_H_

// Updates the sensor data characteristic.
sl_status_t update_light_ambient_data(void);

sl_status_t update_soil_humidity_data(void);

sl_status_t update_temperature_data(void);

sl_status_t update_relative_humidity_data(void);

sl_status_t send_sensor_data_notification(void);

#endif /* BLE_UTILIS_H_ */
