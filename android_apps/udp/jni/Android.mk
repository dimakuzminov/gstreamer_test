LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:=libgstudp

LOCAL_MODULE_TAGS:=eng debug 

LOCAL_SRC_FILES := \
	gstudp.c \
	gstudpsrc.c \
	gstudpsink.c \
	gstmultiudpsink.c \
	gstdynudpsink.c \
	gstudpnetutils.c \
	gstudp-enumtypes.c \
	gstudp-marshal.c

LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

ifndef GSTREAMER_SDK_ROOT
ifndef GSTREAMER_SDK_ROOT_ANDROID
$(error GSTREAMER_SDK_ROOT_ANDROID is not defined!)
endif
GSTREAMER_SDK_ROOT        := $(GSTREAMER_SDK_ROOT_ANDROID)
endif
GSTREAMER_NDK_BUILD_PATH  := $(GSTREAMER_SDK_ROOT)/share/gst-android/ndk-build/
include $(GSTREAMER_NDK_BUILD_PATH)/plugins.mk
GSTREAMER_PLUGINS         := $(GSTREAMER_PLUGINS_CORE) $(GSTREAMER_PLUGINS_SYS) $(GSTREAMER_PLUGINS_NET)
include $(GSTREAMER_NDK_BUILD_PATH)/gstreamer.mk
