/*
 * pcf8591.h
 *
 *  Created on: Jun 25, 2023
 *      Author: Duy Hung Nguyen
 *
 *  Header file for ADC PCF8591 to perform I2C communication with BGM220. Provide peripheral address and command marcos.
 */

#ifndef PCF8591_H_
#define PCF8591_H_

#define PCF8591_ADDRESS 0x48
#define CONTROL_BYTE_CH0 0x40

/*
 * Perform I2C communication to read the voltage value from channel 0 of this ADC
 *
 * @return A bytes encoding the value, must be multiply with V_ref to get the voltage value
 */
uint8_t read_soil_humidity_sensor_voltage_value(void);

#endif /* PCF8591_H_ */
