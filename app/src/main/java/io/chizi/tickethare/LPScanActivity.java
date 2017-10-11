package io.chizi.tickethare;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;

import io.chizi.tickethare.util.MRCarUtil;

/**
 * Created by Jiangchuan on 10/10/17.
 */

public class LPScanActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase cameraBridgeViewBase;

    private Mat recognizeMat;
    private boolean inRecognizing = false;

    private TextView recognizeResultTextView;
    private StringBuilder stringBuilder = new StringBuilder();

    private ArrayList<String> licenseArray = new ArrayList<String>();
    private String licenseOfCar;
    static final int LICENSE_LENGTH = 7;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    System.loadLibrary("mrcar");
                    cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lpscan);
        recognizeResultTextView = (TextView) findViewById(R.id.textview_recognize_result);
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        loadLibAndEnableView();
    }

    private class PlateRecognizeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String recognizeResult = null;
            if (recognizeMat != null) {
                recognizeResult = MRCarUtil.plateRecognition(recognizeMat.getNativeObjAddr(), recognizeMat.getNativeObjAddr());
            }
            return recognizeResult;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equalsIgnoreCase("0") && result.length() == 10) {
//                Toast.makeText(LPScanActivity.this, result, Toast.LENGTH_LONG).show();
                String licenseNum = result.substring(3);
//                String licenseColor = result.substring(0, 1);

                if (checkDifferentLicense(licenseNum)) {
                    stringBuilder.append(licenseOfCar);
                }
//                stringBuilder.append(result);
                stringBuilder.append(", ");
                recognizeResultTextView.setText(stringBuilder.toString());
            }
            inRecognizing = false;
        }
    }

    private boolean checkDifferentLicense(String currentLicense) {
        if (licenseArray.isEmpty()) {
            licenseArray.add(currentLicense);
            return false;
        }
        String lastLicense = licenseArray.get(licenseArray.size() - 1);
        if (countDiffStrings(lastLicense, currentLicense) < 3) {
            licenseArray.add(currentLicense);
            return false;
        }
        voteLicense();
        licenseArray.clear();
        licenseArray.add(currentLicense);
        return true;
    }

    private int countDiffStrings(String str1, String str2) {
        int theCount = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                theCount++;
            }
        }
        return theCount;
    }

    private void voteLicense() {
        int licenseArraySize = licenseArray.size();
        if (licenseArraySize == 1) {
            licenseOfCar = licenseArray.get(0);
            return;
        }
        char[] licenseChar = new char[LICENSE_LENGTH];
        char[] charArray = new char[licenseArraySize];
        for (int i = 0; i < LICENSE_LENGTH; i++) {
            for (int j = 0; j < licenseArraySize; j++) {
                charArray[j] = licenseArray.get(j).charAt(i);
            }
            licenseChar[i] = getMajorityChar(charArray);
        }
        licenseOfCar = String.valueOf(licenseChar);
    }

    public static char getMajorityChar(char[] charArray) {
        char maxappearchar = ' ';
        int counter = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            // increment this character's cnt and compare it to our max.
            charcnt[ch]++;
            if (charcnt[ch] >= counter) {
                counter = charcnt[ch];
                maxappearchar = ch;
            }
        }
        return maxappearchar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLibAndEnableView();
    }

    private void loadLibAndEnableView() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frameMat = inputFrame.rgba();
        if (frameMat != null && !inRecognizing) {
            recognizeMat = new Mat();
            frameMat.copyTo(recognizeMat); //this will be used for the live stream
            if (recognizeMat != null) {
                inRecognizing = true;
                new PlateRecognizeTask().execute();
            }
        }
        return frameMat;
    }

}
