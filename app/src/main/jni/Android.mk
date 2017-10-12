LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=STATIC
ifdef OPENCV_ANDROID_SDK
  ifneq ("","$(wildcard $(OPENCV_ANDROID_SDK)/OpenCV.mk)")
    include ${OPENCV_ANDROID_SDK}/OpenCV.mk
  else
    include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
  endif
else
  include ../../../../../OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
endif
LOCAL_CFLAGS += -fopenmp

LOCAL_SRC_FILES  := mrcar.cpp
LOCAL_SRC_FILES  +=$(wildcard src/core/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard src/util/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard src/train/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard src/preprocess/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard thirdparty/mser/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard thirdparty/LBP/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard thirdparty/textDetect/*.cpp)
LOCAL_SRC_FILES  +=$(wildcard thirdparty/xmlParser/*.cpp)

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += "include"
LOCAL_C_INCLUDES += "thirdparty"
LOCAL_LDLIBS     += -llog -ldl -landroid  -fopenmp 

LOCAL_MODULE     := mrcar

include $(BUILD_SHARED_LIBRARY)

