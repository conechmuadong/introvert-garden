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

I2C_TransferSeq_TypeDef ls_seq;
uint8_t readBuffer[2];
uint8_t command;
I2C_TransferReturn_TypeDef ls_ret;

uint16_t measure_high_resolution2_once(){
  ls_seq.addr = BH1750_ADDRESS;
  command = COMMAND_ONCE_HRESO2;
  ls_seq.flags = I2C_FLAG_WRITE;
  ls_seq.buf[0].data = &command;
  ls_seq.buf[0].len = 1;
  ls_ret = I2CSPM_Transfer(I2C0, &ls_seq);

  sl_udelay_wait(120000);

  ls_seq.flags = I2C_FLAG_READ;
  ls_seq.buf[0].data = &readBuffer[0];
  ls_seq.buf[0].len = 2;
  ls_ret = I2CSPM_Transfer(I2C0, &ls_seq);
  ls_ret = I2CSPM_Transfer(I2C0, &ls_seq);
  return ((uint16_t)readBuffer[0])<<8|(uint16_t)readBuffer[1];
}

