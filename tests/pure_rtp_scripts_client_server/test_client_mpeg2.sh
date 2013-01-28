#!/bin/sh
VIDEO_UDP_PORT=9978

gst-launch-0.10 --gst-debug-level=0 udpsrc port=$VIDEO_UDP_PORT ! mpegtsdemux ! mpeg2dec ! ffmpegcolorspace ! ximagesink sync=false
