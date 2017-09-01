package io.chizi.tickethare.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import io.chizi.ticket.PullTicketsRequest;
import io.chizi.ticket.TicketDetails;
import io.chizi.ticket.TicketGrpc;
import io.chizi.tickethare.R;
import io.chizi.tickethare.util.DateUtil;
import io.chizi.tickethare.util.GrpcRunnable;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import static io.chizi.tickethare.database.DBProvider.KEY_ADDRESS;
import static io.chizi.tickethare.database.DBProvider.KEY_CAR_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_CAR_TYPE;
import static io.chizi.tickethare.database.DBProvider.KEY_CLOSE_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_DATETIME;
import static io.chizi.tickethare.database.DBProvider.KEY_DAY;
import static io.chizi.tickethare.database.DBProvider.KEY_FAR_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_HOUR;
import static io.chizi.tickethare.database.DBProvider.KEY_IS_UPLOADED;
import static io.chizi.tickethare.database.DBProvider.KEY_LATITUDE;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_COLOR;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_NUM;
import static io.chizi.tickethare.database.DBProvider.KEY_LONGITUDE;
import static io.chizi.tickethare.database.DBProvider.KEY_MAP_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_MINUTE;
import static io.chizi.tickethare.database.DBProvider.KEY_MONTH;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_IMG_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_TIME_MILIS;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_WEEK;
import static io.chizi.tickethare.database.DBProvider.KEY_YEAR;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.CLOSE_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.FAR_IMG_FILE_PATH;
import static io.chizi.tickethare.util.AppConstants.FAR_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.HOST_IP;
import static io.chizi.tickethare.util.AppConstants.JPEG_FILE_SUFFIX;
import static io.chizi.tickethare.util.AppConstants.MAP_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PORT;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.TICKET_IMG_FILE_PREFIX;
import static io.chizi.tickethare.util.FileUtil.writeByteStringToFile;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class DatabaseFragment extends Fragment {
    private ManagedChannel mChannel;

    private String userID;
    private long lastTimeMilis = 0;
    private SimpleDateFormat dateFormatf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private int updateCount = 0;

    private String mapFilePath;
    private String farImgFilePath;
    private String closeImgFilePath;
    private String ticketImgFilePath;
    // Database
    private ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            userID = savedInstanceState.getString(SAVED_INSTANCE_USER_ID);
        }
        Intent intentFrom = getActivity().getIntent(); // Get the Intent that called for this Activity to open
        userID = intentFrom.getExtras().getString(POLICE_USER_ID); // Get the data that was sent

        mChannel = ManagedChannelBuilder.forAddress(HOST_IP, PORT).usePlaintext(true).build();
        resolver = getActivity().getContentResolver();

        new GeneralGrpcTask(new PullTicketsRunnable()).execute();

        TitlesFragment titlesFragment = (TitlesFragment)
                getFragmentManager().findFragmentById(R.id.titles);
        if (titlesFragment == null) {
            titlesFragment = new TitlesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.titles, titlesFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    private class PullTicketsRunnable implements GrpcRunnable {
        @Override
        public String run(TicketGrpc.TicketBlockingStub blockingStub, TicketGrpc.TicketStub asyncStub)
                throws Exception {
            return pullTickets(blockingStub);
        }

        private String pullTickets(TicketGrpc.TicketBlockingStub blockingStub) throws StatusRuntimeException {
            Calendar now = Calendar.getInstance();
            PullTicketsRequest request = PullTicketsRequest.newBuilder()
                    .setSid(userID)
                    .setYear(now.get(Calendar.YEAR))
                    .setWeek(now.get(Calendar.WEEK_OF_YEAR))
                    .setLastTime(lastTimeMilis)
                    .build();
            Iterator<TicketDetails> hareProfiles;
            hareProfiles = blockingStub.pullTickets(request);

            updateCount = 0;
            while (hareProfiles.hasNext()) {
                TicketDetails reply = hareProfiles.next();
                // Insert the value into the Content Provider
                ContentValues values = new ContentValues();

                long ticketID = reply.getTicketId();
                values.put(KEY_TICKET_ID, ticketID);
                values.put(KEY_USER_ID, reply.getUserId());
                values.put(KEY_LICENSE_NUM, reply.getLicenseNum());
                values.put(KEY_LICENSE_COLOR, reply.getLicenseColor());

                int year = reply.getYear();
                int month = reply.getMonth();
                int day = reply.getDay();
                int hour = reply.getHour();
                int minute = reply.getMinute();
                values.put(KEY_DATETIME, dateFormatf.format(DateUtil.getDate(year, month, day, hour, minute)));
                values.put(KEY_YEAR, year);
                values.put(KEY_MONTH, month);
                values.put(KEY_WEEK, reply.getWeek());
                values.put(KEY_DAY, day);
                values.put(KEY_HOUR, hour);
                values.put(KEY_MINUTE, minute);
                long timeMils = reply.getTicketTime();
                values.put(KEY_TIME_MILIS, timeMils);
                values.put(KEY_CAR_TYPE, reply.getVehicleType());
                values.put(KEY_CAR_COLOR, reply.getVehicleColor());
                values.put(KEY_ADDRESS, reply.getAddress());
                values.put(KEY_LONGITUDE, reply.getLongitude());
                values.put(KEY_LATITUDE, reply.getLatitude());
                String timeMilsStr = Long.toString(timeMils);
//                String mapFilePath = writeByteStringToFile(getActivity(),
                        mapFilePath = writeByteStringToFile(getActivity(),
                        timeMilsStr + MAP_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX,
                        reply.getMapImage());
                values.put(KEY_MAP_URI, mapFilePath);

//                String farImgFilePath = writeByteStringToFile(getActivity(),
                        farImgFilePath = writeByteStringToFile(getActivity(),
                        timeMilsStr + FAR_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX,
                        reply.getFarImage());
                values.put(KEY_FAR_IMG_URI, farImgFilePath);

//                String closeImgFilePath = writeByteStringToFile(getActivity(),
                        closeImgFilePath = writeByteStringToFile(getActivity(),
                        timeMilsStr + CLOSE_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX,
                        reply.getCloseImage());
                values.put(KEY_CLOSE_IMG_URI, closeImgFilePath);

//                String ticketImgFilePath = writeByteStringToFile(getActivity(),
                        ticketImgFilePath = writeByteStringToFile(getActivity(),
                        timeMilsStr + TICKET_IMG_FILE_PREFIX, JPEG_FILE_SUFFIX,
                        reply.getTicketImage());
                values.put(KEY_TICKET_IMG_URI, ticketImgFilePath);
                values.put(KEY_IS_UPLOADED, 1);

                resolver.insert(TICKET_URL, values);
                updateCount++;
            }

            return "Pull tickets success!";
        }
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

    private void getTicketLastTime() {
        String[] projection = new String[]{"MAX(" + KEY_TIME_MILIS + ") AS last_time"};
        Cursor cursor = resolver.query(TICKET_URL, projection, KEY_USER_ID + "=?", new String[]{userID}, null);
        lastTimeMilis = 0;
        if (cursor.moveToFirst()) {
            lastTimeMilis = cursor.getLong(cursor.getColumnIndex("last_time"));
//            Toast.makeText(getActivity(), "lastTimeMilis from SQLite = " + Long.toString(lastTimeMilis), Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getActivity(), "无罚单存在本机", Toast.LENGTH_SHORT).show();
        }
        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {
        }
    }

    private class GeneralGrpcTask extends AsyncTask<Void, Void, String> {
        private final GrpcRunnable mGrpc;

        GeneralGrpcTask(GrpcRunnable grpc) {
            this.mGrpc = grpc;
        }

        @Override
        protected void onPreExecute() {
            getTicketLastTime();
        }

        @Override
        protected String doInBackground(Void... nothing) {
            try {
                String logs = mGrpc.run(TicketGrpc.newBlockingStub(mChannel),
                        TicketGrpc.newStub(mChannel));
                return "Success!" + System.getProperty("line.separator") + logs;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return "Failed... : " + System.getProperty("line.separator") + sw;
            }
        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(getActivity(), "Updated count = " + Integer.toString(updateCount), Toast.LENGTH_SHORT).show();
            if (updateCount > 0) {
                refreshTitlesFragment();
//                Toast.makeText(getActivity(), mapFilePath, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), farImgFilePath, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), closeImgFilePath, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), ticketImgFilePath, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_USER_ID, userID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mChannel.shutdown();
        super.onDestroy();
    }
}
