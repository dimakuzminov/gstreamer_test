CC=gcc
CXX=g++

CXXSOURCES := $(shell ls *.cpp 2>/dev/null)
CSOURCES := $(shell ls *.c 2>/dev/null)
OBJECTS := $(CXXSOURCES:.cpp=.o) $(CSOURCES:.c=.o)
DEPFILES := $(OBJECTS:%.o=.%.P)
TARGET ?= gst-rtsp-server


CFLAGS += -g -I../include -D__USE_LZO__ -D__DEBUGING__ -I/usr/include/SDL -D__STDC_CONSTANT_MACROS \
		`pkg-config gtk+-2.0 gstreamer-0.10 gst-rtsp-server-0.10 --cflags`
CXXFLAGS += $(CFLAGS)

LDFLAGS += -g -llzo2 -lstdc++ -lm -lpng -lrt -lGL -lGLU -lglut -lpthread -lboost_thread \
	 	-L/usr/lib64 -lSDL -lSDL_mixer \
		`pkg-config gtk+-2.0 gstreamer-0.10 gst-rtsp-server-0.10 --libs`
# -Xlinker -rpath -Xlinker /usr/local/lib

all:$(TARGET) 

$(TARGET):$(OBJECTS)
	$(LINK.o) -o $@ $^ $(LDFLAGS)

clean:
	rm -f $(TARGET) *.o *~ \#*
	rm -f .*.P

DF=$(*F)

%.o:%.cpp
	$(COMPILE.cpp) -MMD -o $@ $<
	@cp $(DF).d .$(DF).P; sed -e 's/#.*//' -e 's/^[^:]*: *//' -e 's/ *\\$$//' -e '/^$$/ d' -e 's/$$/ :/' < $(DF).d >> .$(DF).P && rm -f $(DF).d

%.o:%.c
	$(COMPILE.c) -MMD -o $@ $<
	@cp $(DF).d .$(DF).P; sed -e 's/#.*//' -e 's/^[^:]*: *//' -e 's/ *\\$$//' -e '/^$$/ d' -e 's/$$/ :/' < $(DF).d >> .$(DF).P && rm -f $(DF).d

-include $(DEPFILES)
