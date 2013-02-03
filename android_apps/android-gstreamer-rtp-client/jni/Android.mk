LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libgstudp_temp
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/gstreamer-0.10/static/libgstudp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE := gstudp
LOCAL_WHOLE_STATIC_LIBRARIES := gstudp_temp
LOCAL_SHARED_LIBRARIES := gstreamer_android
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgstrtp_temp
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/gstreamer-0.10/static/libgstrtp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE := gstrtp
LOCAL_WHOLE_STATIC_LIBRARIES := gstrtp_temp
LOCAL_SHARED_LIBRARIES := gstreamer_android
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgstffmpeg_temp
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/gstreamer-0.10/static/libgstffmpeg.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/libavutil.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/libavcodec.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/libavformat.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libz
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/libz.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libbz2_temp
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/libbz2.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := bz2
LOCAL_WHOLE_STATIC_LIBRARIES := bz2_temp
LOCAL_SHARED_LIBRARIES := gstreamer_android
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gstffmpeg
LOCAL_WHOLE_STATIC_LIBRARIES := gstffmpeg_temp
LOCAL_STATIC_LIBRARIES := avformat avcodec avutil z
LOCAL_SHARED_LIBRARIES := gstreamer_android bz2
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgstffmpegcolorspace_temp
LOCAL_SRC_FILES := ../../../external/gstreamer-android-sdk-2012-11/lib/gstreamer-0.10/static/libgstffmpegcolorspace.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gstffmpegcolorspace
LOCAL_WHOLE_STATIC_LIBRARIES := gstffmpegcolorspace_temp
LOCAL_STATIC_LIBRARIES := avformat avcodec avutil z
LOCAL_SHARED_LIBRARIES := gstreamer_android bz2
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := gst_rtp_client
LOCAL_SRC_FILES := gst_rtp_client.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog -landroid
include $(BUILD_SHARED_LIBRARY)


ifndef GSTREAMER_SDK_ROOT
ifndef GSTREAMER_SDK_ROOT_ANDROID
$(error GSTREAMER_SDK_ROOT_ANDROID is not defined!)
endif
GSTREAMER_SDK_ROOT        := $(GSTREAMER_SDK_ROOT_ANDROID)
endif
GSTREAMER_NDK_BUILD_PATH  := $(GSTREAMER_SDK_ROOT)/share/gst-android/ndk-build/
include $(GSTREAMER_NDK_BUILD_PATH)/plugins.mk
GSTREAMER_PLUGINS         := coreelements coreindexers adder app audioconvert audiorate audioresample audiotestsrc ffmpeg ffmpegcolorspace \
							gdp gio pango typefindfunctions videorate videoscale videotestsrc volume autodetect videofilter \
							mpegtsdemux mpeg2dec $(GSTREAMER_PLUGINS_SYS) $(GSTREAMER_PLUGINS_NET) $(GSTREAMER_PLUGINS_EFFECTS)
GSTREAMER_EXTRA_DEPS      := gstreamer-interfaces-0.10 gstreamer-video-0.10 zlib
GSTREAMER_LD              := -lbz2 -lavcodec -lavutil -lavformat

include $(GSTREAMER_NDK_BUILD_PATH)/gstreamer.mk
