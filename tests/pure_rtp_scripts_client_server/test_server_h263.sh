#!/bin/sh
AV=/dev/video0
VIDEO_UDP_PORT=9978
width=704
height=576
gst-launch-0.10 --gst-debug-level=1 v4l2src device=$AV ! \
video/x-raw-yuv, framerate=30/1, width=640, height=480 ! videoscale ! videorate ! \
video/x-raw-yuv,width=704,height=576,framerate=30/1 ! ffenc_h263 ! rtph263pay ! \
udpsink host=127.0.0.1 port=$VIDEO_UDP_PORT sync=false
