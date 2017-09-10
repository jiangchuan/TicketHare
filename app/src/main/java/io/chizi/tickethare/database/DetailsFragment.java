package io.chizi.tickethare.database;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import io.chizi.ticket.RecordReply;
import io.chizi.ticket.TicketDetails;
import io.chizi.ticket.TicketGrpc;
import io.chizi.tickethare.R;
import io.chizi.tickethare.util.BitmapUtil;
import io.chizi.tickethare.util.ColorSpinnerAdapter;
import io.chizi.tickethare.util.FileUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.chizi.tickethare.database.DBProvider.KEY_ADDRESS;
import static io.chizi.tickethare.database.DBProvider.KEY_VEHICLE_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_VEHICLE_TYPE;
import static io.chizi.tickethare.database.DBProvider.KEY_DAY;
import static io.chizi.tickethare.database.DBProvider.KEY_HOUR;
import static io.chizi.tickethare.database.DBProvider.KEY_FAR_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_CLOSE_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_IS_UPLOADED;
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
import static io.chizi.tickethare.database.DBProvider.KEY_TIME_MILIS;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_WEEK;
import static io.chizi.tickethare.database.DBProvider.KEY_YEAR;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.HOST_IP;
import static io.chizi.tickethare.util.AppConstants.PORT;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_INDEX;
import static io.chizi.tickethare.util.AppConstants.TITLES_FRAGMENT_TICKET_ID;
import static io.chizi.tickethare.util.AppConstants.TRANS_IMAGE_H;
import static io.chizi.tickethare.util.AppConstants.TRANS_IMAGE_W;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class DetailsFragment extends Fragment {
    ContentResolver resolver; // Provides access to other applications Content Providers
    private ManagedChannel mChannel;

    private long ticketID;
    private String userID;
    private String licenseNum;
    private String licenseColor;
    private String vehicleType;
    private String vehicleColor;
    private int year;
    private int month;
    private int week;
    private int day;
    private int hour;
    private int minute;
    private long timeMilis = 0;
    private String address;
    private double longitude;
    private double latitude;
    private String mapFilePath;
    private String farImgFilePath;
    private String closeImgFilePath;
    private String ticketImgFilePath;
    private int isUploaded = -1;

    private ProgressDialog progressDialog;

    private String policeName;
    private String policeCity;
    private String policeDept;

    private TextView ticketTitleTextView;
    private TextView ticketDespTextView;
    private TextView ticketIDTextView;
    private TextView proofNotifyTextView;

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

    private EditText ticketPoliceEditText;

    private ImageView mapDetailsImageView;
    private ImageView photo1DetailsImageView;
    private ImageView photo2DetailsImageView;
    private ImageView photo3DetailsImageView;

    private Button uploadTicketButton;

    // Create a DetailsFragment that contains the hero data for the correct index
    public static DetailsFragment newInstance(int index, long theTicketID) {
        DetailsFragment f = new DetailsFragment();

        // Bundles are used to pass data using a key "index" and a value
        Bundle args = new Bundle();
        args.putInt(SAVED_INSTANCE_CURR_INDEX, index);
        args.putLong(TITLES_FRAGMENT_TICKET_ID, theTicketID);

        // Assign key value to the fragment
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt(SAVED_INSTANCE_CURR_INDEX, 0);
    }

    public long getShownTicketID() {
        return getArguments().getLong(TITLES_FRAGMENT_TICKET_ID, -1L);
    }

    // LayoutInflator puts the Fragment on the screen
    // ViewGroup is the view you want to attach the Fragment to
    // Bundle stores key value pairs so that data can be saved
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resolver = getActivity().getContentResolver();

        ticketIDTextView = (TextView) view.findViewById(R.id.ticket_id);
        ticketTitleTextView = (TextView) view.findViewById(R.id.textview_ticket_title);
        ticketDespTextView = (TextView) view.findViewById(R.id.textview_ticket_description);
        proofNotifyTextView = (TextView) view.findViewById(R.id.proof_notification_textview);
        proofNotifyTextView.setVisibility(View.VISIBLE);

        licenseEditText = (EditText) view.findViewById(R.id.car_license);
        dateTimeEditText = (EditText) view.findViewById(R.id.ticket_dateTime);
        addressEditText = (EditText) view.findViewById(R.id.ticket_parking_address);

        yearEditText = (EditText) view.findViewById(R.id.ticket_year);
        monthEditText = (EditText) view.findViewById(R.id.ticket_month);
        dayEditText = (EditText) view.findViewById(R.id.ticket_day);
        ticketPoliceEditText = (EditText) view.findViewById(R.id.ticket_police);

        vehicleColorSpinner = (Spinner) view.findViewById(R.id.car_color);
        String[] vehicleColorNames = getResources().getStringArray(R.array.car_color_arrays);
        int[] vehicleColorBackgroundInts = getResources().getIntArray(R.array.vehicle_color_int_arrays);
        int[] vehicleColorTextInts = getResources().getIntArray(R.array.vehicle_text_color_int_arrays);
        ColorSpinnerAdapter spinnerArrayAdapter = new ColorSpinnerAdapter(
                getActivity(), vehicleColorNames, vehicleColorTextInts, vehicleColorBackgroundInts);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        vehicleColorSpinner.setAdapter(spinnerArrayAdapter);

        bigBusRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_big_bus);
        smallBusRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_small_bus);
        bigTruckRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_big_truck);
        smallTruckRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_small_truck);
        motorCycleRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_motorcycle);
        otherCarTypeRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_other_car_type);
        vehicleTypeRadioGroup1 = (RadioGroup) view.findViewById(R.id.radiogroup_vehicle_type1);
        vehicleTypeRadioGroup1.clearCheck();
        vehicleTypeRadioGroup2 = (RadioGroup) view.findViewById(R.id.radiogroup_vehicle_type2);
        vehicleTypeRadioGroup2.clearCheck();

        yellowRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_yellow);
        blueRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_blue);
        blackRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_black);
        otherColorRadioButton = (RadioButton) view.findViewById(R.id.radiobutton_other_license_color);
        licenseColorRadioGroup = (RadioGroup) view.findViewById(R.id.radiogroup_license_color);

        mapDetailsImageView = (ImageView) view.findViewById(R.id.mapDetailsImageView);
        photo1DetailsImageView = (ImageView) view.findViewById(R.id.photo1DetailsImageView);
        photo2DetailsImageView = (ImageView) view.findViewById(R.id.photo2DetailsImageView);
        photo3DetailsImageView = (ImageView) view.findViewById(R.id.photo3DetailsImageView);

        uploadTicketButton = (Button) view.findViewById(R.id.button_generate_ticket);

        updateDetails();

        if (isUploaded == 0) {
            uploadTicketButton.setText(getString(R.string.upload_ticket));
            uploadTicketButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChannel = ManagedChannelBuilder.forAddress(HOST_IP, PORT)
                .usePlaintext(true)
                .build();

        uploadTicketButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new recordTicketGrpcTask().execute();
            }
        });

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
        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {
        }
    }

    public void getTicketInfo(String theTicketIDStr) {
        String[] projection = new String[]{KEY_USER_ID, KEY_LICENSE_NUM, KEY_LICENSE_COLOR, KEY_VEHICLE_TYPE, KEY_VEHICLE_COLOR, KEY_YEAR, KEY_MONTH, KEY_WEEK, KEY_DAY, KEY_HOUR, KEY_MINUTE, KEY_TIME_MILIS, KEY_ADDRESS, KEY_LONGITUDE, KEY_LATITUDE, KEY_MAP_URI, KEY_FAR_IMG_URI, KEY_CLOSE_IMG_URI, KEY_TICKET_IMG_URI, KEY_IS_UPLOADED};
        Cursor cursor = resolver.query(TICKET_URL, projection, KEY_TICKET_ID + "=?", new String[]{theTicketIDStr}, null);
        if (cursor.moveToFirst()) {
            userID = cursor.getString(cursor.getColumnIndex(KEY_USER_ID));
            licenseNum = cursor.getString(cursor.getColumnIndex(KEY_LICENSE_NUM));
            licenseColor = cursor.getString(cursor.getColumnIndex(KEY_LICENSE_COLOR));
            vehicleType = cursor.getString(cursor.getColumnIndex(KEY_VEHICLE_TYPE));
            vehicleColor = cursor.getString(cursor.getColumnIndex(KEY_VEHICLE_COLOR));
            year = cursor.getInt(cursor.getColumnIndex(KEY_YEAR));
            month = cursor.getInt(cursor.getColumnIndex(KEY_MONTH));
            week = cursor.getInt(cursor.getColumnIndex(KEY_WEEK));
            day = cursor.getInt(cursor.getColumnIndex(KEY_DAY));
            hour = cursor.getInt(cursor.getColumnIndex(KEY_HOUR));
            minute = cursor.getInt(cursor.getColumnIndex(KEY_MINUTE));
            timeMilis = cursor.getLong(cursor.getColumnIndex(KEY_TIME_MILIS));
            address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
            longitude = cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE));
            latitude = cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE));
            mapFilePath = cursor.getString(cursor.getColumnIndex(KEY_MAP_URI));
            farImgFilePath = cursor.getString(cursor.getColumnIndex(KEY_FAR_IMG_URI));
            closeImgFilePath = cursor.getString(cursor.getColumnIndex(KEY_CLOSE_IMG_URI));
            ticketImgFilePath = cursor.getString(cursor.getColumnIndex(KEY_TICKET_IMG_URI));
            isUploaded = cursor.getInt(cursor.getColumnIndex(KEY_IS_UPLOADED));
        } else {
            Toast.makeText(getActivity(), R.string.toast_no_ticket_info, Toast.LENGTH_SHORT).show();
        }
        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {
        }
    }


    private class recordTicketGrpcTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                prepareProgressDialog();
            }
        }
        @Override
        protected List<String> doInBackground(Void... nothing) {
            if (progressDialog == null) {
                prepareProgressDialog();
            }
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                TicketDetails.Builder ticketDetailsBuilder = TicketDetails.newBuilder();
                ticketDetailsBuilder
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
                        .setTicketTime(timeMilis)
                        .setAddress(address)
                        .setLongitude(longitude)
                        .setLatitude(latitude)
                        .setIsUploaded(true)
                        .setMapImage(ByteString.copyFrom(FileUtil.getImageBytesfromPath(mapFilePath)))
                        .setFarImage(ByteString.copyFrom(FileUtil.getImageBytesfromPath(farImgFilePath)))
                        .setCloseImage(ByteString.copyFrom(FileUtil.getImageBytesfromPath(closeImgFilePath)))
                        .setTicketImage(ByteString.copyFrom(FileUtil.getImageBytesfromPath(ticketImgFilePath)));
                TicketDetails ticketDetails = ticketDetailsBuilder.build();
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
                    uploadTicketButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(), R.string.toast_record_ticket_failed, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.toast_record_ticket_failed, Toast.LENGTH_LONG).show();
            }

        }
    }


    private void updateDetails() {
        if (resolver == null) {
            resolver = getActivity().getContentResolver();
        }
        ticketID = getShownTicketID();
        String ticketIDStr = Long.toString(ticketID);
        getTicketInfo(ticketIDStr);

        ticketIDTextView.setText(ticketIDStr);
        licenseEditText.setText(licenseNum);
        addressEditText.setText(address);

        String yearStr = Integer.toString(year);
        String monthStr = Integer.toString(month);
        String dayStr = Integer.toString(day);
        String hourStr = Integer.toString(hour);
        String minuteStr = Integer.toString(minute);

        String dateTimeString = yearStr + "年" + monthStr + "月" + dayStr + "日"
                + hourStr + "时"
                + minuteStr + "分";
        dateTimeEditText.setText(dateTimeString);

        yearEditText.setText(yearStr);
        monthEditText.setText(monthStr);
        dayEditText.setText(dayStr);

        getUserInfo(userID);
        ticketTitleTextView.setText(policeCity + getString(R.string.ticket_title));
        ticketDespTextView.setText(getString(R.string.ticket_description1) + policeDept + getString(R.string.ticket_description2));
        ticketPoliceEditText.setText(policeName);

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

        if (licenseColor != null) {
            switch (licenseColor) {
                case "黄":
                    licenseColorRadioGroup.check(R.id.radiobutton_yellow);
                    break;
                case "蓝":
                    licenseColorRadioGroup.check(R.id.radiobutton_blue);
                    break;
                case "黑":
                    licenseColorRadioGroup.check(R.id.radiobutton_black);
                    break;
                default:
                    licenseColorRadioGroup.check(R.id.radiobutton_other_license_color);
            }
        }

        ViewTreeObserver mapVTO = mapDetailsImageView.getViewTreeObserver();
        mapVTO.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mapDetailsImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                String filePath = mapFilePath;
                if (filePath != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(filePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
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
                String filePath = farImgFilePath;
                if (filePath != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(filePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
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
                String filePath = closeImgFilePath;
                if (filePath != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(filePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
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

        ViewTreeObserver photo3VTO = photo3DetailsImageView.getViewTreeObserver();
        photo3VTO.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                photo3DetailsImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                String filePath = ticketImgFilePath;
                if (filePath != null) {
                    Bitmap bitmap = BitmapUtil.getScaledBitmap(filePath, TRANS_IMAGE_W, TRANS_IMAGE_H);
                    if (bitmap != null) {
                        int finalWidth = photo3DetailsImageView.getMeasuredWidth();
                        int finalHeight = photo3DetailsImageView.getMeasuredHeight();
                        int finalSize = finalWidth > finalHeight ? finalWidth : finalHeight;
                        BitmapUtil.setBitmapToImageView(photo3DetailsImageView, finalSize, finalSize, bitmap);
                    }
                }
                return true;
            }
        });


        vehicleTypeRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (bigBusRadioButton.isChecked()) {
                    vehicleTypeRadioGroup2.clearCheck();
                }
                if (smallBusRadioButton.isChecked()) {
                    vehicleTypeRadioGroup2.clearCheck();
                }
                if (bigTruckRadioButton.isChecked()) {
                    vehicleTypeRadioGroup2.clearCheck();
                }
            }
        });
        vehicleTypeRadioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (smallTruckRadioButton.isChecked()) {
                    vehicleTypeRadioGroup1.clearCheck();
                }
                if (motorCycleRadioButton.isChecked()) {
                    vehicleTypeRadioGroup1.clearCheck();
                }
                if (otherCarTypeRadioButton.isChecked()) {
                    vehicleTypeRadioGroup1.clearCheck();
                }
            }
        });

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

    @Override
    public void onDestroy() {
        mChannel.shutdown();
        super.onDestroy();
    }
}
