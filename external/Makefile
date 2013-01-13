GST_RTSP=gst-rtsp-0.10.8
GST=gstreamer-0.10.36
GST_PLUGINS_BASE=gst-plugins-base-0.10.36
GST_PLUGINS_GOOD=gst-plugins-good-0.10.31

all: gst-rtsp

gst-rtsp: $(GST_RTSP).tar.bz2 gst-plugins-good
	if [ ! -d $(GST_RTSP) ]; \
	then \
		tar xf $<; \
		cd $(GST_RTSP); ./configure; \
	fi
	cd $(GST_RTSP); make
	cd $(GST_RTSP); make install

gst: $(GST).tar.xz
	if [ ! -d $(GST) ]; \
	then \
		tar xf $<; \
		cd $(GST); \
		./configure; \
		sed "s;\#elif G_OS_UNIX;\#elif defined\(G_OS_UNIX\);" -i ./plugins/elements/gstfilesink.c; \
	fi
	cd $(GST); make
	cd $(GST); make install

gst-plugins: $(GST_PLUGINS_BASE).tar.bz2 gst
	if [ ! -d $(GST_PLUGINS_BASE) ]; \
	then \
		tar xf $<; \
		cd $(GST_PLUGINS_BASE); \
		./configure; \
	fi
	cd $(GST_PLUGINS_BASE); make
	cd $(GST_PLUGINS_BASE); make install

gst-plugins-good: $(GST_PLUGINS_GOOD).tar.xz gst-plugins
	if [ ! -d $(GST_PLUGINS_GOOD) ]; \
	then \
		tar xf $<; \
		cd $(GST_PLUGINS_GOOD); \
		./configure; \
	fi
	cd $(GST_PLUGINS_GOOD); make
	cd $(GST_PLUGINS_GOOD); make install

clean-gst:
	rm -fr $(GST)

clean-plugins:
	rm -fr $(GST_PLUGINS_BASE)

clean-plugins-good:
	rm -fr $(GST_PLUGINS_GOOD)

clean-rtsp:
	rm -fr $(GST_RTSP)

clean:
	rm -fr $(GST_RTSP)
	rm -fr $(GST)
	rm -fr $(GST_PLUGINS_BASE)
	rm -fr $(GST_PLUGINS_GOOD)
