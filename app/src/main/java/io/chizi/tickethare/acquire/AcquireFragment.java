package io.chizi.tickethare.acquire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.ZoomStyle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.chizi.tickethare.R;
import io.chizi.tickethare.database.DBProvider;
import io.chizi.tickethare.database.TitlesFragment;
import io.chizi.tickethare.login.UpdateProfileActivity;
import io.chizi.tickethare.util.BitmapUtil;
import io.chizi.tickethare.util.FileUtil;

import static io.chizi.tickethare.acquire.PlateRecognizer.plateRecognition;
import static io.chizi.tickethare.database.DBProvider.KEY_ADDRESS;
import static io.chizi.tickethare.database.DBProvider.KEY_CAR_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_CAR_TYPE;
import static io.chizi.tickethare.database.DBProvider.KEY_DATETIME;
import static io.chizi.tickethare.database.DBProvider.KEY_DAY;
import static io.chizi.tickethare.database.DBProvider.KEY_HOUR;
import static io.chizi.tickethare.database.DBProvider.KEY_IMG1_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_IMG2_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_IMG3_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_LATITUDE;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_CORRECT;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_NUM;
import static io.chizi.tickethare.database.DBProvider.KEY_LONGITUDE;
import static io.chizi.tickethare.database.DBProvider.KEY_MAP_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_MINUTE;
import static io.chizi.tickethare.database.DBProvider.KEY_MONTH;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_CITY;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_DEPT;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_NAME;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_YEAR;
import static io.chizi.tickethare.util.AppConstants.ANDROID_DATA_DIR;
import static io.chizi.tickethare.util.AppConstants.BACK_LICENSE_COLOR;
import static io.chizi.tickethare.util.AppConstants.BACK_VEHICLE_COLOR;
import static io.chizi.tickethare.util.AppConstants.BACK_VEHICLE_TYPE;
import static io.chizi.tickethare.util.AppConstants.COMPRESS_RATIO;
import static io.chizi.tickethare.util.AppConstants.CURRENT_ADDRESS;
import static io.chizi.tickethare.util.AppConstants.CURRENT_IMG1_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.CURRENT_IMG2_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LATITUDE;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LICENSE_COLOR;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LICENSE_CORRECT;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LICENSE_NUM;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LONGITUDE;
import static io.chizi.tickethare.util.AppConstants.CURRENT_MAP_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_CITY;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_DEPT;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_NAME;
import static io.chizi.tickethare.util.AppConstants.CURRENT_TICKET_ID;
import static io.chizi.tickethare.util.AppConstants.CURRENT_USER_ID;
import static io.chizi.tickethare.util.AppConstants.CURRENT_VEHICLE_COLOR;
import static io.chizi.tickethare.util.AppConstants.CURRENT_VEHICLE_TYPE;
import static io.chizi.tickethare.util.AppConstants.FILE_INSDCARD_DIR;
import static io.chizi.tickethare.util.AppConstants.GOBACK_USER_ID;
import static io.chizi.tickethare.util.AppConstants.IMG1_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.IMG2_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.IMG3_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.JPEG_FILE_SUFFIX;
import static io.chizi.tickethare.util.AppConstants.MAP_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PREF_INSTALLED_KEY;
import static io.chizi.tickethare.util.AppConstants.REQUEST_BACK_IMAGE_CAPTURE;
import static io.chizi.tickethare.util.AppConstants.REQUEST_FRONT_IMAGE_CAPTURE;
import static io.chizi.tickethare.util.AppConstants.REQUEST_PREVIEW_SHOW;
import static io.chizi.tickethare.util.AppConstants.REQUEST_SIDE_IMAGE_CAPTURE;
import static io.chizi.tickethare.util.AppConstants.REQUEST_UPDATE_PROFILE;
import static io.chizi.tickethare.util.AppConstants.RUNTIME_DATA_DIR_ASSET;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_ADDRESS;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_IMG1_PATH;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_IMG2_PATH;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_IMG3_PATH;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_MAP_PATH;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_TIME;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_DAY;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_HOUR;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LATITUDE;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LICENSE_COLOR;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LICENSE_CORRECT;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LICENSE_NUM;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LONGITUDE;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_MINUTE;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_MONTH;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_POLICE_CITY;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_POLICE_DEPT;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_POLICE_NAME;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_TICKET_ID;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_VEHICLE_COLOR;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_VEHICLE_TYPE;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_YEAR;
import static io.chizi.tickethare.util.AppConstants.TRANS_IMAGE_H;
import static io.chizi.tickethare.util.AppConstants.TRANS_IMAGE_W;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class AcquireFragment extends Fragment {


    private static final String LOG_TAG = AcquireFragment.class.getName();
    private static final float VERTICAL_RATIO_W = 2.6f;
    private static final float VERTICAL_RATIO_H = 10.0f;
    private static final float HORIZONTAL_RATIO_W = 4.3f;
    private static final float HORIZONTAL_RATIO_H = 6.0f;
    private static final float RATIO_ENLARGE = 1.5f;
    private static final FlashMode[] FLASH_MODES = {
            FlashMode.AUTO,
            FlashMode.ALWAYS
    };
    private static int SCREEN_WIDTH; // 屏幕宽度（像素）
    private static int SCREEN_HEIGHT; // 屏幕高度（像素）

    private Long ticketID = -1L;
    private String licenseNum;
    private String licenseColor = "蓝";
    private String vehicleType = "小型客车";
    private String vehicleColor = "黑";

    private String userID;
    private String policeName;
    private String policeCity;
    private String policeDept;

    private TextView policeNameTextView;
    private TextView policeUserIDTextView;
    private TextView policeDeptTextView;

    private String currentTime;
    private String currentImageFilePath1;
    private String currentImageFilePath2;
    private String currentImageFilePath3;
    private String currentMapFilePath;
    private int licenseCorrect = -1;
    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hour = -1;
    private int minute = -1;

    private File currentImageFile1 = null;
    private File currentImageFile2 = null;
    private File currentImageFile3 = null;
    private Bitmap imageBitmap = null;
    //    private byte[] imageBytes;
    private ByteArrayOutputStream imageOS;
    private ImageView profileImageView;

    private Button takePictureButton;

    private String svmPath = "svm.xml";
    private String annPath = "ann.xml";
    private String ann_chinesePath = "ann_chinese.xml";
    private String mappingPath = "province_mapping";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.d(LOG_TAG, "OpenCV loaded successfully");
                    System.loadLibrary("platerecognizer");
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
            Bitmap bitmap = BitmapUtil.scaleBitmap(imageBitmap, TRANS_IMAGE_W, TRANS_IMAGE_H);
            bitmap = BitmapUtil.cropBitmapCenter(bitmap, SCREEN_WIDTH, SCREEN_HEIGHT, RATIO_ENLARGE, VERTICAL_RATIO_W, VERTICAL_RATIO_H, HORIZONTAL_RATIO_W, HORIZONTAL_RATIO_H);
            Mat m = new Mat();
            Utils.bitmapToMat(bitmap, m);
            return plateRecognition(m.getNativeObjAddr(), m.getNativeObjAddr());
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equalsIgnoreCase("0") && result.length() == 10) {
                licenseNum = result.substring(3);
                licenseColor = result.substring(0, 1);
                vehicleType = "小型客车"; // TODO: Add recognization for vehicle type and color
                vehicleColor = "黑";
                showLicenseCheckDialog();
            } else {
                showAlertandFront(getString(R.string.recognition_error));
            }
            backToHome();
        }
    }


    Button.OnClickListener takePictureClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
