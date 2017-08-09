LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

APP_BUILD_SCRIPT := Android.mk
LOCAL_SRC_FILES:= ping.c ping_common.c
LOCAL_MODULE := ping
include $(BUILD_EXECUTABLE)
