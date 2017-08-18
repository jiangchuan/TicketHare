package io.chizi.tickethare.acquire;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.IPort;
import HPRTAndroidSDK.PublicFunction;
import io.chizi.tickethare.R;
import io.chizi.tickethare.util.BitmapUtil;
import io.chizi.tickethare.util.ColorSpinnerAdapter;

import static io.chizi.tickethare.util.AppConstants.BACK_LICENSE_COLOR;
import static io.chizi.tickethare.util.AppConstants.BACK_LICENSE_NUM;
import static io.chizi.tickethare.util.AppConstants.BACK_VEHICLE_COLOR;
import static io.chizi.tickethare.util.AppConstants.BACK_VEHICLE_TYPE;
import static io.chizi.tickethare.util.AppConstants.CURRENT_ADDRESS;
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

public class PreviewActivity extends Activity {
    // Database
    ContentResolver resolver; // Provides access to other applications Content Providers

    private Long ticketID = -1L;
    private String userID;
    private String policeName;
    private String policeCity;
    private String policeDept;
    private String licenseNum;
    private String licenseColor = "蓝";
    private String vehicleType = "小型客车";
    private String vehicleColor;

    private SimpleDateFormat dateFormatf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String currentTime;
    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hour = -1;
    private int minute = -1;

    private String address;
    private double longitude;
    private double latitude;

    private String currentMapFilePath;
    private String currentImageFilePath1;
    private String currentImageFilePath2;
    private String currentImageFilePath3;

    private TextView ticketHintTextView;
    private TextView vehicleColorTextView;
    private TextView vehicleTypeTextView;
    private EditText licenseEditText;
    private EditText dateTimeEditText;
    private EditText addressEditText;
    private EditText yearEditText;
    private EditText monthEditText;
    private EditText dayEditText;

    private Spinner vehicleColorSpinner;

    private RadioGroup vehicleTypeRadioGroup1;
    private RadioGroup vehicleTypeRadioGroup2;
    private RadioButton bigBusRadioButton;
    private RadioButton smallBusRadioButton;
    private RadioButton bigTruckRadioButton;
    private RadioButton smallTruckRadioButton;
    private RadioButton motorCycleRadioButton;
    private RadioButton otherCarTypeRadioButton;

    private RadioGroup licenseColorRadioGroup;
    private RadioButton yellowRadioButton;
    private RadioButton blueRadioButton;
    private RadioButton blackRadioButton;
    private RadioButton otherColorRadioButton;

    private TextView ticketTitleTextView;
    private TextView ticketDespTextView;
    private EditText ticketPoliceEditText;

    private Button generateTicketButton;
    private Button cancelTicketButton;

    private ImageView mapDetailsImageView;
    private ImageView photo1DetailsImageView;
    private ImageView photo2DetailsImageView;

//    // HM printer starts here
//    private Context thisCon = null;
//    private BluetoothAdapter mBluetoothAdapter;
//    private PublicFunction PFun = null;
//
//    private Spinner spnPrinterList = null;
//
//    private ArrayAdapter arrPrinterList;
//    private static HPRTPrinterHelper HPRTPrinter = new HPRTPrinterHelper();
//    private String PrinterName = "";
//
//    private Handler handler;
//    private ProgressDialog dialog;
//    public static String paper = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_preview);

//        try {
//            thisCon = this.getApplicationContext();
//            PFun = new PublicFunction(thisCon);
//            InitSetting();
//            InitCombox();
//            this.spnPrinterList.setOnItemSelectedListener(new OnItemSelectedPrinter());
//            EnableBluetooth();
//            handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    if (msg.what == 1) {
//                        Toast.makeText(PreviewActivity.this, "succeed", Toast.LENGTH_LONG).show();
//                        dialog.cancel();
//                    } else {
//                        Toast.makeText(PreviewActivity.this, "failure", Toast.LENGTH_LONG).show();
//                        dialog.cancel();
//                    }
//                }
//            };
//        } catch (Exception e) {
//            Log.e("HPRTSDKSample", (new StringBuilder("PreviewActivity --> onCreate ")).append(e.getMessage()).toString());
//        }

