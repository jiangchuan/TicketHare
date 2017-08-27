package io.chizi.tickethare.acquire;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.protobuf.ByteString;

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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PublicFunction;
import io.chizi.ticket.MasterOrder;
import io.chizi.ticket.RecordReply;
import io.chizi.ticket.SlaveLoc;
import io.chizi.ticket.StatsReply;
import io.chizi.ticket.TicketDetails;
import io.chizi.ticket.TicketGrpc;
import io.chizi.ticket.TicketRange;
import io.chizi.ticket.TicketRangeSid;
import io.chizi.ticket.TicketStats;
import io.chizi.tickethare.R;
import io.chizi.tickethare.database.TitlesFragment;
import io.chizi.tickethare.util.BitmapUtil;
import io.chizi.tickethare.util.ColorSpinnerAdapter;
import io.chizi.tickethare.util.FileUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.chizi.tickethare.acquire.PlateRecognizer.plateRecognition;
import static io.chizi.tickethare.database.DBProvider.KEY_ADDRESS;
import static io.chizi.tickethare.database.DBProvider.KEY_CAR_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_CAR_TYPE;
import static io.chizi.tickethare.database.DBProvider.KEY_DATETIME;
import static io.chizi.tickethare.database.DBProvider.KEY_DAY;
import static io.chizi.tickethare.database.DBProvider.KEY_HOUR;
import static io.chizi.tickethare.database.DBProvider.KEY_FAR_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_CLOSE_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_IS_UPLOADED;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_PORTRAIT_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_LATITUDE;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_NUM;
import static io.chizi.tickethare.database.DBProvider.KEY_LONGITUDE;
import static io.chizi.tickethare.database.DBProvider.KEY_MAP_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_MINUTE;
import static io.chizi.tickethare.database.DBProvider.KEY_MONTH;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_CITY;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_DEPT;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_NAME;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_RANGE_END;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_RANGE_START;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_WEEK;
import static io.chizi.tickethare.database.DBProvider.KEY_YEAR;
import static io.chizi.tickethare.database.DBProvider.POLICE_URL;
import static io.chizi.tickethare.database.DBProvider.RANGE_URL;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.ANDROID_DATA_DIR;
import static io.chizi.tickethare.util.AppConstants.BACK_LICENSE_COLOR;
import static io.chizi.tickethare.util.AppConstants.BACK_LICENSE_NUM;
import static io.chizi.tickethare.util.AppConstants.BACK_VEHICLE_COLOR;
import static io.chizi.tickethare.util.AppConstants.BACK_VEHICLE_TYPE;
import static io.chizi.tickethare.util.AppConstants.CHINA_EAST;
import static io.chizi.tickethare.util.AppConstants.CHINA_NORTH;
import static io.chizi.tickethare.util.AppConstants.CHINA_SOUTH;
import static io.chizi.tickethare.util.AppConstants.CHINA_WEST;
import static io.chizi.tickethare.util.AppConstants.COMPRESS_RATIO;
import static io.chizi.tickethare.util.AppConstants.CURRENT_ADDRESS;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_PORTRAIT_PATH;
import static io.chizi.tickethare.util.AppConstants.FAR_IMG_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.CLOSE_IMG_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LATITUDE;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LICENSE_COLOR;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LICENSE_NUM;
import static io.chizi.tickethare.util.AppConstants.CURRENT_LONGITUDE;
import static io.chizi.tickethare.util.AppConstants.MAP_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_CITY;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_DEPT;
import static io.chizi.tickethare.util.AppConstants.CURRENT_POLICE_NAME;
import static io.chizi.tickethare.util.AppConstants.CURRENT_TICKET_ID;
import static io.chizi.tickethare.util.AppConstants.CURRENT_USER_ID;
import static io.chizi.tickethare.util.AppConstants.CURRENT_VEHICLE_COLOR;
import static io.chizi.tickethare.util.AppConstants.CURRENT_VEHICLE_TYPE;
import static io.chizi.tickethare.util.AppConstants.FILE_INSDCARD_DIR;
import static io.chizi.tickethare.util.AppConstants.GOBACK_USER_ID;
import static io.chizi.tickethare.util.AppConstants.CLOSE_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.FAR_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.HOST_IP;
import static io.chizi.tickethare.util.AppConstants.MINUTE_IN_MS;
import static io.chizi.tickethare.util.AppConstants.PORT;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_IS_UPLOADED;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_POLICE_PORTRAIT_PATH;
import static io.chizi.tickethare.util.AppConstants.SECOND_IN_MS;
import static io.chizi.tickethare.util.AppConstants.TICKET_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.JPEG_FILE_SUFFIX;
import static io.chizi.tickethare.util.AppConstants.MAP_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PREF_INSTALLED_KEY;
import static io.chizi.tickethare.util.AppConstants.REQUEST_FAR_IMG_CAPTURE;
import static io.chizi.tickethare.util.AppConstants.REQUEST_CLOSE_IMG_CAPTURE;
import static io.chizi.tickethare.util.AppConstants.REQUEST_PREVIEW_SHOW;
import static io.chizi.tickethare.util.AppConstants.REQUEST_TICKET_IMG_CAPTURE;
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
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LICENSE_NUM;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_LONGITUDE;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_MINUTE;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_MONTH;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_WEEK;
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
    private Long ticketIDStart = -1L;
    private Long ticketIDEnd = -1L;
    private String licenseNum;
    private String licenseColor = "蓝";
    private String vehicleType = "小型客车";
    private String vehicleColor = "黑";

    private String userID;
    private String policeName;
    private String policeCity;
    private String policeDept;
    private String policePortraitPath;

    private TextView policeNameTextView;
    private TextView policeUserIDTextView;
    private TextView policeDeptTextView;

    private String currentTime;
    private String closeImgFilePath;
    private String farImgFilePath;
    private String ticketImgFilePath;
    private String mapFilePath;
    private int isUploaded = -1;
    private int year = -1;
    private int month = -1;
    private int week = -1;
    private int day = -1;
    private int hour = -1;
    private int minute = -1;

    private int numSavedTicket;
    private int numUploadedTicket;

    private File closeImgFile = null;
    private File farImgFile = null;
    private File ticketImgFile = null;
    private Bitmap imageBitmap = null;
    //    private byte[] imageBytes;
    private ByteArrayOutputStream imageOS;
    private ImageView profileImageView;

    private Button takePictureButton;

    private String svmPath = "svm.xml";
    private String annPath = "ann.xml";
    private String ann_chinesePath = "ann_chinese.xml";
    private String mappingPath = "province_mapping";

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
    private ContentResolver resolver; // Provides access to other applications Content Providers

    private ProgressDialog progressDialog;

    private ManagedChannel mChannel;

    private Boolean receivedLoc = false;


    private static final long LOC_TIME_INTERVAL = 5 * SECOND_IN_MS;
    private static final long ANCHOR_TIME_INTERVAL = 30 * MINUTE_IN_MS;
    //    private static final long ANCHOR_TIME_INTERVAL = 1 * MINUTE_IN_MS;
    private static final long TICKET_STATS_TIME_INTERVAL = 10 * MINUTE_IN_MS;

    private String masterOrder;

    // HM printer starts here
    private BluetoothAdapter mBluetoothAdapter;
    private PublicFunction PFun = null;

    private Spinner spnPrinterList = null;

    private ArrayAdapter arrPrinterList;
    private static HPRTPrinterHelper HPRTPrinter = new HPRTPrinterHelper();
    private String PrinterName = "";

    private Handler handler;
    private ProgressDialog dialog;
    public static String paper = "0";

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
        addressLonLatTextView = (TextView) view.findViewById(R.id.addressLonLat);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            ticketID = savedInstanceState.getLong(SAVED_INSTANCE_TICKET_ID);
            userID = savedInstanceState.getString(SAVED_INSTANCE_USER_ID);
            policeName = savedInstanceState.getString(SAVED_INSTANCE_POLICE_NAME);
            policeCity = savedInstanceState.getString(SAVED_INSTANCE_POLICE_CITY);
            policeDept = savedInstanceState.getString(SAVED_INSTANCE_POLICE_DEPT);
            policePortraitPath = savedInstanceState.getString(SAVED_INSTANCE_POLICE_PORTRAIT_PATH);
            licenseNum = savedInstanceState.getString(SAVED_INSTANCE_LICENSE_NUM);
            licenseColor = savedInstanceState.getString(SAVED_INSTANCE_LICENSE_COLOR);
            isUploaded = savedInstanceState.getInt(SAVED_INSTANCE_IS_UPLOADED, -1);
            vehicleType = savedInstanceState.getString(SAVED_INSTANCE_VEHICLE_TYPE);
            vehicleColor = savedInstanceState.getString(SAVED_INSTANCE_VEHICLE_COLOR);

            currentTime = savedInstanceState.getString(SAVED_INSTANCE_CURR_TIME);
            mapFilePath = savedInstanceState.getString(SAVED_INSTANCE_CURR_MAP_PATH);
            closeImgFilePath = savedInstanceState.getString(SAVED_INSTANCE_CURR_IMG1_PATH);
            farImgFilePath = savedInstanceState.getString(SAVED_INSTANCE_CURR_IMG2_PATH);
            ticketImgFilePath = savedInstanceState.getString(SAVED_INSTANCE_CURR_IMG3_PATH);

            address = savedInstanceState.getString(SAVED_INSTANCE_ADDRESS);
            longitude = savedInstanceState.getDouble(SAVED_INSTANCE_LONGITUDE);
            latitude = savedInstanceState.getDouble(SAVED_INSTANCE_LATITUDE);

            year = savedInstanceState.getInt(SAVED_INSTANCE_YEAR, -1);
            month = savedInstanceState.getInt(SAVED_INSTANCE_MONTH, -1);
            week = savedInstanceState.getInt(SAVED_INSTANCE_WEEK, -1);
            day = savedInstanceState.getInt(SAVED_INSTANCE_DAY, -1);
            hour = savedInstanceState.getInt(SAVED_INSTANCE_HOUR, -1);
            minute = savedInstanceState.getInt(SAVED_INSTANCE_MINUTE, -1);
        }

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

        takePictureButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFields();
                saveScreen();
                takeFarPictureIntent();
            }
        });
