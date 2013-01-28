#!/bin/sh
AV=/dev/video0
VIDEO_UDP_PORT=9978
width=640
height=480
gst-launch-0.10 --gst-debug-level=0 v4l2src device=$AV ! video/x-raw-yuv,width=$width,height=$height ! \
videorate ! ffmpegcolorspace ! mpeg2enc ! \
udpsink host=192.168.43.1 port=$VIDEO_UDP_PORT sync=false