//            takePictureButton.setImageResource(R.drawable.camera_gray);
            takePictureButton.setEnabled(false);
            saveScreen();
            takeFrontPictureIntent();
        }
    };

    private SimpleDateFormat dateFormatf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener;
    MapView mMapView;
    BaiduMap mBaiduMap;
    // UI相关
    boolean isFirstLoc = true; // 是否首次定位

    private String address;
    private double longitude;
    private double latitude;
    private TextView addressLonLatTextView;

    // Database
    ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFile();
    }

    private void initFile() {
        File file = new File(String.format("/sdcard/" + FILE_INSDCARD_DIR + "/"));
        if (!file.exists()) {
            file.mkdirs();
        }
        CopyOneFile(annPath);
        CopyOneFile(svmPath);
        CopyOneFile(mappingPath);
        CopyOneFile(ann_chinesePath);
    }

    private void CopyOneFile(String path) {
        File file = new File(String.format("/sdcard/" + FILE_INSDCARD_DIR + "/" + path));
        try {
            copyFileFromAssetsToSDCard(path, "/sdcard/" + FILE_INSDCARD_DIR + "/" + path);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void copyFileFromAssetsToSDCard(String resname, String sdpath) throws Throwable {
        InputStream is = getResources().getAssets().open(resname);
        OutputStream os = new FileOutputStream(sdpath);
        byte data[] = new byte[1024];
        int len;
        while ((len = is.read(data)) > 0) {
            os.write(data, 0, len);
        }
        is.close();
        os.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acquire, container, false);
        mMapView = (MapView) view.findViewById(R.id.fragment_bmapView);
        initLocation();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        policeNameTextView = (TextView) view.findViewById(R.id.police_name_textview);
        policeUserIDTextView = (TextView) view.findViewById(R.id.police_user_id_textview);
        policeDeptTextView = (TextView) view.findViewById(R.id.police_dept_textview);
        takePictureButton = (Button) view.findViewById(R.id.take_pic_button);
        profileImageView = (ImageView) view.findViewById(R.id.profile_image_view);
//        profileImageView.setContentDescription("This is the profile!");
        addressLonLatTextView = (TextView) view.findViewById(R.id.addressLonLat);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        resolver = activity.getContentResolver();

        Intent intentFrom = activity.getIntent(); // Get the Intent that called for this Activity to open

        userID = intentFrom.getExtras().getString(POLICE_USER_ID); // Get the data that was sent
        getUserInfo(userID); // Get policeName policeDept, and policeCity
        policeNameTextView.setText(policeName);
        policeUserIDTextView.setText(userID);
        policeDeptTextView.setText(policeDept);

        if (!PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
                .getBoolean(PREF_INSTALLED_KEY, false)) {
            PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
                    .edit().putBoolean(PREF_INSTALLED_KEY, true).commit();
            FileUtil.copyAssetFolder(activity.getAssets(), RUNTIME_DATA_DIR_ASSET,
                    ANDROID_DATA_DIR + File.separatorChar + RUNTIME_DATA_DIR_ASSET);
        }

        takePictureButton.setOnClickListener(takePictureClickListener);
        profileImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentDesc = getString(R.string.toast_update_hint);
                int[] pos = new int[2];
                profileImageView.getLocationInWindow(pos);
                Toast t = Toast.makeText(getActivity(), contentDesc, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((contentDesc.length() / 2) * 12), pos[1] - 128);
                t.show();
            }
        });

        profileImageView.setOnLongClickListener(new ImageView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Start the Signup activity
                Intent updateProfileIntent = new Intent(getActivity(), UpdateProfileActivity.class);
                updateProfileIntent.putExtra(POLICE_USER_ID, userID);
                startActivityForResult(updateProfileIntent, REQUEST_UPDATE_PROFILE);
                return true;
            }
        });

        if (savedInstanceState != null) {
            ticketID = savedInstanceState.getLong(SAVED_INSTANCE_TICKET_ID);
            userID = savedInstanceState.getString(SAVED_INSTANCE_USER_ID);
            policeName = savedInstanceState.getString(SAVED_INSTANCE_POLICE_NAME);
            policeCity = savedInstanceState.getString(SAVED_INSTANCE_POLICE_CITY);
            policeDept = savedInstanceState.getString(SAVED_INSTANCE_POLICE_DEPT);
            licenseNum = savedInstanceState.getString(SAVED_INSTANCE_LICENSE_NUM);
            licenseColor = savedInstanceState.getString(SAVED_INSTANCE_LICENSE_COLOR);
            licenseCorrect = savedInstanceState.getInt(SAVED_INSTANCE_LICENSE_CORRECT, -1);
            vehicleType = savedInstanceState.getString(SAVED_INSTANCE_VEHICLE_TYPE);
            vehicleColor = savedInstanceState.getString(SAVED_INSTANCE_VEHICLE_COLOR);

            currentTime = savedInstanceState.getString(SAVED_INSTANCE_CURR_TIME);
            currentMapFilePath = savedInstanceState.getString(SAVED_INSTANCE_CURR_MAP_PATH);
            currentImageFilePath1 = savedInstanceState.getString(SAVED_INSTANCE_CURR_IMG1_PATH);
            currentImageFilePath2 = savedInstanceState.getString(SAVED_INSTANCE_CURR_IMG2_PATH);
            currentImageFilePath3 = savedInstanceState.getString(SAVED_INSTANCE_CURR_IMG3_PATH);

            address = savedInstanceState.getString(SAVED_INSTANCE_ADDRESS);
            longitude = savedInstanceState.getDouble(SAVED_INSTANCE_LONGITUDE);
            latitude = savedInstanceState.getDouble(SAVED_INSTANCE_LATITUDE);

            year = savedInstanceState.getInt(SAVED_INSTANCE_YEAR, -1);
            month = savedInstanceState.getInt(SAVED_INSTANCE_MONTH, -1);
            day = savedInstanceState.getInt(SAVED_INSTANCE_DAY, -1);
            hour = savedInstanceState.getInt(SAVED_INSTANCE_HOUR, -1);
            minute = savedInstanceState.getInt(SAVED_INSTANCE_MINUTE, -1);
        }

        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        SCREEN_WIDTH = metric.widthPixels;  // 屏幕宽度（像素）
        SCREEN_HEIGHT = metric.heightPixels;  // 屏幕高度（像素）

    }


    public void showPreview() {
        Intent goToPreviewActivityIntent = new Intent(getActivity(), PreviewActivity.class);
        Bundle params = new Bundle();
        params.putString(CURRENT_USER_ID, userID);
        params.putString(CURRENT_POLICE_NAME, policeName);
        params.putString(CURRENT_POLICE_CITY, policeCity);
        params.putString(CURRENT_POLICE_DEPT, policeDept);
        params.putLong(CURRENT_TICKET_ID, ticketID);
        params.putString(CURRENT_LICENSE_NUM, licenseNum);
        params.putString(CURRENT_LICENSE_COLOR, licenseColor);
        params.putInt(CURRENT_LICENSE_CORRECT, licenseCorrect);
        params.putString(CURRENT_VEHICLE_TYPE, vehicleType);
        params.putString(CURRENT_VEHICLE_COLOR, vehicleColor);
        params.putString(CURRENT_ADDRESS, address);
        params.putDouble(CURRENT_LONGITUDE, longitude);
        params.putDouble(CURRENT_LATITUDE, latitude);
        params.putString(CURRENT_MAP_FILE_PATH, currentMapFilePath);
        params.putString(CURRENT_IMG1_FILE_PATH, currentImageFilePath1);
        params.putString(CURRENT_IMG2_FILE_PATH, currentImageFilePath2);
        goToPreviewActivityIntent.putExtras(params);
        startActivityForResult(goToPreviewActivityIntent, REQUEST_PREVIEW_SHOW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FRONT_IMAGE_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (currentImageFilePath1 != null) {
                        imageBitmap = BitmapUtil.getScaledBitmap(currentImageFilePath1, TRANS_IMAGE_W, TRANS_IMAGE_H);
                        if (imageBitmap != null) {
                            new PlateRecognizeTask().execute();
                        } else {
                            showAlertandFront("imageBitmap is null!");
                        }
                    } else {
                        showAlertandFront("currentImageFilePath1 is null!");
                    }
                } else {
//                    showAlertandFront("resultCode is not OK!");
                    backToHome();
                }
                break;

            case REQUEST_BACK_IMAGE_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    showPreview();
                } else {
                    backToHome();
                }
                break;

            case REQUEST_PREVIEW_SHOW:
                if (resultCode == getActivity().RESULT_OK) {
                    vehicleColor = data.getStringExtra(BACK_VEHICLE_COLOR);
                    vehicleType = data.getStringExtra(BACK_VEHICLE_TYPE);
                    licenseColor = data.getStringExtra(BACK_LICENSE_COLOR);
//                    scanQRCode();
                } else {
                    backToHome();
                }
                break;

