

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := TestSm3
LS_CPP=$(subst $(1)/,,$(wildcard $(1)/*.cpp))
LOCAL_SRC_FILES := $(call LS_CPP,$(LOCAL_PATH))

include $(BUILD_SHARED_LIBRARY)