//        profileImageView.setOnClickListener(new ImageView.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String contentDesc = getString(R.string.toast_update_hint);
//                int[] pos = new int[2];
//                profileImageView.getLocationInWindow(pos);
//                Toast t = Toast.makeText(getActivity(), contentDesc, Toast.LENGTH_SHORT);
//                t.setGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((contentDesc.length() / 2) * 12), pos[1] - 128);
//                t.show();
//            }
//        });
//        profileImageView.setOnLongClickListener(new ImageView.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // Start the Signup activity
//                Intent updateProfileIntent = new Intent(getActivity(), ChangePasswordActivity.class);
//                updateProfileIntent.putExtra(POLICE_USER_ID, userID);
//                startActivityForResult(updateProfileIntent, REQUEST_UPDATE_PROFILE);
//                return true;
//            }
//        });


        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        SCREEN_WIDTH = metric.widthPixels;  // 屏幕宽度(像素)
        SCREEN_HEIGHT = metric.heightPixels;  // 屏幕高度(像素)

        ViewTreeObserver profileImgVTO = profileImageView.getViewTreeObserver();
        profileImgVTO.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                profileImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (policePortraitPath != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(policePortraitPath, TRANS_IMAGE_W, TRANS_IMAGE_H);
                    if (bitmap != null) {
                        int finalWidth = profileImageView.getMeasuredWidth();
                        int finalHeight = profileImageView.getMeasuredHeight();
                        int finalSize = finalWidth > finalHeight ? finalWidth : finalHeight;
                        BitmapUtil.setBitmapToImageView(profileImageView, finalSize, finalSize, bitmap);
                    }
                }
                return true;
            }
        });


        try {
            PFun = new PublicFunction(getActivity());
            InitSetting();
            InitCombox();
            this.spnPrinterList.setOnItemSelectedListener(new OnItemSelectedPrinter());
            EnableBluetooth();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        Toast.makeText(getActivity(), "succeed", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    } else {
                        Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                }
            };
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("AcquireFragment --> onCreate ")).append(e.getMessage()).toString());
        }

        mChannel = ManagedChannelBuilder.forAddress(HOST_IP, PORT)
                .usePlaintext(true)
                .build();

        final Handler locHandler = new Handler();
        Timer locTimer = new Timer();
        TimerTask locTask = new TimerTask() {
            @Override
            public void run() {
                locHandler.post(new Runnable() {
                    public void run() {
                        if (receivedLoc) {
                            new SlaveLocSubmitGrpcTask().execute();
                        }
                    }
                });
            }
        };
        locTimer.schedule(locTask, 0, LOC_TIME_INTERVAL); //it executes this every 5s
    }

    private boolean lonLatInChina(double theLon, double theLat) {
        if (theLon > CHINA_WEST && theLon < CHINA_EAST && theLat > CHINA_SOUTH && theLat < CHINA_NORTH) {
            return true;
        }
        return false;
    }

    private void showLiscenseNumPrompt() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt_license_num, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView promptTitle = (TextView) promptsView
                .findViewById(R.id.textview_license_num_prompt);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.edittext_license_num_prompt);
        if (licenseNum == null || licenseNum.isEmpty()) {
            promptTitle.setText(getResources().getString(R.string.license_num_input_prompt));
        } else {
            promptTitle.setText(getResources().getString(R.string.license_num_correct_prompt));
            userInput.setText(licenseNum);
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                licenseNum = userInput.getText().toString();
//                                if (licenseNum.isEmpty()) {
//                                    Toast.makeText(getActivity(), R.string.toast_wrong_userid, Toast.LENGTH_LONG).show();
//                                    userInput.setError(getString(R.string.request_wrong_userid));
//                                } else {
//                                    userInput.setError(null);
//                                    showLiscenseColorPrompt();
//                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
        alertDialog.show(); // show it

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licenseNum = userInput.getText().toString();
                if (licenseNum.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.toast_wrong_license_num, Toast.LENGTH_LONG).show();
                    userInput.setError(getString(R.string.request_wrong_license_num));
                } else {
                    userInput.setError(null);
                    alertDialog.dismiss();
                    showLiscenseColorPrompt();
                }
            }
        });

    }

    private void showLiscenseColorPrompt() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt_license_color, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final RadioGroup licenseColorRadioGroup = (RadioGroup) promptsView
                .findViewById(R.id.radiogroup_license_color);
        final RadioButton yellowRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_yellow);
        final RadioButton blueRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_blue);
        final RadioButton blackRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_black);
        final RadioButton otherColorRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_other_license_color);
        blueRadioButton.setChecked(true);

        licenseColorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (yellowRadioButton.isChecked()) {
                    licenseColor = getString(R.string.radiobutton_yellow).substring(0, 1);
                }
                if (blueRadioButton.isChecked()) {
                    licenseColor = getString(R.string.radiobutton_blue).substring(0, 1);
                }
                if (blackRadioButton.isChecked()) {
                    licenseColor = getString(R.string.radiobutton_black).substring(0, 1);
                }
                if (otherColorRadioButton.isChecked()) {
                    licenseColor = getString(R.string.radiobutton_other_license_color);
                }
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showVehicleTypePrompt();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
        alertDialog.show(); // show it
    }

    private void showVehicleTypePrompt() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt_vehicle_type, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final RadioGroup vehicleTypeRadioGroup1 = (RadioGroup) promptsView
                .findViewById(R.id.radiogroup_vehicle_type1);
        vehicleTypeRadioGroup1.clearCheck();
        final RadioGroup vehicleTypeRadioGroup2 = (RadioGroup) promptsView
                .findViewById(R.id.radiogroup_vehicle_type2);
        vehicleTypeRadioGroup2.clearCheck();

        final RadioButton bigBusRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_big_bus);
        final RadioButton smallBusRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_small_bus);
        final RadioButton bigTruckRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_big_truck);
        final RadioButton smallTruckRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_small_truck);
        final RadioButton motorCycleRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_motorcycle);
        final RadioButton otherCarTypeRadioButton = (RadioButton) promptsView
                .findViewById(R.id.radiobutton_other_car_type);
        smallBusRadioButton.setChecked(true);

        vehicleTypeRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (bigBusRadioButton.isChecked()) {
                    vehicleTypeRadioGroup2.clearCheck();
                    vehicleType = getString(R.string.radiobutton_big_bus);
                }
                if (smallBusRadioButton.isChecked()) {
                    vehicleTypeRadioGroup2.clearCheck();
                    vehicleType = getString(R.string.radiobutton_small_bus);
                }
                if (bigTruckRadioButton.isChecked()) {
                    vehicleTypeRadioGroup2.clearCheck();
                    vehicleType = getString(R.string.radiobutton_big_truck);
                }
            }
        });

        vehicleTypeRadioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (smallTruckRadioButton.isChecked()) {
                    vehicleTypeRadioGroup1.clearCheck();
                    vehicleType = getString(R.string.radiobutton_small_truck);
                }
                if (motorCycleRadioButton.isChecked()) {
                    vehicleTypeRadioGroup1.clearCheck();
                    vehicleType = getString(R.string.radiobutton_motorcycle);
                }
                if (otherCarTypeRadioButton.isChecked()) {
                    vehicleTypeRadioGroup1.clearCheck();
                    vehicleType = getString(R.string.radiobutton_other_car_type);
                }
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showVehicleColorPrompt();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
        alertDialog.show(); // show it
    }

    private void showVehicleColorPrompt() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt_vehicle_color, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final Spinner vehicleColorSpinner = (Spinner) promptsView
                .findViewById(R.id.car_color);
        String[] vehicleColorNames = getResources().getStringArray(R.array.car_color_arrays);
        int[] vehicleColorBackgroundInts = getResources().getIntArray(R.array.vehicle_color_int_arrays);
        int[] vehicleColorTextInts = getResources().getIntArray(R.array.vehicle_text_color_int_arrays);
        ColorSpinnerAdapter spinnerArrayAdapter = new ColorSpinnerAdapter(
                getActivity(), vehicleColorNames, vehicleColorTextInts, vehicleColorBackgroundInts);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        vehicleColorSpinner.setAdapter(spinnerArrayAdapter);
        vehicleColorSpinner.setSelection(0);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                vehicleColor = vehicleColorSpinner.getSelectedItem().toString();
                                showPreview();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
        alertDialog.show(); // show it
    }

    private void connectToBlueTooth() {
        if (HPRTPrinter != null) {
            HPRTPrinterHelper.PortClose();
        }
        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
        startActivityForResult(serverIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);
        return;
    }


    private void printTicket() {
        try {
            HPRTPrinterHelper.printAreaSize("0", "200", "200", "700", "1");
//            String[] receiptLines = getResources().getStringArray(R.array.activity_main_sample_2inch_receipt);
            int receiptLen = 16;
            String[] receiptLines = new String[receiptLen];
            receiptLines[0] = "成都市道路停车记录告知单";
            receiptLines[1] = "编号: " + ticketID;
            receiptLines[2] = "车辆牌号: " + licenseNum + "            车身颜色: " + vehicleColor;
            receiptLines[3] = "车辆类型: " + vehicleType;
            receiptLines[4] = "号牌颜色: " + licenseColor;
            receiptLines[5] = "停车时间: " + year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分";
            receiptLines[6] = "停车地点: " + address;
            receiptLines[7] = "该机动车在上述时间、地点未在道路停车泊位或停";
            receiptLines[8] = "车场内停放，现已对现场道路交通情况进行了图像记录，";
            receiptLines[9] = "并报告" + policeDept + "审核认定是否违法停放。";
            receiptLines[10] = "你可在七个工作日后登陆http://www.cdjg.gov.cn查询，";
            receiptLines[11] = "或在收到公安机关交通管理部门通知后前往接受处理。";
            receiptLines[12] = "交通协管员: " + policeName;
            receiptLines[13] = year + "年" + month + "月" + day + "日";
            receiptLines[14] = "----------------------------------------";
            receiptLines[15] = "备注: ";

            HPRTPrinterHelper.LanguageEncode = "GBK";

            HPRTPrinterHelper.Align(HPRTPrinterHelper.CENTER);
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "0", receiptLines[0]);
            HPRTPrinterHelper.Align(HPRTPrinterHelper.RIGHT);
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + 30, receiptLines[1]);

            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
            for (int i = 2; i < 7; i++) {
                HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "0" + (i * 30), receiptLines[i]);
            }

            int iLine = 7;
            HPRTPrinterHelper.Align(HPRTPrinterHelper.RIGHT);
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (iLine * 30), receiptLines[iLine]);

            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
            for (int i = 8; i < 13; i++) {
                HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (i * 30), receiptLines[i]);
            }

            iLine = 13;
            HPRTPrinterHelper.Align(HPRTPrinterHelper.RIGHT);
            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (iLine * 30), receiptLines[iLine]);

            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
            for (int i = receiptLen - 2; i < receiptLen; i++) {
                HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (i * 30), receiptLines[i]);
            }

            if ("1".equals(this.paper)) {
                HPRTPrinterHelper.Form();
            }
            HPRTPrinterHelper.Print();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    private void InitSetting() {
        String SettingValue = PFun.ReadSharedPreferencesData("Codepage");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Codepage", "0,PC437(USA:Standard Europe)");

        SettingValue = PFun.ReadSharedPreferencesData("Cut");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Cut", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Cashdrawer");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Cashdrawer", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Buzzer");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Buzzer", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Feeds");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Feeds", "0");
        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            this.paper = paper;
        }
    }

    //add printer list
    private void InitCombox() {
        try {
            arrPrinterList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
            String strSDKType = getResources().getString(R.string.sdk_type);
            if (strSDKType.equals("all"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_cpcl, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("hprt"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_hprt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mkt"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_mkt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mprint"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_mprint, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("sycrown"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_sycrown, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mgpos"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_mgpos, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("ds"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_ds, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("cst"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_cst, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("other"))
                arrPrinterList = ArrayAdapter.createFromResource(getActivity(), R.array.printer_list_other, android.R.layout.simple_spinner_item);
            arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            PrinterName = arrPrinterList.getItem(0).toString();
            spnPrinterList.setAdapter(arrPrinterList);
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> InitCombox ")).append(e.getMessage()).toString());
        }
    }

    private class OnItemSelectedPrinter implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PrinterName = arrPrinterList.getItem(arg2).toString();
            HPRTPrinter = new HPRTPrinterHelper(getActivity(), PrinterName);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } else {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
        }
        return bRet;
    }


    public void showPreview() {
        Intent goToPreviewActivityIntent = new Intent(getActivity(), PreviewActivity.class);
        Bundle params = new Bundle();
        params.putString(CURRENT_USER_ID, userID);
        params.putString(CURRENT_POLICE_NAME, policeName);
        params.putString(CURRENT_POLICE_CITY, policeCity);
        params.putString(CURRENT_POLICE_DEPT, policeDept);
        params.putString(CURRENT_POLICE_PORTRAIT_PATH, policePortraitPath);
        params.putLong(CURRENT_TICKET_ID, ticketID);
        params.putString(CURRENT_LICENSE_NUM, licenseNum);
        params.putString(CURRENT_LICENSE_COLOR, licenseColor);
        params.putString(CURRENT_VEHICLE_TYPE, vehicleType);
        params.putString(CURRENT_VEHICLE_COLOR, vehicleColor);
        params.putString(CURRENT_ADDRESS, address);
        params.putDouble(CURRENT_LONGITUDE, longitude);
        params.putDouble(CURRENT_LATITUDE, latitude);
        params.putString(MAP_FILE_PATH, mapFilePath);
        params.putString(FAR_IMG_FILE_PATH, farImgFilePath);
        params.putString(CLOSE_IMG_FILE_PATH, closeImgFilePath);
        goToPreviewActivityIntent.putExtras(params);
        startActivityForResult(goToPreviewActivityIntent, REQUEST_PREVIEW_SHOW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FAR_IMG_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (farImgFilePath != null) {
                        takeClosePictureIntent();
                    } else {
                        showAlertandRepeat("farImgFilePath is null!", REQUEST_FAR_IMG_CAPTURE);
                    }
                }
                break;

            case REQUEST_CLOSE_IMG_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (closeImgFilePath != null) {
                        imageBitmap = BitmapUtil.getScaledBitmap(closeImgFilePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
                        if (imageBitmap != null) {
                            new PlateRecognizeTask().execute();
                        } else {
                            showAlertandRepeat("imageBitmap is null!", REQUEST_CLOSE_IMG_CAPTURE);
                        }
                    } else {
                        showAlertandRepeat("closeImgFilePath is null!", REQUEST_CLOSE_IMG_CAPTURE);
                    }
                }
                break;

            case REQUEST_PREVIEW_SHOW:
                if (resultCode == getActivity().RESULT_OK) {
                    licenseNum = data.getStringExtra(BACK_LICENSE_NUM);
                    vehicleColor = data.getStringExtra(BACK_VEHICLE_COLOR);
                    vehicleType = data.getStringExtra(BACK_VEHICLE_TYPE);
                    licenseColor = data.getStringExtra(BACK_LICENSE_COLOR);
                    getTicketID();
                    getCurrentTime();
                    connectToBlueTooth();
                }
                break;

            case HPRTPrinterHelper.ACTIVITY_CONNECT_BT:
                String strIsConnected = data.getExtras().getString("is_connected");
                if (strIsConnected.equals("NO")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.activity_main_scan_error), Toast.LENGTH_LONG).show();
                } else {
                    printTicket();
                    Toast.makeText(getActivity(), getResources().getString(R.string.activity_main_connected), Toast.LENGTH_LONG).show();
                    showPrintCheckDialog();
                }
                break;


            case REQUEST_TICKET_IMG_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (ticketImgFilePath != null) {
                        showUploadDialog();
                        backToHome();
                    } else {
                        showAlertandRepeat("ticketImgFilePath is null!", REQUEST_TICKET_IMG_CAPTURE);
                    }
                }
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

    private void getCurrentTime() {
        Calendar now = Calendar.getInstance();
        currentTime = dateFormatf.format(now.getTime());
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        week = now.get(Calendar.WEEK_OF_YEAR);
        day = now.get(Calendar.DAY_OF_MONTH);
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
    }

    private void getTicketID() {
        String[] projection = new String[]{KEY_TICKET_RANGE_START, KEY_TICKET_RANGE_END};
        Cursor cursor = resolver.query(RANGE_URL, projection, KEY_USER_ID + "=?", new String[]{userID}, null);
        if (cursor.moveToFirst()) {
            ticketIDStart = cursor.getLong(cursor.getColumnIndex(KEY_TICKET_RANGE_START));
            ticketIDEnd = cursor.getLong(cursor.getColumnIndex(KEY_TICKET_RANGE_END));

            if (ticketIDStart < 0 || ticketIDEnd < 0 || ticketIDStart > ticketIDEnd) { // No ticket range or out of range
                new TicketRangeGrpcTask().execute();
            } else {
                updateTicketIDandRange();
            }
        } else {
            new TicketRangeGrpcTask().execute();
        }
    }

    private void updateTicketIDandRange() {
        ticketID = ticketIDStart;
        ticketIDStart++;

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userID);
        values.put(KEY_TICKET_RANGE_START, ticketIDStart);
        values.put(KEY_TICKET_RANGE_END, ticketIDEnd);
        resolver.insert(RANGE_URL, values);
    }

    private void initFields() {
        currentTime = null;
        ticketID = -1L;
        licenseNum = null;
        licenseColor = "蓝";
        isUploaded = -1;
        vehicleType = "小型客车";
        vehicleColor = "黑";
    }

    private void backToHome() {
        takePictureButton.setEnabled(true);
    }

    private class TicketRangeGrpcTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<String> doInBackground(Void... nothing) {
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                TicketRangeSid request = TicketRangeSid.newBuilder().setSid(userID).build();
                TicketRange reply = blockingStub.pullTicketRange(request);
                ticketIDStart = reply.getTicketIdStart();
                ticketIDEnd = reply.getTicketIdEnd();
                return resultList;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                resultList.add("Failed... : " + System.getProperty("line.separator") + sw);
                return resultList;
            }
        }

        @Override
        protected void onPostExecute(List<String> resultList) {
            updateTicketIDandRange();
        }
    }

    private class SlaveLocSubmitGrpcTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<String> doInBackground(Void... nothing) {
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                SlaveLoc request = newSlaveLoc(userID, longitude, latitude);
                MasterOrder reply = blockingStub.slaveLocSubmit(request);
                resultList.add(String.valueOf(reply.getMasterOrder()));
                return resultList;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                resultList.add("Failed... : " + System.getProperty("line.separator") + sw);
                return resultList;
            }
        }

        @Override
        protected void onPostExecute(List<String> resultList) {
            if (resultList != null) {
                String masterOrder = resultList.get(0);
                if (masterOrder != null) {
//                    Toast.makeText(getActivity(), masterOrder, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static SlaveLoc newSlaveLoc(String theSid, double theLon, double theLat) {
        return SlaveLoc.newBuilder().setSid(theSid).setLongitude(theLon).setLatitude(theLat).build();
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
                mapFilePath = FileUtil.getStorageDir(getActivity()) + "/" + FileUtil.getFileName(MAP_FILE_PREFIX) + JPEG_FILE_SUFFIX;
                File file = new File(mapFilePath);
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
//                takeClosePictureIntent();
//                saveTicket();
            }
        });
