LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# OpenCV
OPENCVROOT:= /Users/Jiangchuan/Documents/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/src/core/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/src/preprocess/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/src/train/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/src/util/*.cpp)

FILE_LIST += $(wildcard $(LOCAL_PATH)/thirdparty/LBP/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/thirdparty/mser/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/thirdparty/textDetect/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/thirdparty/xmlParser/*.cpp)


LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include
LOCAL_MODULE     := platerecognizer

#LOCAL_LDLIBS := -landroid
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)


# Add prebuilt baidu.so
include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_base_v4_1_1
LOCAL_SRC_FILES := libBaiduMapSDK_base_v4_1_1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_cloud_v4_1_1
LOCAL_SRC_FILES := libBaiduMapSDK_cloud_v4_1_1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_map_v4_1_1
LOCAL_SRC_FILES := libBaiduMapSDK_map_v4_1_1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_radar_v4_1_1
LOCAL_SRC_FILES := libBaiduMapSDK_radar_v4_1_1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_search_v4_1_1
LOCAL_SRC_FILES := libBaiduMapSDK_search_v4_1_1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_util_v4_1_1
LOCAL_SRC_FILES := libBaiduMapSDK_util_v4_1_1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := liblocSDK6a
LOCAL_SRC_FILES := liblocSDK6a.so
include $(PREBUILT_SHARED_LIBRARY)

