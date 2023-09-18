/**
 * @file ble_utilis.h
 * @brief Bluetooth Low Energy header file, provides the method to update gattdb characteristic
 * and send it to client devices via BLE notification.
 *
 * @author Duy Hung Nguyen
 * @date July 03 2023
 */

#ifndef BLE_UTILIS_H_
#define BLE_UTILIS_H_


/**
 * @brief Updates the sensor data characteristic.
 *
 * Using sensor's measurement method to get the information, then update GATT database
 * characteristic attribute stored in local.
 *
 * @return SL_STATUS_OK when successful, error code when not.
*/
sl_status_t update_sensor_data(void);

/**
 * @brief Send sensor data to BLE client
 * 
 * Read GATT database characteristic attributes was stored. Then notify client which
 * enable receive the notification via BLE connection.
 * 
 * @param evt Bluetooth event handle parameter
 *
 * @return SL_STATUS_OK when successful, error code when not.
*/
sl_status_t send_sensor_data_notification(sl_bt_msg_t *evt);

#endif /* BLE_UTILIS_H_ */