        if (savedInstanceState != null) {
            ticketID = savedInstanceState.getLong(SAVED_INSTANCE_TICKET_ID);
            userID = savedInstanceState.getString(SAVED_INSTANCE_USER_ID);
            policeName = savedInstanceState.getString(SAVED_INSTANCE_POLICE_NAME);
            policeCity = savedInstanceState.getString(SAVED_INSTANCE_POLICE_CITY);
            policeDept = savedInstanceState.getString(SAVED_INSTANCE_POLICE_DEPT);
            licenseNum = savedInstanceState.getString(SAVED_INSTANCE_LICENSE_NUM);
            licenseColor = savedInstanceState.getString(SAVED_INSTANCE_LICENSE_COLOR);
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

        Intent intentFrom = getIntent(); // Get the Intent that called for this Activity to open
        if (intentFrom != null) {
            Bundle params = intentFrom.getExtras();
            if (intentFrom != null) {
                userID = params.getString(CURRENT_USER_ID);
                policeName = params.getString(CURRENT_POLICE_NAME);
                policeCity = params.getString(CURRENT_POLICE_CITY);
                policeDept = params.getString(CURRENT_POLICE_DEPT);
                ticketID = params.getLong(CURRENT_TICKET_ID);
                licenseNum = params.getString(CURRENT_LICENSE_NUM);
                licenseColor = params.getString(CURRENT_LICENSE_COLOR);
                vehicleType = params.getString(CURRENT_VEHICLE_TYPE);
                vehicleColor = params.getString(CURRENT_VEHICLE_COLOR);
                address = params.getString(CURRENT_ADDRESS);
                longitude = params.getDouble(CURRENT_LONGITUDE);
                latitude = params.getDouble(CURRENT_LATITUDE);
                currentMapFilePath = params.getString(MAP_FILE_PATH);
                currentImageFilePath1 = params.getString(FAR_IMG_FILE_PATH);
                currentImageFilePath2 = params.getString(CLOSE_IMG_FILE_PATH);
            }
        }
        Calendar now = Calendar.getInstance();
        currentTime = dateFormatf.format(now.getTime());
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        day = now.get(Calendar.DAY_OF_MONTH);
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);

        ticketHintTextView = (TextView) findViewById(R.id.ticket_preview_hint_textview);
        ticketHintTextView.setVisibility(View.VISIBLE);

        ticketTitleTextView = (TextView) findViewById(R.id.textview_ticket_title);
        ticketTitleTextView.setText(policeCity + getString(R.string.ticket_title));

        vehicleColorTextView = (TextView) findViewById(R.id.vehicle_color_textview);
        vehicleColorTextView.setTextColor(getResources().getColor(R.color.red));
        vehicleTypeTextView = (TextView) findViewById(R.id.vehicle_type_textview);
        vehicleTypeTextView.setTextColor(getResources().getColor(R.color.red));

        ticketDespTextView = (TextView) findViewById(R.id.textview_ticket_description);
        ticketDespTextView.setText(getString(R.string.ticket_description1) + policeDept + getString(R.string.ticket_description2));

        licenseEditText = (EditText) findViewById(R.id.car_license);
        licenseEditText.setText(licenseNum);
        dateTimeEditText = (EditText) findViewById(R.id.ticket_dateTime);
        dateTimeEditText.setText(currentTime);

        addressEditText = (EditText) findViewById(R.id.ticket_parking_address);
        addressEditText.setText(address + " (" + longitude + ", " + latitude + ")");

        yearEditText = (EditText) findViewById(R.id.ticket_year);
        yearEditText.setText(Integer.toString(year));
        monthEditText = (EditText) findViewById(R.id.ticket_month);
        monthEditText.setText(Integer.toString(month));
        dayEditText = (EditText) findViewById(R.id.ticket_day);
        dayEditText.setText(Integer.toString(day));

