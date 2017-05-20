#ifndef MR_UTIL_H_
#define MR_UTIL_H_

#include <android/log.h>
#define LOG_TAG "MRCar"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

#endif
