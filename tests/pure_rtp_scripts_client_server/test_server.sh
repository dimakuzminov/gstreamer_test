#!/bin/sh
AV=/dev/video0
VIDEO_UDP_PORT=9978
width=640
height=480
gst-launch-0.10 --gst-debug-level=0 v4l2src device=$AV ! video/x-raw-yuv,width=$width,height=$height ! \
videorate ! ffmpegcolorspace ! video/x-raw-yuv,width=$width,height=$height ! \
x264enc tune=zerolatency byte-stream=true bitrate=3000 speed-preset=ultrafast ! rtph264pay name=pay0 pt=96 ! \
udpsink host=127.0.0.1 port=$VIDEO_UDP_PORT sync=false
