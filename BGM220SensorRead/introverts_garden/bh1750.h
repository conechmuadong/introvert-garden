/**
 * @file bh1750.h
 * @brief BH1750 light ambient sensor header file, provides peripheral address and command macros.
 * 
 * @author Duy Hung Nguyen
 * @date June 25th 2023 
 */

#ifndef BH1750_H_
#define BH1750_H_

#include "stdint.h"

#define BH1750_ADDRESS 0x23

#define COMMAND_POWER_ON 0x01
#define COMMAND_POWER_OFF 0x00
#define COMMAND_RESET 0x07
#define COMMAND_CONT_HRESO 0x10
#define COMMAND_CONT_HRESO2 0x11
#define COMMAND_CONT_LRESO 0x13
#define COMMAND_ONCE_HRESO 0x20
#define COMMAND_ONCE_HRESO2 0x21
#define COMMAND_ONCE_LRESO2 0x23

/**
 * @brief Light ambient measurement function
 * 
 * Perform once light ambient measurement in high resolution 2 mode. The resolution is 0.5 lx
 * Light ambient = Results * Resolution / 1.2 (lx)
 *
 * @return 16 bit value encoding the light ambient value
 */

uint16_t measure_high_resolution2_once();

#endif /* BH1750_H_ */
