LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := VRNDK01
LOCAL_SRC_FILES := VRNDK01.cpp

include $(BUILD_SHARED_LIBRARY)