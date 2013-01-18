#!/bin/sh
VIDEO_UDP_PORT=9978

gst-launch-0.10 --gst-debug-level=0 udpsrc port=$VIDEO_UDP_PORT caps = 'application/x-rtp, media=(string)video, clock-rate=(int)90000, encoding-name=(string)H264' ! \
rtph264depay ! ffdec_h264 ! ffmpegcolorspace ! videoscale ! video/x-raw-yuv,width=640,height=480 ! ffmpegcolorspace ! xvimagesink
