/*
 * bh1750.c
 *
 *  Created on: Jun 25, 2023
 *      Author: Duy Hung Nguyen
 *
 *  BH1750's I2C communication sources file
 */

#include "bh1750.h"
#include "sl_i2cspm.h"
#include "app_log.h"

I2C_TransferSeq_TypeDef ls_seq;
extern uint8_t readData[2];
uint8_t command;
extern I2C_TransferReturn_TypeDef ret;

uint16_t measure_high_resolution2_once(){
  ls_seq.addr = BH1750_ADDRESS << 1;
  command = COMMAND_ONCE_HRESO;
  ls_seq.flags = I2C_FLAG_WRITE;
  ls_seq.buf[0].data = &command;
  ls_seq.buf[0].len = 1;
  ret = I2CSPM_Transfer(I2C0, &ls_seq);

  sl_udelay_wait(120000);

  ls_seq.flags = I2C_FLAG_READ;
  ls_seq.buf[0].data = &readData[0];
  ls_seq.buf[0].len = 2;
  ret = I2CSPM_Transfer(I2C0, &ls_seq);
//ret = I2CSPM_Transfer(I2C0, &ls_seq);
  app_log("Results: %d %d \n", readData[0], readData[1]);
  return ((uint16_t)readData[0])<<8|(uint16_t)readData[1];
}

