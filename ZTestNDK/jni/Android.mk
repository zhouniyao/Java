LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ZTestNDK
LOCAL_SRC_FILES := ZTestNDK.cpp

include $(BUILD_SHARED_LIBRARY)
