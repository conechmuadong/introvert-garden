/***************************************************************************//**
 * @file
 * @brief Top level application functions
 *******************************************************************************
 * # License
 * <b>Copyright 2020 Silicon Laboratories Inc. www.silabs.com</b>
 *******************************************************************************
 *
 * The licensor of this software is Silicon Laboratories Inc. Your use of this
 * software is governed by the terms of Silicon Labs Master Software License
 * Agreement (MSLA) available at
 * www.silabs.com/about-us/legal/master-software-license-agreement. This
 * software is distributed to you in Source Code format and is governed by the
 * sections of the MSLA applicable to Source Code.
 *
 ******************************************************************************/
#include "em_device.h"
#include "em_cmu.h"
#include "em_emu.h"
#include "em_chip.h"
#include "em_gpio.h"
#include "em_timer.h"
#include "app_log.h"

// Stored edges from interrupt
volatile uint32_t firstEdge;
volatile uint32_t secondEdge;
/***************************************************************************//**
 * Initialize application.
 ******************************************************************************/

void TIMER0_IRQHandler(void);
void init_GPIO(void);
void initTimer0(void);
void initHFXO(void);
void initCMU(void);

void app_init(void)
{
  initHFXO();
  initCMU();
  init_GPIO();
  initTimer0();
}

/***************************************************************************//**
 * App ticking function.
 ******************************************************************************/
void app_process_action(void)
{
   EMU_EnterEM1();
}
void TIMER0_IRQHandler(void){
  uint32_t flags = TIMER_IntGet(TIMER0);
  TIMER_IntClear(TIMER0, flags);

  // Read the last two captured edges
   // Note: interrupt occurs after the second capture
   firstEdge = TIMER_CaptureGet(TIMER0, 0);
   secondEdge = TIMER_CaptureGet(TIMER0, 0);
   app_log("%d\n", firstEdge - secondEdge);
}
void initCMU(void){
  CMU_ClockSelectSet(cmuClock_EM01GRPACLK, cmuSelect_HFXO);
  CMU_ClockEnable(cmuClock_GPIO, true);
}
void initHFXO(void)
{
  // Initialize the HFXO with the board specific settings
  CMU_HFXOInit_TypeDef hfxoInit = CMU_HFXOINIT_DEFAULT;
  CMU_HFXOInit(&hfxoInit);

  // Enable HFXO and wait for it to stabilize
  CMU_OscillatorEnable(cmuOsc_HFXO, true, true);
}
void init_GPIO(void){
  GPIO_PinModeSet(gpioPortB, 1, gpioModeInput, 0);
}

void initTimer0(void){
  TIMER_Init_TypeDef timerInit = TIMER_INIT_DEFAULT;

  TIMER_InitCC_TypeDef timerCCInit = TIMER_INITCC_DEFAULT;

  timerCCInit.mode = timerCCModeCapture;
  timerCCInit.edge = timerEdgeBoth;
  timerCCInit.eventCtrl = timerEventEvery2ndEdge;
  timerInit.enable = false;

  TIMER_Init(TIMER0, &timerInit);

  GPIO->TIMERROUTE[0].ROUTEEN = GPIO_TIMER_ROUTEEN_CC0PEN;
  GPIO->TIMERROUTE[0].CC0ROUTE = (gpioPortB << _GPIO_TIMER_CC0ROUTE_PORT_SHIFT)
                                | (1 << _GPIO_TIMER_CC0ROUTE_PIN_SHIFT);

  TIMER_InitCC(TIMER0, 0, &timerCCInit);

  TIMER_IntEnable(TIMER0, TIMER_IEN_CC0);
  NVIC_EnableIRQ(TIMER0_IRQn);

  TIMER_Enable(TIMER0, true);
}

