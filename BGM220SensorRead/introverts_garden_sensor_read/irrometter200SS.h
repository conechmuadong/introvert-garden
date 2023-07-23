/**
 *  @file irrometter200SS.h
 *  @brief Header file for Irrometter200SS soil humidity sensor
 *
 *  @date Jul 20, 2023
 *  @author: Duy Hung Nguyen
 */

#ifndef IRROMETTER200SS_H_
#define IRROMETTER200SS_H_
#include "stdint.h"

/**
 * @brief Initialize function for TIMER1
 *
 * Initialize CMU, GPIO input mode and set up TIMER Capture mode for capture the rising edge
 */
void TIMER1_init (void);

/**
 * @brief Function to measure the input frequency's period
 *
 * This function will take results from timer and counter, which capture the rising
 * edge, then calculate the period of input frequency
 *
 * @return Period in microseconds
 */
uint32_t calculatePeriod (void);

#endif /* IRROMETTER200SS_H_ */
