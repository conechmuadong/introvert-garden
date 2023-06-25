/*
 * pcf8591.c
 *
 *  Created on: Jun 25, 2023
 *      Author: Duy Hung Nguyen
 *
 * ADC PCF8591's I2C communication source file
 */

#include "pcf8591.h"
#include "sl_i2cspm.h"

I2C_TransferSeq_TypeDef seq;
uint8_t readData[2];
uint8_t controlByte = CONTROL_BYTE_CH0;
I2C_TransferReturn_TypeDef ret;

uint8_t read_soil_humidity_sensor_voltage_value(){
    seq.addr = PCF8591_ADDRESS << 1;
    seq.buf[0].data = &controlByte;
    seq.buf[0].len = 1;
    seq.flags = I2C_FLAG_WRITE;
    I2CSPM_Transfer(I2C0, &seq);

    seq.buf[0].data = &readData[0];
    seq.flags = I2C_FLAG_READ;
    seq.buf[0].len = 2;
    I2CSPM_Transfer(I2C0,&seq);
    I2CSPM_Transfer(I2C0,&seq);
    return readData[0];
}