//        Toast.makeText(getActivity(), R.string.toast_map_saving, Toast.LENGTH_SHORT).show();
    }

    public void getUserInfo(String theUserID) {
        String[] projection = new String[]{KEY_POLICE_NAME, KEY_POLICE_CITY, KEY_POLICE_DEPT, KEY_POLICE_PORTRAIT_URI};
        Cursor cursor = resolver.query(POLICE_URL, projection, KEY_USER_ID + "=?", new String[]{theUserID}, null);
        if (cursor.moveToFirst()) {
            policeName = cursor.getString(cursor.getColumnIndex(KEY_POLICE_NAME));
            policeCity = cursor.getString(cursor.getColumnIndex(KEY_POLICE_CITY));
            policeDept = cursor.getString(cursor.getColumnIndex(KEY_POLICE_DEPT));
            policePortraitPath = cursor.getString(cursor.getColumnIndex(KEY_POLICE_PORTRAIT_URI));
        } else {
            Toast.makeText(getActivity(), R.string.toast_no_police_info, Toast.LENGTH_SHORT).show();
        }
        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {
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
        } else if (isUploaded == -1) {
            showUploadDialog();
        } else if (currentTime == null) {
            Toast.makeText(getActivity(), R.string.toast_ticket_no_time, Toast.LENGTH_SHORT).show();
        } else {
            // Insert the value into the Content Provider
            ContentValues values = new ContentValues();
            values.put(KEY_TICKET_ID, ticketID);
            values.put(KEY_USER_ID, userID);
            values.put(KEY_LICENSE_NUM, licenseNum);
            values.put(KEY_LICENSE_COLOR, licenseColor);
            values.put(KEY_DATETIME, currentTime);
            values.put(KEY_YEAR, year);
            values.put(KEY_MONTH, month);
            values.put(KEY_WEEK, week);
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
            if (mapFilePath != null) {
                values.put(KEY_MAP_URI, mapFilePath);
            }
            if (farImgFilePath != null) {
                values.put(KEY_FAR_IMG_URI, farImgFilePath);
            }
            if (closeImgFilePath != null) {
                values.put(KEY_CLOSE_IMG_URI, closeImgFilePath);
            }
            if (ticketImgFilePath != null) {
                values.put(KEY_TICKET_IMG_URI, ticketImgFilePath);
            }
            values.put(KEY_IS_UPLOADED, isUploaded);

            resolver.insert(TICKET_URL, values);
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

            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

            // Receive Location
            double theLon = location.getLongitude();
            double theLat = location.getLatitude();
            if (lonLatInChina(theLon, theLat)) {
                receivedLoc = true;
                longitude = theLon;
                latitude = theLat;
            }
            String streetName = null;
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeOffLineLocation) {// GPS或网络定位结果
                String locDesc = location.getLocationDescribe();
                address = location.getAddrStr();
                streetName = location.getAddress().street;
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
            addressLonLatTextView.setText(getString(R.string.ticket_address_header) + streetName);
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

        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            this.paper = paper;
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

        mChannel.shutdown();

        if (HPRTPrinter != null) {
            HPRTPrinterHelper.PortClose();
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
        outState.putString(SAVED_INSTANCE_POLICE_PORTRAIT_PATH, policePortraitPath);
        outState.putString(SAVED_INSTANCE_LICENSE_NUM, licenseNum);
        outState.putString(SAVED_INSTANCE_LICENSE_COLOR, licenseColor);
        outState.putInt(SAVED_INSTANCE_IS_UPLOADED, isUploaded);
        outState.putString(SAVED_INSTANCE_VEHICLE_TYPE, vehicleType);
        outState.putString(SAVED_INSTANCE_VEHICLE_COLOR, vehicleColor);

        outState.putString(SAVED_INSTANCE_CURR_TIME, currentTime);
        outState.putString(SAVED_INSTANCE_CURR_MAP_PATH, mapFilePath);
        outState.putString(SAVED_INSTANCE_CURR_IMG1_PATH, closeImgFilePath);
        outState.putString(SAVED_INSTANCE_CURR_IMG2_PATH, farImgFilePath);
        outState.putString(SAVED_INSTANCE_CURR_IMG3_PATH, ticketImgFilePath);

        outState.putString(SAVED_INSTANCE_ADDRESS, address);
        outState.putDouble(SAVED_INSTANCE_LONGITUDE, longitude);
        outState.putDouble(SAVED_INSTANCE_LATITUDE, latitude);

        outState.putInt(SAVED_INSTANCE_YEAR, year);
        outState.putInt(SAVED_INSTANCE_MONTH, month);
        outState.putInt(SAVED_INSTANCE_WEEK, week);
        outState.putInt(SAVED_INSTANCE_DAY, day);
        outState.putInt(SAVED_INSTANCE_HOUR, hour);
        outState.putInt(SAVED_INSTANCE_MINUTE, minute);

        super.onSaveInstanceState(outState);
    }

    private void takeFarPictureIntent() {
        try {
            farImgFile = FileUtil.createImageFile(getActivity(), FAR_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX);
            farImgFilePath = farImgFile.getAbsolutePath();
            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    .facing(Facing.BACK)
                    .to(farImgFile)
                    .flashModes(FLASH_MODES)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .confirmationQuality(0.3f)
                    .hintText(getString(R.string.camera_hint_far))
                    .build();
            startActivityForResult(intent, REQUEST_FAR_IMG_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            farImgFile = null;
            farImgFilePath = null;
            showAlertandRepeat(getString(R.string.img_file_create_error), REQUEST_FAR_IMG_CAPTURE);
        }
    }

    private void takeClosePictureIntent() {
        try {
            closeImgFile = FileUtil.createImageFile(getActivity(), CLOSE_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX);
            closeImgFilePath = closeImgFile.getAbsolutePath();
            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    // .skipConfirm()
                    .facing(Facing.BACK)
                    .to(closeImgFile)
                    // .debug()
                    .flashModes(FLASH_MODES)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .confirmationQuality(0.3f)
                    .hintText(getString(R.string.camera_hint_close))
                    .showFocus(1)
                    .build();
            startActivityForResult(intent, REQUEST_CLOSE_IMG_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            closeImgFile = null;
            closeImgFilePath = null;
            showAlertandRepeat(getString(R.string.img_file_create_error), REQUEST_CLOSE_IMG_CAPTURE);
        }
    }

    private void takeTicketPictureIntent() {
        try {
            ticketImgFile = FileUtil.createImageFile(getActivity(), TICKET_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX);
            ticketImgFilePath = ticketImgFile.getAbsolutePath();
            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    .facing(Facing.BACK)
                    .to(ticketImgFile)
                    .flashModes(FLASH_MODES)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .confirmationQuality(0.3f)
                    .hintText(getString(R.string.camera_hint_ticket))
                    .build();
            startActivityForResult(intent, REQUEST_TICKET_IMG_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            ticketImgFile = null;
            ticketImgFilePath = null;
            showAlertandRepeat(getString(R.string.img_file_create_error), REQUEST_TICKET_IMG_CAPTURE);
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

    private void showAlertandRepeat(String alertString, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(alertString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (requestCode) {
                    case REQUEST_FAR_IMG_CAPTURE:
                        takeFarPictureIntent();
                        break;
                    case REQUEST_CLOSE_IMG_CAPTURE:
                        takeClosePictureIntent();
                        break;
                    case REQUEST_TICKET_IMG_CAPTURE:
                        takeTicketPictureIntent();
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLicenseCheckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("\"" + licenseNum.substring(0, 2) + "\u2022" + licenseNum.substring(2) + "\" " + getString(R.string.alert_dialog_license_check));
        builder.setPositiveButton(R.string.alert_dialog_check_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showLiscenseColorPrompt();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_check_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showLiscenseNumPrompt();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPrintCheckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("重新打印?");
        builder.setPositiveButton(R.string.alert_dialog_check_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                printTicket();
                Toast.makeText(getActivity(), getResources().getString(R.string.activity_main_connected), Toast.LENGTH_LONG).show();
                takeTicketPictureIntent();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_check_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takeTicketPictureIntent();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.alert_dialog_upload));
        builder.setPositiveButton(R.string.alert_dialog_check_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                isUploaded = 1;
                saveTicket();
                recordTicket();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_check_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                isUploaded = 0;
                saveTicket();
                new SubmitTicketStatsGrpcTask().execute();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recordTicket() {
        new recordTicketGrpcTask().execute();
    }

    private class recordTicketGrpcTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                prepareProgressDialog();
            }
            getTicketStats();
        }

        @Override
        protected List<String> doInBackground(Void... nothing) {
            if (progressDialog == null) {
                prepareProgressDialog();
            }
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                TicketDetails ticketDetails = TicketDetails.newBuilder()
                        .setTicketId(ticketID)
                        .setUserId(userID)
                        .setLicenseNum(licenseNum)
                        .setLicenseColor(licenseColor)
                        .setVehicleType(vehicleType)
                        .setVehicleColor(vehicleColor)
                        .setYear(year)
                        .setMonth(month)
                        .setWeek(week)
                        .setDay(day)
                        .setHour(hour)
                        .setMinute(minute)
                        .setAddress(address)
                        .setLongitude(longitude)
                        .setLatitude(latitude)
                        .setMapImage(ByteString.copyFrom(getImageBytesfromPath(mapFilePath)))
                        .setFarImage(ByteString.copyFrom(getImageBytesfromPath(farImgFilePath)))
                        .setCloseImage(ByteString.copyFrom(getImageBytesfromPath(closeImgFilePath)))
                        .setTicketImage(ByteString.copyFrom(getImageBytesfromPath(ticketImgFilePath)))
                        .setSavedTicketCount(numSavedTicket)
                        .setUploadedTicketCount(numUploadedTicket)
                        .build();
                RecordReply reply = blockingStub.recordTicket(ticketDetails);
                resultList.add(String.valueOf(reply.getRecordSuccess()));
                return resultList;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                resultList.add("Failed... : " + System.getProperty("line.separator") + sw);
                return resultList;
            }
        }

        @Override
        protected void onPostExecute(List<String> resultList) {
            dismissProgressDialog();
            if (resultList != null) {
                String createSuccess = resultList.get(0);
                if (createSuccess != null && createSuccess.equals("true")) {
                    Toast.makeText(getActivity(), R.string.toast_record_ticket_success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), R.string.toast_record_ticket_failed, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.toast_record_ticket_failed, Toast.LENGTH_LONG).show();
            }
            backToHome();
        }
    }

    private class SubmitTicketStatsGrpcTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            getTicketStats();
        }

        @Override
        protected List<String> doInBackground(Void... nothing) {
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                TicketStats loginRequest = TicketStats.newBuilder().setSid(userID).setSavedTicketCount(numSavedTicket).setUploadedTicketCount(numUploadedTicket).build();
                StatsReply reply = blockingStub.submitTicketStats(loginRequest);
                resultList.add(String.valueOf(reply.getStatsSuccess()));
                return resultList;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                resultList.add("Failed... : " + System.getProperty("line.separator") + sw);
                return resultList;
            }
        }

        @Override
        protected void onPostExecute(List<String> resultList) {
        }
    }

    private void getTicketStats() {
        // Projection contains the columns we want
        String[] projection = new String[]{KEY_IS_UPLOADED};
        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(TICKET_URL, projection, null, null, null);

        numSavedTicket = cursor.getCount();

        int[] ticketDetailsArray = new int[cursor.getCount()];
        // Cycle through and display every row of data
        if (cursor.moveToFirst()) {
            numUploadedTicket = 0;
            do {
                int columnValue = cursor.getInt(cursor.getColumnIndex(KEY_IS_UPLOADED));
                if (columnValue == 1) {
                    numUploadedTicket += columnValue;
                }
            } while (cursor.moveToNext());
        }
        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {

        }
    }

    public byte[] getImageBytesfromPath(String filePath) {
        Bitmap bitmap = BitmapUtil.getScaledBitmap(filePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
        ByteArrayOutputStream imageOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_RATIO, imageOS);
        return imageOS.toByteArray();
    }

    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progress_dialog_recording_ticket));
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
        progressDialog = null;
    }


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
                showLiscenseNumPrompt();
            }
        }
    }

}
