/*
 * si7021.c
 *
 *  Created on: Jun 25, 2023
 *      Author: Duy Hung Nguyen
 */
#include "sl_i2cspm.h"
#include "si7021.h"
#include "app_log.h"

extern I2C_TransferSeq_TypeDef seq;
extern uint8_t readData[2];
extern I2C_TransferReturn_TypeDef ret;
extern uint8_t command;

uint16_t measure_relative_humidity(){
  seq.addr = SI7021_ADDRESS << 1;
  seq.flags = I2C_FLAG_WRITE;
  command = SI7021_MEASURE_HUMD_NHM;
  seq.buf[0].data = &command;
  seq.buf[0].len = 1;
  ret = I2CSPM_Transfer(I2C0, &seq);
  if (ret != i2cTransferDone){
      app_log("Error in write process\n");
      return 0xFFFF;
  }
  seq.flags = I2C_FLAG_READ;
  seq.buf[0].data = &readData[0];
  seq.buf[0].len = 2;
  ret = I2CSPM_Transfer(I2C0, &seq);
  if (ret != i2cTransferDone){
        app_log("Error in read process\n");
        return 0xFFFF;
  }
  return ((uint16_t)readData[0]<<8)|((uint16_t)readData[1]);
}

uint16_t measure_temperature(){
  seq.addr = SI7021_ADDRESS << 1;
  seq.flags = I2C_FLAG_WRITE;
  command = SI7021_MEASURE_TEMP_NHM;
  seq.buf[0].data = &command;
  seq.buf[0].len = 1;
  ret = I2CSPM_Transfer(I2C0, &seq);
  if (ret != i2cTransferDone){
      app_log("Error in write process\n");
      return 0xFFFF;
  }
  seq.flags = I2C_FLAG_READ;
  seq.buf[0].data = &readData[0];
  seq.buf[0].len = 2;
  ret = I2CSPM_Transfer(I2C0, &seq);
  if (ret != i2cTransferDone){
        app_log("Error in read process\n");
        return 0xFFFF;
  }
  return ((uint16_t)readData[0]<<8)|((uint16_t)readData[1]);
}