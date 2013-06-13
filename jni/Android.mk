# Makefile
#	Copyright (C) Max Kastanas 2010
#	Copyright (C) Naval Saini 2010

# * This program is free software; you can redistribute it and/or modify
# * it under the terms of the GNU General Public License as published by
# * the Free Software Foundation; either version 2 of the License, or
# * (at your option) any later version.
# *
# * This program is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# * GNU General Public License for more details.
# *
# * You should have received a copy of the GNU General Public License
# * along with this program; if not, write to the Free Software
# * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

LOCAL_PATH:= $(call my-dir)


## libfftw module
include $(CLEAR_VARS)

LOCAL_MODULE    := libfftw
LOCAL_CFLAGS    := -Werror -g\
	-I$(LOCAL_PATH)/fftw \
	-I$(LOCAL_PATH)/fftw/api \
	-I$(LOCAL_PATH)/fftw/kernel \
	-I$(LOCAL_PATH)/fftw/dft \
	-I$(LOCAL_PATH)/fftw/rdft \
	-I$(LOCAL_PATH)/fftw/reodft \
	-I$(LOCAL_PATH)/fftw/simd \
	-I$(LOCAL_PATH)/fftw/rdft/simd \
-I$(LOCAL_PATH)/fftw/rdft/scalar \
-I$(LOCAL_PATH)/fftw/dft/simd \
-I$(LOCAL_PATH)/fftw/dft/scalar

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./fftw/ -type f -name '*.c'; find ./fftw/ -type f -name '*.cpp')

LOCAL_LDLIBS    := \
                   -llog -lm

include $(BUILD_STATIC_LIBRARY)



## libsndfile module
include $(CLEAR_VARS)

LOCAL_MODULE    := libsndfile
LOCAL_CFLAGS    := -Werror -g\
-I$(LOCAL_PATH)/libsndfile \
-I$(LOCAL_PATH)/include

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./libsndfile/ -type f -name '*.c'; find ./libsndfile/ -type f -name '*.cpp')

LOCAL_LDLIBS    := \
                   -llog -lm

include $(BUILD_STATIC_LIBRARY)



## libsamplerate module
include $(CLEAR_VARS)

LOCAL_MODULE    := libsamplerate
LOCAL_CFLAGS    := -Werror -g\
-I$(LOCAL_PATH)/libsamplerate

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./libsamplerate/ -type f -name '*.c'; find ./libsamplerate/ -type f -name '*.cpp')

LOCAL_LDLIBS    := \
                   -llog -lm

include $(BUILD_STATIC_LIBRARY)

## libaubio module
include $(CLEAR_VARS)

LOCAL_MODULE    := libaubio
LOCAL_CFLAGS    := -Werror -g\
	-I$(LOCAL_PATH)/aubio \
	-I$(LOCAL_PATH)/aubio/ext \
	-I$(LOCAL_PATH)/aubio/src \
	-I$(LOCAL_PATH)/libsamplerate \
	-I$(LOCAL_PATH)/fftw \
	-I$(LOCAL_PATH)/libsndfile \
	-I$(LOCAL_PATH)/include

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./aubio/ -type f -name '*.c'; find ./aubio/ -type f -name '*.cpp';find ./atm/ -type f -name '*.c'; find ./atm/ -type f -name '*.cpp')
LOCAL_STATIC_LIBRARIES := libfftw libsndfile libsamplerate
LOCAL_LDLIBS    := \
                   -llog -lm -lfftw -llibsndfile -llibsamplerate

include $(BUILD_STATIC_LIBRARY)


## libatm module
include $(CLEAR_VARS)

LOCAL_MODULE    := libatm
LOCAL_CFLAGS    := -Werror -g\
	-I$(LOCAL_PATH)/atm \
	-I$(LOCAL_PATH)/aubio \
	-I$(LOCAL_PATH)/aubio/ext \
	-I$(LOCAL_PATH)/aubio/src \
	-I$(LOCAL_PATH)/libsamplerate \
	-I$(LOCAL_PATH)/fftw \
	-I$(LOCAL_PATH)/libsndfile \
	-I$(LOCAL_PATH)/include

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./aubio/ -type f -name '*.c'; find ./aubio/ -type f -name '*.cpp';find ./atm/ -type f -name '*.c'; find ./atm/ -type f -name '*.cpp')
LOCAL_STATIC_LIBRARIES := libfftw libsndfile libsamplerate libaubio
LOCAL_LDLIBS    := \
                   -llog -lm

include $(BUILD_SHARED_LIBRARY)