/*
 * si7021.h
 *
 *  Created on: Jun 25, 2023
 *      Author: Duy Hung Nguyen
 *
 *  Temperature and Relative humidity sensor module Si7021 header file, provide measurement method
 *  peripheral address and command macros.
 */

#ifndef SI7021_H_
#define SI7021_H_

#include "stdint.h"

#define SI7021_ADDRESS 0x40

#define SI7021_MEASURE_TEMP_HM 0xE5
#define SI7021_MEASURE_TEMP_NHM 0xF5
#define SI7021_MEASURE_HUMD_HM 0xE3
#define SI7021_MEASURE_HUMD_NHM 0xF3
#define SI7021_READ_PREVIOUS_TEMP_MEASURE 0xE0
#define SI7021_RESET 0xFE

/*
 * Perform I2C communication to get relative humidity value
 *
 * @return 16 bit encode the result, always return xxxxxx10 in the LSB field
 * */
uint16_t measure_relative_humidity(void);

/*
 * Perform i2C communication to get temperature value
 *
 * @return 16 bit encode the temperature in Celcius, always return xxxxxx00 in the LSB field
 *
 * */
uint16_t measure_temperature(void);

#endif /* SI7021_H_ */
