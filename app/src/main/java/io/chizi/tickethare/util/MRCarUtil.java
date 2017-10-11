package io.chizi.tickethare.util;

/**
 * Created by Jiangchuan on 10/9/17.
 */

public class MRCarUtil {
    public static final String ApplicationDir="mrcar";
    public static final String svmPath="svm.xml";
    public static final String annPath="ann.xml";
    public static final String ann_chinesePath="ann_chinese.xml";
    public static final String mappingPath="province_mapping.xml";
    public static final  String initimgPath="test.jpg";
    public static native String plateRecognition(long matImg,long matResult);
}
