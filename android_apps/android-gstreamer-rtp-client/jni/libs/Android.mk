LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := gstudp
LOCAL_SRC_FILES := libgstudp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gstrtp
LOCAL_SRC_FILES := libgstrtp.so
include $(PREBUILT_SHARED_LIBRARY)