        ticketPoliceEditText = (EditText) findViewById(R.id.ticket_police);
        ticketPoliceEditText.setText(policeName);

        vehicleColorSpinner = (Spinner) findViewById(R.id.car_color);
        String[] vehicleColorNames = getResources().getStringArray(R.array.car_color_arrays);
        int[] vehicleColorBackgroundInts = getResources().getIntArray(R.array.vehicle_color_int_arrays);
        int[] vehicleColorTextInts = getResources().getIntArray(R.array.vehicle_text_color_int_arrays);
        ColorSpinnerAdapter spinnerArrayAdapter = new ColorSpinnerAdapter(
                PreviewActivity.this, vehicleColorNames, vehicleColorTextInts, vehicleColorBackgroundInts);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        vehicleColorSpinner.setAdapter(spinnerArrayAdapter);

        if (vehicleColor != null) {
            switch (vehicleColor) {
                case "黑":
                    vehicleColorSpinner.setSelection(0);
                    break;
                case "白":
                    vehicleColorSpinner.setSelection(1);
                    break;
                case "银":
                    vehicleColorSpinner.setSelection(2);
                    break;
                case "灰":
                    vehicleColorSpinner.setSelection(3);
                    break;
                case "红":
                    vehicleColorSpinner.setSelection(4);
                    break;
                case "橙":
                    vehicleColorSpinner.setSelection(5);
                    break;
                case "黄":
                    vehicleColorSpinner.setSelection(6);
                    break;
                case "绿":
                    vehicleColorSpinner.setSelection(7);
                    break;
                case "青":
                    vehicleColorSpinner.setSelection(8);
                    break;
                case "蓝":
                    vehicleColorSpinner.setSelection(9);
                    break;
                case "紫":
                    vehicleColorSpinner.setSelection(10);
                    break;
            }
        }

        bigBusRadioButton = (RadioButton) findViewById(R.id.radiobutton_big_bus);
        smallBusRadioButton = (RadioButton) findViewById(R.id.radiobutton_small_bus);
        bigTruckRadioButton = (RadioButton) findViewById(R.id.radiobutton_big_truck);
        smallTruckRadioButton = (RadioButton) findViewById(R.id.radiobutton_small_truck);
        motorCycleRadioButton = (RadioButton) findViewById(R.id.radiobutton_motorcycle);
        otherCarTypeRadioButton = (RadioButton) findViewById(R.id.radiobutton_other_car_type);
        vehicleTypeRadioGroup1 = (RadioGroup) findViewById(R.id.radiogroup_vehicle_type1);
        vehicleTypeRadioGroup1.clearCheck();
        vehicleTypeRadioGroup2 = (RadioGroup) findViewById(R.id.radiogroup_vehicle_type2);
        vehicleTypeRadioGroup2.clearCheck();
        if (vehicleType != null) {
            switch (vehicleType) {
                case "大型客车":
                    vehicleTypeRadioGroup2.clearCheck();
                    vehicleTypeRadioGroup1.check(R.id.radiobutton_big_bus);
                    break;
                case "小型客车":
                    vehicleTypeRadioGroup2.clearCheck();
                    vehicleTypeRadioGroup1.check(R.id.radiobutton_small_bus);
                    break;
                case "大型货车":
                    vehicleTypeRadioGroup2.clearCheck();
                    vehicleTypeRadioGroup1.check(R.id.radiobutton_big_truck);
                    break;
                case "小型货车":
                    vehicleTypeRadioGroup1.clearCheck();
                    vehicleTypeRadioGroup2.check(R.id.radiobutton_small_truck);
                    break;
                case "摩托车":
                    vehicleTypeRadioGroup1.clearCheck();
                    vehicleTypeRadioGroup2.check(R.id.radiobutton_motorcycle);
                    break;
                default:
                    vehicleTypeRadioGroup1.clearCheck();
                    vehicleTypeRadioGroup2.check(R.id.radiobutton_other_car_type);
            }
        }

