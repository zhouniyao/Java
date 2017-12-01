LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ArrayNdk
LOCAL_SRC_FILES := ArrayNdk.cpp

include $(BUILD_SHARED_LIBRARY)
