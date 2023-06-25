/*
 * bh1750.h
 *
 *  Created on: Jun 25, 2023
 *      Author: Duy Hung Nguyen
 */

#ifndef BH1750_H_
#define BH1750_H_

#include "stdint.h"

#define BH1750_ADDRESS 0x23<<1

#define COMMAND_POWER_ON 0x01
#define COMMAND_POWER_OFF 0x00
#define COMMAND_RESET 0x07
#define COMMAND_CONT_HRESO 0x10
#define COMMAND_CONT_HRESO2 0x11
#define COMMAND_CONT_LRESO 0x13
#define COMMAND_ONCE_HRESO 0x20
#define COMMAND_ONCE_HRESO2 0x21
#define COMMAND_ONCE_LRESO2 0x23

uint16_t measure_high_resolution2_once();

#endif /* BH1750_H_ */
