GST_RTSP=gst-rtsp-0.10.8
GST=gstreamer-0.10.36
GST_PLUGINS_BASE=gst-plugins-base-0.10.36

all: gst-rtsp gst gst-plugins

gst-rtsp: $(GST_RTSP).tar.bz2
	if [ ! -d $(GST_RTSP) ]; \
	then \
		tar xf $<; \
		cd $(GST_RTSP); ./configure; \
	fi
	cd $(GST_RTSP); make

gst: $(GST).tar.xz
	if [ ! -d $(GST) ]; \
	then \
		tar xf $<; \
		cd $(GST); \
		./configure; \
		sed "s;\#elif G_OS_UNIX;\#elif defined\(G_OS_UNIX\);" -i ./plugins/elements/gstfilesink.c; \
	fi
	cd $(GST); make

gst-plugins: $(GST_PLUGINS_BASE).tar.bz2
	if [ ! -d $(GST_PLUGINS_BASE) ]; \
	then \
		tar xf $<; \
		cd $(GST_PLUGINS_BASE); \
		./configure; \
	fi
	cd $(GST_PLUGINS_BASE); make


install:
	cd $(GST_RTSP); make install
	cd $(GST); make install
	cd $(GST_PLUGINS_BASE); make install

install-gst:
	cd $(GST); make install

install-plugins:
	cd $(GST_PLUGINS_BASE); make install

install-rtsp:
	cd $(GST_RTSP); make install

install:
	cd $(GST_RTSP); make install
	cd $(GST); make install
	cd $(GST_PLUGINS_BASE); make install

clean-gst:
	rm -fr $(GST)

clean-plugins:
	rm -fr $(GST_PLUGINS_BASE)

clean-rtsp:
	rm -fr $(GST_RTSP)

clean:
	rm -fr $(GST_RTSP)
	rm -fr $(GST)
	rm -fr $(GST_PLUGINS_BASE)
