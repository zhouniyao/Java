# This is a generated file. Please do not edit.

.PHONY: all

COMMANDS := 	\
	    scd_cmd_1

all: $(COMMANDS)

scd_cmd_1:
	@echo begin generating scanner info for $@
	D:/Eclipse_Android_Building/OpenGLES_build/android-ndk-r8/toolchains/arm-linux-androideabi-4.4.3/prebuilt/windows/bin/arm-linux-androideabi-g++ -D '__ARM_ARCH_5__=1' -D '__ARM_ARCH_5T__=1' -D '__ARM_ARCH_5E__=1' -D '__ARM_ARCH_5TE__=1' -march=armv5te -mtune=xscale -msoft-float -fno-exceptions -mthumb -Os -finline-limit=64 -D 'ANDROID=1' -O2 -D 'NDEBUG=1' -I "D:\Eclipse_Android_Building\OpenGLES_build\android-ndk-r8\sources\cxx-stl\system\include" -I "F:\AndroidWorkSpace\ZTestNDK\jni" -I "D:\Eclipse_Android_Building\OpenGLES_build\android-ndk-r8\platforms\android-14\arch-arm\usr\include" -E -P -v -dD specs.cpp
	@echo end generating scanner info for $@


