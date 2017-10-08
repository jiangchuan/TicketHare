package io.chizi.tickethare;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import static io.chizi.tickethare.acquire.PlateRecognizer.plateRecognition;

/**
 * Created by Jiangchuan on 10/1/17.
 */

public class LPScanActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "openCVCamera";
    private CameraBridgeViewBase cameraBridgeViewBase;

    private Mat recognizeMat;
    private boolean inRecognizing = false;

    private TextView recognizeResultTextView;
    private StringBuilder stringBuilder = new StringBuilder();


    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    System.loadLibrary("platerecognizer");
                    cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }

        }
    };

    private class PlateRecognizeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String recognizeResult = null;
            if (recognizeMat != null) {
                recognizeResult = plateRecognition(recognizeMat.getNativeObjAddr(), recognizeMat.getNativeObjAddr());
            }
            return recognizeResult;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equalsIgnoreCase("0") && result.length() == 10) {
                stringBuilder.append(result);
                stringBuilder.append(", ");
                recognizeResultTextView.setText(stringBuilder.toString());
//                Toast.makeText(LPScanActivity.this, result, Toast.LENGTH_LONG).show();
//                licenseNum = result.substring(3);
//                licenseColor = result.substring(0, 1);
            } else {
            }
            inRecognizing = false;
        }
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        loadLibAndEnableView();
    }

    private void loadLibAndEnableView() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library not loaded!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV loaded successfully!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {}

    @Override
    public void onCameraViewStopped() {}

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




























