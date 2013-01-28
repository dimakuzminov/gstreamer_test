LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:=libgstrtp

LOCAL_MODULE_TAGS:=eng debug 

LOCAL_SRC_FILES := \
	fnv1hash.c \
	gstasteriskh263.c \
	gstrtpac3depay.c \
	gstrtpac3pay.c \
	gstrtpamrdepay.c \
	gstrtpamrpay.c \
	gstrtpbvdepay.c \
	gstrtpbvpay.c \
	gstrtp.c \
	gstrtpceltdepay.c \
	gstrtpceltpay.c \
	gstrtpchannels.c \
	gstrtpdepay.c \
	gstrtpdvdepay.c \
	gstrtpdvpay.c \
	gstrtpg722depay.c \
	gstrtpg722pay.c \
	gstrtpg723depay.c \
	gstrtpg723pay.c \
	gstrtpg726depay.c \
	gstrtpg726pay.c \
	gstrtpg729depay.c \
	gstrtpg729pay.c \
	gstrtpgsmdepay.c \
	gstrtpgsmpay.c \
	gstrtpgstdepay.c \
	gstrtpgstpay.c \
	gstrtph263depay.c \
	gstrtph263pay.c \
	gstrtph263pdepay.c \
	gstrtph263ppay.c \
	gstrtph264depay.c \
	gstrtph264pay.c \
	gstrtpilbcdepay.c \
	gstrtpilbcpay.c \
	gstrtpj2kdepay.c \
	gstrtpj2kpay.c \
	gstrtpjpegdepay.c \
	gstrtpjpegpay.c \
	gstrtpL16depay.c \
	gstrtpL16pay.c \
	gstrtpmp1sdepay.c \
	gstrtpmp2tdepay.c \
	gstrtpmp2tpay.c \
	gstrtpmp4adepay.c \
	gstrtpmp4apay.c \
	gstrtpmp4gdepay.c \
	gstrtpmp4gpay.c \
	gstrtpmp4vdepay.c \
	gstrtpmp4vpay.c \
	gstrtpmpadepay.c \
	gstrtpmpapay.c \
	gstrtpmparobustdepay.c \
	gstrtpmpvdepay.c \
	gstrtpmpvpay.c \
	gstrtppcmadepay.c \
	gstrtppcmapay.c \
	gstrtppcmudepay.c \
	gstrtppcmupay.c \
	gstrtpqcelpdepay.c \
	gstrtpqdmdepay.c \
	gstrtpsirendepay.c \
	gstrtpsirenpay.c \
	gstrtpspeexdepay.c \
	gstrtpspeexpay.c \
	gstrtpsv3vdepay.c \
	gstrtptheoradepay.c \
	gstrtptheorapay.c \
	gstrtpvorbisdepay.c \
	gstrtpvorbispay.c \
	gstrtpvrawdepay.c \
	gstrtpvrawpay.c 


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