//            case REQUEST_QRCODE_SCAN:
//                if (resultCode == getActivity().RESULT_OK) {
//                    String scannedQRCode = data.getStringExtra(SCANNED_QR_CODE);
//                    if (scannedQRCode == null) {
//                        Log.d(LOG_TAG, "Cancelled scan");
//                        showAlertandQR(getString(R.string.scan_no_result));
//                        scanQRCode();
//                    } else {
//                        Log.d(LOG_TAG, "Scanned");
//                        ticketID = Long.parseLong(scannedQRCode);
//                        takeSidePictureIntent();
//                    }
//                } else {
//                    showPreview();
//                }
//                break;

            case REQUEST_SIDE_IMAGE_CAPTURE:
                Calendar now = Calendar.getInstance();
                currentTime = dateFormatf.format(now.getTime());
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1; // Note: zero based!
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                minute = now.get(Calendar.MINUTE);
                saveTicket();
                backToHome();
                break;

            case REQUEST_UPDATE_PROFILE:
                if (resultCode == getActivity().RESULT_OK) {
                    userID = data.getStringExtra(GOBACK_USER_ID);
                    getUserInfo(userID); // Get policeName policeDept, and policeCity
                    policeNameTextView.setText(policeName);
                    policeUserIDTextView.setText(userID);
                    policeDeptTextView.setText(policeDept);
                }
                break;
        }
    }

    private void backToHome() {
//            takePictureButton.setImageResource(R.drawable.camera);
        takePictureButton.setEnabled(true);
    }

    private void refreshTitlesFragment() {
        TitlesFragment titlesFragment = (TitlesFragment)
                getFragmentManager().findFragmentById(R.id.titles);
        if (titlesFragment == null) {
            titlesFragment = new TitlesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.titles, titlesFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else {
            titlesFragment.updateListView();
        }
        titlesFragment.clickOnListViewItem(0);
    }

    private void initLocation() {
        // 地图初始化
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        // 定位初始化
        myListener = new MyLocationListenner();
        mLocClient = new LocationClient(getActivity().getApplicationContext());

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true); // 可选，设置是否需要地址信息，默认不需要
//        option.setIsNeedLocationDescribe(true); // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.registerLocationListener(myListener);
        mLocClient.start();
//        mBaiduMap.showMapPoi(true);
    }

    public void saveScreen() {
        // 截图，在SnapshotReadyCallback中保存图片到 sd 卡
        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap snapshot) {
                currentMapFilePath = FileUtil.getStorageDir(getActivity()) + "/" + FileUtil.getFileName(MAP_FILE_PREFIX) + JPEG_FILE_SUFFIX;
                File file = new File(currentMapFilePath);
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (snapshot.compress(
                            Bitmap.CompressFormat.JPEG, COMPRESS_RATIO, out)) {
                        out.flush();
                        out.close();
                    }
//                    Toast.makeText(getActivity(), R.string.toast_map_saved + file.toString(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                takeFrontPictureIntent();
//                saveTicket();
            }
        });
