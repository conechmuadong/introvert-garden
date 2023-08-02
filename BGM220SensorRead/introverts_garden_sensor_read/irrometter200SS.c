/**
 * @file irrometter200SS.c 
 * @brief Soil humidity sensor Irrometter 200SS' frequency measurement method source file.
 * 
 * @author Duy Hung Nguyen
 * @date July 20th 2023
*/
#include "em_common.h"
#include "app.h"
#include "em_gpio.h"
#include "em_timer.h"
#include "em_cmu.h"
#include "app_log.h"
#include "irrometter200SS.h"

uint32_t firstEdge;
uint32_t secondEdge;
uint32_t period;

bool overflow = false; //Overflow flag

void TIMER1_init (void)
{
  // Initialize the CMU
  CMU_ClockEnable (cmuClock_GPIO, true);
  CMU_ClockEnable (cmuClock_TIMER1, true);
  
  // Initialize GPIO pin as input
  GPIO_PinModeSet (gpioPortB, 1, gpioModeInput, 0);
  
  // Initialize the timer
  TIMER_Init_TypeDef timerInit = TIMER_INIT_DEFAULT;

  // Configure TIMER1 for input capture mode
  TIMER_InitCC_TypeDef timerCCInit = TIMER_INITCC_DEFAULT;

  timerCCInit.mode = timerCCModeCapture;
  timerCCInit.edge = timerEdgeRising;             // Input capture on rising edge
  timerCCInit.eventCtrl = timerEventEvery2ndEdge; // Interrupt on every other edge
  timerInit.enable = false;

  TIMER_Init (TIMER1, &timerInit);

  // Route TIMER1 CC0 input from PB1
  GPIO->TIMERROUTE[1].ROUTEEN = GPIO_TIMER_ROUTEEN_CC0PEN;
  GPIO->TIMERROUTE[1].CC0ROUTE = (gpioPortB << _GPIO_TIMER_CC0ROUTE_PORT_SHIFT)
      | (1 << _GPIO_TIMER_CC0ROUTE_PIN_SHIFT);

  TIMER_InitCC (TIMER1, 0, &timerCCInit);

  // Enable TIMER1 interrupts
  TIMER_TopSet (TIMER1, 0xFFFFFFFF);
  TIMER_IntClear (TIMER1, _TIMER_IF_MASK);
  TIMER_IntEnable (TIMER1, TIMER_IEN_CC0);
  NVIC_EnableIRQ (TIMER1_IRQn);
}
/**
 * TIMER1 Interrupt event handle function 
*/
void TIMER1_IRQHandler (void)
{
  // Get the interrupt flags
  uint32_t flags = TIMER_IntGet (TIMER1);

  // Get the time at which each rising edge was captured
  firstEdge = TIMER_CaptureGet(TIMER1, 0);
  secondEdge = TIMER_CaptureGet(TIMER1, 0);

  // Checking overflow between edges
  if (flags & TIMER_IF_OF)
    {
      overflow = true;
    }
  if(!(flags & TIMER_IF_OF)){
          overflow = false;
  }
  uint32_t countsBetweenEdges;

  /*
   * Calculate the frequency of TIMER1 from the bus clock.  This
   * assumes the pre-scaler remains at the default value of 1.
   */
  uint32_t timerClockMHz = CMU_ClockFreqGet (cmuClock_EM01GRPACLK) / 1000000;

  /*
   * Calculate the count between edges depending on whether or not
   * there was an overflow.
   */
  if (overflow)
    countsBetweenEdges = TIMER_TopGet (TIMER1) - firstEdge + 1 + secondEdge;
  else
    countsBetweenEdges = secondEdge - firstEdge;

  // Reset the overflow flag
  overflow = false;

  // Convert the count between edges to a period in microseconds
  period = (countsBetweenEdges / timerClockMHz);

  // Clear the interrupt flags and exit
  TIMER_IntClear (TIMER1, flags);
}
uint32_t calculatePeriod (void)
{
  return period;
}