        yellowRadioButton = (RadioButton) findViewById(R.id.radiobutton_yellow);
        blueRadioButton = (RadioButton) findViewById(R.id.radiobutton_blue);
        blackRadioButton = (RadioButton) findViewById(R.id.radiobutton_black);
        otherColorRadioButton = (RadioButton) findViewById(R.id.radiobutton_other_license_color);
        licenseColorRadioGroup = (RadioGroup) findViewById(R.id.radiogroup_license_color);

        generateTicketButton = (Button) findViewById(R.id.button_generate_ticket);
        generateTicketButton.setVisibility(View.VISIBLE);

        cancelTicketButton = (Button) findViewById(R.id.button_cancel_ticket);
        cancelTicketButton.setVisibility(View.VISIBLE);

        //Database
        resolver = getContentResolver();

        generateTicketButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateVehicleInfo();
                backToPreviousActivity();
//                connectToBlueTooth();
            }
        });

        cancelTicketButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (licenseColor != null) {
            switch (licenseColor) {
                case "黄":
                    yellowRadioButton.setChecked(true);
                    break;
                case "蓝":
                    blueRadioButton.setChecked(true);
                    break;
                case "黑":
                    blackRadioButton.setChecked(true);
                    break;
                default:
                    otherColorRadioButton.setChecked(true);
            }
        }

        mapDetailsImageView = (ImageView) findViewById(R.id.mapDetailsImageView);
        photo1DetailsImageView = (ImageView) findViewById(R.id.photo1DetailsImageView);
        photo2DetailsImageView = (ImageView) findViewById(R.id.photo2DetailsImageView);

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

        ViewTreeObserver mapVTO = mapDetailsImageView.getViewTreeObserver();
        mapVTO.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mapDetailsImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (currentMapFilePath != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(currentMapFilePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
                    if (bitmap != null) {
                        int finalWidth = mapDetailsImageView.getMeasuredWidth();
                        int finalHeight = mapDetailsImageView.getMeasuredHeight();
                        int finalSize = finalWidth > finalHeight ? finalWidth : finalHeight;
                        BitmapUtil.setBitmapToImageView(mapDetailsImageView, finalSize, finalSize, bitmap);
                    }
                }
                return true;
            }
        });

        ViewTreeObserver photo1VTO = photo1DetailsImageView.getViewTreeObserver();
        photo1VTO.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                photo1DetailsImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (currentImageFilePath1 != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(currentImageFilePath1, TRANS_IMAGE_W, TRANS_IMAGE_H);
                    if (bitmap != null) {
                        int finalWidth = photo1DetailsImageView.getMeasuredWidth();
                        int finalHeight = photo1DetailsImageView.getMeasuredHeight();
                        int finalSize = finalWidth > finalHeight ? finalWidth : finalHeight;
                        BitmapUtil.setBitmapToImageView(photo1DetailsImageView, finalSize, finalSize, bitmap);
                    }
                }
                return true;
            }
        });

        ViewTreeObserver photo2VTO = photo2DetailsImageView.getViewTreeObserver();
        photo2VTO.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                photo2DetailsImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (currentImageFilePath2 != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(currentImageFilePath2, TRANS_IMAGE_W, TRANS_IMAGE_H);
                    if (bitmap != null) {
                        int finalWidth = photo2DetailsImageView.getMeasuredWidth();
                        int finalHeight = photo2DetailsImageView.getMeasuredHeight();
                        int finalSize = finalWidth > finalHeight ? finalWidth : finalHeight;
                        BitmapUtil.setBitmapToImageView(photo2DetailsImageView, finalSize, finalSize, bitmap);
                    }
                }
                return true;
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        String paper = PFun.ReadSharedPreferencesData("papertype");
//        if (!"".equals(paper)) {
//            PreviewActivity.paper = paper;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (HPRTPrinter != null) {
//            HPRTPrinterHelper.PortClose();
//        }
//        super.onDestroy();
//    }
//
//    private void connectToBlueTooth() {
//        if (HPRTPrinter != null) {
//            HPRTPrinterHelper.PortClose();
//        }
//        Intent serverIntent = new Intent(PreviewActivity.this, DeviceListActivity.class);
//        startActivityForResult(serverIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);
//        return;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (resultCode) {
//            case HPRTPrinterHelper.ACTIVITY_CONNECT_BT:
//                String strIsConnected = data.getExtras().getString("is_connected");
//                if (strIsConnected.equals("NO")) {
//                    Toast.makeText(PreviewActivity.this, getResources().getString(R.string.activity_main_scan_error), Toast.LENGTH_LONG).show();
//                    return;
//                } else {
//                    updateVehicleInfo();
//                    printTicket();
//                    Toast.makeText(PreviewActivity.this, getResources().getString(R.string.activity_main_connected), Toast.LENGTH_LONG).show();
//                    backToPreviousActivity();
//                    return;
//                }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void printTicket() {
//        try {
//            HPRTPrinterHelper.printAreaSize("0", "200", "200", "700", "1");
////            String[] receiptLines = getResources().getStringArray(R.array.activity_main_sample_2inch_receipt);
//            int receiptLen = 16;
//            String[] receiptLines = new String[receiptLen];
//            receiptLines[0] = "成都市道路停车记录告知单";
//            receiptLines[1] = "编号: 5104312681";
//            receiptLines[2] = "车辆牌号: " + licenseNum + "            车身颜色: " + vehicleColor;
//            receiptLines[3] = "车辆类型: " + vehicleType;
//            receiptLines[4] = "号牌颜色: " + licenseColor;
//            receiptLines[5] = "停车时间: " + year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分";
//            receiptLines[6] = "停车地点: " + address;
//            receiptLines[7] = "该机动车在上述时间、地点未在道路停车泊位或停";
//            receiptLines[8] = "车场内停放，现已对现场道路交通情况进行了图像记录，";
//            receiptLines[9] = "并报告交警" + policeDept + "审核认定是否违法停放。";
//            receiptLines[10] = "你可在七个工作日后登陆http://www.cdjg.gov.cn查询，";
//            receiptLines[11] = "或在收到公安机关交通管理部门通知后前往接受处理。";
//            receiptLines[12] = "交通协管员: " + policeName;
//            receiptLines[13] = year + "年" + month + "月" + day + "日";
//            receiptLines[14] = "----------------------------------------";
//            receiptLines[15] = "备注: ";
//
//            HPRTPrinterHelper.LanguageEncode = "GBK";
//
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.CENTER);
//            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "0", receiptLines[0]);
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.RIGHT);
//            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + 30, receiptLines[1]);
//
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
//            for (int i = 2; i < 7; i++) {
//                HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "0" + (i * 30), receiptLines[i]);
//            }
//
//            int iLine = 7;
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.RIGHT);
//            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (iLine * 30), receiptLines[iLine]);
//
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
//            for (int i = 8; i < 13; i++) {
//                HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (i * 30), receiptLines[i]);
//            }
//
//            iLine = 13;
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.RIGHT);
//            HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (iLine * 30), receiptLines[iLine]);
//
//            HPRTPrinterHelper.Align(HPRTPrinterHelper.LEFT);
//            for (int i = receiptLen - 2; i < receiptLen; i++) {
//                HPRTPrinterHelper.Text(HPRTPrinterHelper.TEXT, "8", "0", "0", "" + (i * 30), receiptLines[i]);
//            }
//
//            if ("1".equals(PreviewActivity.paper)) {
//                HPRTPrinterHelper.Form();
//            }
//            HPRTPrinterHelper.Print();
//        } catch (Exception e) {
//            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
//        }
//    }
//
//    private void InitSetting() {
//        String SettingValue = PFun.ReadSharedPreferencesData("Codepage");
//        if (SettingValue.equals(""))
//            PFun.WriteSharedPreferencesData("Codepage", "0,PC437(USA:Standard Europe)");
//
//        SettingValue = PFun.ReadSharedPreferencesData("Cut");
//        if (SettingValue.equals(""))
//            PFun.WriteSharedPreferencesData("Cut", "0");
//
//        SettingValue = PFun.ReadSharedPreferencesData("Cashdrawer");
//        if (SettingValue.equals(""))
//            PFun.WriteSharedPreferencesData("Cashdrawer", "0");
//
//        SettingValue = PFun.ReadSharedPreferencesData("Buzzer");
//        if (SettingValue.equals(""))
//            PFun.WriteSharedPreferencesData("Buzzer", "0");
//
//        SettingValue = PFun.ReadSharedPreferencesData("Feeds");
//        if (SettingValue.equals(""))
//            PFun.WriteSharedPreferencesData("Feeds", "0");
//        String paper = PFun.ReadSharedPreferencesData("papertype");
//        if (!"".equals(paper)) {
//            PreviewActivity.paper = paper;
//        }
//    }
//
//    //add printer list
//    private void InitCombox() {
//        try {
//            arrPrinterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
//            String strSDKType = getResources().getString(R.string.sdk_type);
//            if (strSDKType.equals("all"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_cpcl, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("hprt"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_hprt, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("mkt"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_mkt, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("mprint"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_mprint, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("sycrown"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_sycrown, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("mgpos"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_mgpos, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("ds"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_ds, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("cst"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_cst, android.R.layout.simple_spinner_item);
//            if (strSDKType.equals("other"))
//                arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list_other, android.R.layout.simple_spinner_item);
//            arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            PrinterName = arrPrinterList.getItem(0).toString();
//            spnPrinterList.setAdapter(arrPrinterList);
//        } catch (Exception e) {
//            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> InitCombox ")).append(e.getMessage()).toString());
//        }
//    }
//
//    private class OnItemSelectedPrinter implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//            PrinterName = arrPrinterList.getItem(arg2).toString();
//            HPRTPrinter = new HPRTPrinterHelper(thisCon, PrinterName);
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> arg0) {
//        }
//    }
//
//    private boolean EnableBluetooth() {
//        boolean bRet = false;
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter != null) {
//            if (mBluetoothAdapter.isEnabled())
//                return true;
//            mBluetoothAdapter.enable();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                bRet = true;
//                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
//            }
//        } else {
//            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
//        }
//        return bRet;
//    }

    private void updateVehicleInfo() {
        licenseNum = licenseEditText.getText().toString();
        vehicleColor = vehicleColorSpinner.getSelectedItem().toString();
    }

    private void backToPreviousActivity() {
        Intent goingBack = new Intent();
        goingBack.putExtra(BACK_LICENSE_NUM, licenseNum);
        goingBack.putExtra(BACK_VEHICLE_COLOR, vehicleColor);
        goingBack.putExtra(BACK_VEHICLE_TYPE, vehicleType);
        goingBack.putExtra(BACK_LICENSE_COLOR, licenseColor);
        setResult(RESULT_OK, goingBack);
        finish();
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

//        BitmapDrawable drawable = (BitmapDrawable) mapDetailsImageView.getDrawable();
//        if (drawable != null) {
//            Bitmap bitmap = drawable.getBitmap();
//            outState.putParcelable(SAVED_INSTANCE_MAP, bitmap);
//        }
//        drawable = (BitmapDrawable) photo1DetailsImageView.getDrawable();
//        if (drawable != null) {
//            Bitmap bitmap = drawable.getBitmap();
//            outState.putParcelable(SAVED_INSTANCE_PHOTO1, bitmap);
//        }
//        drawable = (BitmapDrawable) photo2DetailsImageView.getDrawable();
//        if (drawable != null) {
//            Bitmap bitmap = drawable.getBitmap();
//            outState.putParcelable(SAVED_INSTANCE_PHOTO2, bitmap);
//        }

        super.onSaveInstanceState(outState);
    }

}
