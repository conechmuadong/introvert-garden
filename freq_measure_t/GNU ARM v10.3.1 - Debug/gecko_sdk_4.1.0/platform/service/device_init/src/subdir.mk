################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_dcdc_s2.c \
C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_emu_s2.c \
C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_hfxo_s2.c \
C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_lfxo_s2.c \
C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_nvic.c 

OBJS += \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_dcdc_s2.o \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_emu_s2.o \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_hfxo_s2.o \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_lfxo_s2.o \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_nvic.o 

C_DEPS += \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_dcdc_s2.d \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_emu_s2.d \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_hfxo_s2.d \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_lfxo_s2.d \
./gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_nvic.d 


# Each subdirectory must supply rules for building sources it contributes
gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_dcdc_s2.o: C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_dcdc_s2.c gecko_sdk_4.1.0/platform/service/device_init/src/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GNU ARM C Compiler'
	arm-none-eabi-gcc -g3 -gdwarf-2 -mcpu=cortex-m33 -mthumb -std=c99 '-DDEBUG_EFM=1' '-DBGM220PC22HNA=1' '-DSL_BOARD_NAME="BRD4314A"' '-DSL_BOARD_REV="A02"' '-DSL_COMPONENT_CATALOG_PRESENT=1' -I"E:\BGM220 Projects\freq_measure_t\config" -I"E:\BGM220 Projects\freq_measure_t\autogen" -I"E:\BGM220 Projects\freq_measure_t" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/Device/SiliconLabs/BGM22/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//app/common/util/app_log" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//hardware/board/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/CMSIS/Core/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/device_init/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/emlib/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/iostream/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/toolchain/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/system/inc" -Os -Wall -Wextra -ffunction-sections -fdata-sections -imacrossl_gcc_preinclude.h -mfpu=fpv5-sp-d16 -mfloat-abi=hard -mcmse -c -fmessage-length=0 -MMD -MP -MF"gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_dcdc_s2.d" -MT"$@" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_emu_s2.o: C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_emu_s2.c gecko_sdk_4.1.0/platform/service/device_init/src/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GNU ARM C Compiler'
	arm-none-eabi-gcc -g3 -gdwarf-2 -mcpu=cortex-m33 -mthumb -std=c99 '-DDEBUG_EFM=1' '-DBGM220PC22HNA=1' '-DSL_BOARD_NAME="BRD4314A"' '-DSL_BOARD_REV="A02"' '-DSL_COMPONENT_CATALOG_PRESENT=1' -I"E:\BGM220 Projects\freq_measure_t\config" -I"E:\BGM220 Projects\freq_measure_t\autogen" -I"E:\BGM220 Projects\freq_measure_t" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/Device/SiliconLabs/BGM22/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//app/common/util/app_log" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//hardware/board/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/CMSIS/Core/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/device_init/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/emlib/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/iostream/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/toolchain/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/system/inc" -Os -Wall -Wextra -ffunction-sections -fdata-sections -imacrossl_gcc_preinclude.h -mfpu=fpv5-sp-d16 -mfloat-abi=hard -mcmse -c -fmessage-length=0 -MMD -MP -MF"gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_emu_s2.d" -MT"$@" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_hfxo_s2.o: C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_hfxo_s2.c gecko_sdk_4.1.0/platform/service/device_init/src/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GNU ARM C Compiler'
	arm-none-eabi-gcc -g3 -gdwarf-2 -mcpu=cortex-m33 -mthumb -std=c99 '-DDEBUG_EFM=1' '-DBGM220PC22HNA=1' '-DSL_BOARD_NAME="BRD4314A"' '-DSL_BOARD_REV="A02"' '-DSL_COMPONENT_CATALOG_PRESENT=1' -I"E:\BGM220 Projects\freq_measure_t\config" -I"E:\BGM220 Projects\freq_measure_t\autogen" -I"E:\BGM220 Projects\freq_measure_t" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/Device/SiliconLabs/BGM22/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//app/common/util/app_log" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//hardware/board/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/CMSIS/Core/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/device_init/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/emlib/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/iostream/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/toolchain/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/system/inc" -Os -Wall -Wextra -ffunction-sections -fdata-sections -imacrossl_gcc_preinclude.h -mfpu=fpv5-sp-d16 -mfloat-abi=hard -mcmse -c -fmessage-length=0 -MMD -MP -MF"gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_hfxo_s2.d" -MT"$@" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_lfxo_s2.o: C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_lfxo_s2.c gecko_sdk_4.1.0/platform/service/device_init/src/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GNU ARM C Compiler'
	arm-none-eabi-gcc -g3 -gdwarf-2 -mcpu=cortex-m33 -mthumb -std=c99 '-DDEBUG_EFM=1' '-DBGM220PC22HNA=1' '-DSL_BOARD_NAME="BRD4314A"' '-DSL_BOARD_REV="A02"' '-DSL_COMPONENT_CATALOG_PRESENT=1' -I"E:\BGM220 Projects\freq_measure_t\config" -I"E:\BGM220 Projects\freq_measure_t\autogen" -I"E:\BGM220 Projects\freq_measure_t" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/Device/SiliconLabs/BGM22/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//app/common/util/app_log" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//hardware/board/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/CMSIS/Core/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/device_init/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/emlib/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/iostream/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/toolchain/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/system/inc" -Os -Wall -Wextra -ffunction-sections -fdata-sections -imacrossl_gcc_preinclude.h -mfpu=fpv5-sp-d16 -mfloat-abi=hard -mcmse -c -fmessage-length=0 -MMD -MP -MF"gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_lfxo_s2.d" -MT"$@" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_nvic.o: C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk/platform/service/device_init/src/sl_device_init_nvic.c gecko_sdk_4.1.0/platform/service/device_init/src/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GNU ARM C Compiler'
	arm-none-eabi-gcc -g3 -gdwarf-2 -mcpu=cortex-m33 -mthumb -std=c99 '-DDEBUG_EFM=1' '-DBGM220PC22HNA=1' '-DSL_BOARD_NAME="BRD4314A"' '-DSL_BOARD_REV="A02"' '-DSL_COMPONENT_CATALOG_PRESENT=1' -I"E:\BGM220 Projects\freq_measure_t\config" -I"E:\BGM220 Projects\freq_measure_t\autogen" -I"E:\BGM220 Projects\freq_measure_t" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/Device/SiliconLabs/BGM22/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//app/common/util/app_log" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//hardware/board/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/CMSIS/Core/Include" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/device_init/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/emlib/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/iostream/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/common/toolchain/inc" -I"C:/Users/bannh/SimplicityStudio/SDKs/gecko_sdk//platform/service/system/inc" -Os -Wall -Wextra -ffunction-sections -fdata-sections -imacrossl_gcc_preinclude.h -mfpu=fpv5-sp-d16 -mfloat-abi=hard -mcmse -c -fmessage-length=0 -MMD -MP -MF"gecko_sdk_4.1.0/platform/service/device_init/src/sl_device_init_nvic.d" -MT"$@" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