//        Toast.makeText(getActivity(), R.string.toast_map_saving, Toast.LENGTH_SHORT).show();
    }

    public void getUserInfo(String theUserID) {
        String[] projection = new String[]{KEY_POLICE_NAME, KEY_POLICE_CITY, KEY_POLICE_DEPT};
        Cursor cursor = resolver.query(DBProvider.POLICE_URL, projection, KEY_USER_ID + "=?", new String[]{theUserID}, null);
        if (cursor.moveToFirst()) {
            policeName = cursor.getString(cursor.getColumnIndex(KEY_POLICE_NAME));
            policeCity = cursor.getString(cursor.getColumnIndex(KEY_POLICE_CITY));
            policeDept = cursor.getString(cursor.getColumnIndex(KEY_POLICE_DEPT));
        } else {
            Toast.makeText(getActivity(), "No police info stored in database!", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveTicket() {
        if (ticketID == -1L) {
            Toast.makeText(getActivity(), R.string.toast_no_ticket_id, Toast.LENGTH_SHORT).show();
        } else if (userID == null) {
            Toast.makeText(getActivity(), R.string.toast_no_user, Toast.LENGTH_SHORT).show();
        } else if (licenseNum == null) {
            Toast.makeText(getActivity(), R.string.toast_ticket_no_license, Toast.LENGTH_SHORT).show();
        } else if (licenseColor == null) {
            Toast.makeText(getActivity(), R.string.toast_ticket_no_license_color, Toast.LENGTH_SHORT).show();
        } else if (licenseCorrect == -1) {
            showLicenseCheckDialog();
        } else if (currentTime == null) {
            Toast.makeText(getActivity(), R.string.toast_ticket_no_time, Toast.LENGTH_SHORT).show();
        } else {
            // Insert the value into the Content Provider
            ContentValues values = new ContentValues();
            values.put(KEY_TICKET_ID, ticketID);
            values.put(KEY_USER_ID, userID);
            values.put(KEY_LICENSE_NUM, licenseNum);
            values.put(KEY_LICENSE_COLOR, licenseColor);
            values.put(KEY_LICENSE_CORRECT, licenseCorrect);
            values.put(KEY_DATETIME, currentTime);
            values.put(KEY_YEAR, year);
            values.put(KEY_MONTH, month);
            values.put(KEY_DAY, day);
            values.put(KEY_HOUR, hour);
            values.put(KEY_MINUTE, minute);
            if (vehicleType != null) {
                values.put(KEY_CAR_TYPE, vehicleType);
            }
            if (vehicleColor != null) {
                values.put(KEY_CAR_COLOR, vehicleColor);
            }
            if (address != null) {
                values.put(KEY_ADDRESS, address);
            }
            String lon = Double.toString(longitude);
            if (lon != null) {
                values.put(KEY_LONGITUDE, lon);
            }
            String lat = Double.toString(latitude);
            if (lat != null) {
                values.put(KEY_LATITUDE, lat);
            }
            if (currentMapFilePath != null) {
                values.put(KEY_MAP_URI, currentMapFilePath);
            }
            if (currentImageFilePath1 != null) {
                values.put(KEY_IMG1_URI, currentImageFilePath1);
            }
            if (currentImageFilePath2 != null) {
                values.put(KEY_IMG2_URI, currentImageFilePath2);
            }
            if (currentImageFilePath3 != null) {
                values.put(KEY_IMG3_URI, currentImageFilePath3);
            }
            resolver.insert(DBProvider.TICKET_URL, values);
            Toast.makeText(getActivity(), R.string.toast_ticket_saved, Toast.LENGTH_SHORT).show();
        }
        refreshTitlesFragment();
    }

    /* 定位SDK监听函数 */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            mBaiduMap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

            // Receive Location
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeOffLineLocation) {// GPS或网络定位结果
                String locDesc = location.getLocationDescribe();
                address = location.getAddrStr();
                if (locDesc != null) {
                    address = address + " (" + location.getLocationDescribe() + ")";
                }
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                address = "服务端定位失败,请告知我们";
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                address = "定位失败，请检查网络是否通畅";
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                address = "无定位依据，手机可能处于飞行模式，试试重启手机";
            }
            addressLonLatTextView.setText(getString(R.string.ticket_address_header) + address + "\n经纬度: (" + longitude + ", " + latitude + ")");
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG_TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, getActivity(), mLoaderCallback);
        } else {
            Log.d(LOG_TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(SAVED_INSTANCE_TICKET_ID, ticketID);
        outState.putString(SAVED_INSTANCE_USER_ID, userID);
        outState.putString(SAVED_INSTANCE_POLICE_NAME, policeName);
        outState.putString(SAVED_INSTANCE_POLICE_CITY, policeCity);
        outState.putString(SAVED_INSTANCE_POLICE_DEPT, policeDept);
        outState.putString(SAVED_INSTANCE_LICENSE_NUM, licenseNum);
        outState.putString(SAVED_INSTANCE_LICENSE_COLOR, licenseColor);
        outState.putInt(SAVED_INSTANCE_LICENSE_CORRECT, licenseCorrect);
        outState.putString(SAVED_INSTANCE_VEHICLE_TYPE, vehicleType);
        outState.putString(SAVED_INSTANCE_VEHICLE_COLOR, vehicleColor);

        outState.putString(SAVED_INSTANCE_CURR_TIME, currentTime);
        outState.putString(SAVED_INSTANCE_CURR_MAP_PATH, currentMapFilePath);
        outState.putString(SAVED_INSTANCE_CURR_IMG1_PATH, currentImageFilePath1);
        outState.putString(SAVED_INSTANCE_CURR_IMG2_PATH, currentImageFilePath2);
        outState.putString(SAVED_INSTANCE_CURR_IMG3_PATH, currentImageFilePath3);

        outState.putString(SAVED_INSTANCE_ADDRESS, address);
        outState.putDouble(SAVED_INSTANCE_LONGITUDE, longitude);
        outState.putDouble(SAVED_INSTANCE_LATITUDE, latitude);

        outState.putInt(SAVED_INSTANCE_YEAR, year);
        outState.putInt(SAVED_INSTANCE_MONTH, month);
        outState.putInt(SAVED_INSTANCE_DAY, day);
        outState.putInt(SAVED_INSTANCE_HOUR, hour);
        outState.putInt(SAVED_INSTANCE_MINUTE, minute);

        super.onSaveInstanceState(outState);
    }

//    public void scanQRCode() {
//        Intent goToDecoderActivityIntent = new Intent(getActivity(), DecoderActivity.class);
//        startActivityForResult(goToDecoderActivityIntent, REQUEST_QRCODE_SCAN);
//    }

    private void takeFrontPictureIntent() {
        try {
            currentImageFile1 = FileUtil.createImageFile(getActivity(), IMG1_FILE_PREFIX, JPEG_FILE_SUFFIX);
            currentImageFilePath1 = currentImageFile1.getAbsolutePath();
            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    // .skipConfirm()
                    .facing(Facing.BACK)
                    .to(currentImageFile1)
                    // .debug()
                    .flashModes(FLASH_MODES)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .confirmationQuality(0.3f)
                    .hintText(getString(R.string.camera_hint_front))
                    .showFocus(1)
                    // .skipOrientationNormalization()
                    .build();
            startActivityForResult(intent, REQUEST_FRONT_IMAGE_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            currentImageFile1 = null;
            currentImageFilePath1 = null;
            showAlertandFront(getString(R.string.img_file_create_error));
        }
    }

    private void takeBackPictureIntent() {
        try {
            currentImageFile2 = FileUtil.createImageFile(getActivity(), IMG2_FILE_PREFIX, JPEG_FILE_SUFFIX);
            currentImageFilePath2 = currentImageFile2.getAbsolutePath();
            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    .facing(Facing.BACK)
                    .to(currentImageFile2)
                    .flashModes(FLASH_MODES)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .confirmationQuality(0.3f)
                    .hintText(getString(R.string.camera_hint_back))
                    .build();
            startActivityForResult(intent, REQUEST_BACK_IMAGE_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            currentImageFile2 = null;
            currentImageFilePath2 = null;
            showAlertandBack(getString(R.string.img_file_create_error));
        }
    }

    private void takeSidePictureIntent() {
        try {
            currentImageFile3 = FileUtil.createImageFile(getActivity(), IMG3_FILE_PREFIX, JPEG_FILE_SUFFIX);
            currentImageFilePath3 = currentImageFile3.getAbsolutePath();
            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    .facing(Facing.BACK)
                    .to(currentImageFile3)
                    .flashModes(FLASH_MODES)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .confirmationQuality(0.3f)
                    .hintText(getString(R.string.camera_hint_side))
                    .build();
            startActivityForResult(intent, REQUEST_SIDE_IMAGE_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            currentImageFile3 = null;
            currentImageFilePath3 = null;
            showAlertandSide(getString(R.string.img_file_create_error));
        }
    }

    private void showAlertDialog(String alertString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(alertString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertandFront(String alertString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(alertString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takeFrontPictureIntent();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertandBack(String alertString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(alertString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takeBackPictureIntent();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertandQR(String alertString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(alertString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                scanQRCode();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertandSide(String alertString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(alertString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takeSidePictureIntent();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLicenseCheckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("\"" + licenseNum.substring(0, 2) + "\u2022" + licenseNum.substring(2) + "\" " + getString(R.string.alert_dialog_license_check));
        builder.setPositiveButton(R.string.alert_dialog_license_check_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                licenseCorrect = 1;
                takeBackPictureIntent();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_license_check_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                licenseCorrect = 0;
                takeBackPictureIntent();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
