#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include "util.h"
#include "easypr.h"
using namespace std;
using namespace cv;
using namespace easypr;
#ifdef __cplusplus
extern "C" {
#endif
//string mappingpath="/sdcard/mrcar/province_mapping.xml";
JNIEXPORT jstring JNICALL Java_io_chizi_tickethare_util_MRCarUtil_plateRecognition(JNIEnv *env, jclass type, jlong matImg,jlong matResult) {
    LOGI("plateRecognition entering");
    Mat &img=*(Mat *)matImg;
    cvtColor(img, img, cv::COLOR_RGBA2BGR);
    Mat &result=*(Mat *)matResult;
    vector<Mat> resultVec;
    CPlateRecognize pr;
    pr.setResultShow(false);
    pr.setDetectType(PR_DETECT_CMSER);
    string license;
    vector<CPlate> plateVec;
    MRTimer tm;
    tm.start();
    int re= pr.plateRecognize(img, plateVec);
    tm.stop();
    tm.log4debug("Recognize Cost time:");
    for (int i = 0; i < plateVec.size(); i++)
    {
        CPlate plate = plateVec.at(i);
        license = plate.getPlateStr();
        break;
    }
    result=img.clone();
    cvtColor(result,result, cv::COLOR_RGB2BGRA);
    return env->NewStringUTF(license.c_str());
}

#ifdef __cplusplus
}
#endif