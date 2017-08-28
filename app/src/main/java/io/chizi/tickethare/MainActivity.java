package io.chizi.tickethare;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;


import com.google.protobuf.ByteString;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.chizi.ticket.LoginReply;
import io.chizi.ticket.LoginRequest;
import io.chizi.ticket.LogoutReply;
import io.chizi.ticket.LogoutRequest;
import io.chizi.ticket.TicketGrpc;
import io.chizi.tickethare.acquire.AcquireFragment;
import io.chizi.tickethare.login.LoginActivity;
import io.chizi.tickethare.pager.MyFragmentPagerAdapter;
import io.chizi.tickethare.pager.SlidingTabLayout;
import io.chizi.tickethare.util.FileUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_RANGE_END;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_RANGE_START;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.RANGE_URL;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.HOST_IP;
import static io.chizi.tickethare.util.AppConstants.JPEG_FILE_SUFFIX;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PORT;
import static io.chizi.tickethare.util.AppConstants.REQUEST_PERMISSIONS;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_USER_ID;


/**
 * Created by Jiangchuan on 9/4/16.
 */

//public class MainActivity extends FragmentActivity {
public class MainActivity extends RuntimePermissionsActivity {
    private String userID;

    // Database
    private ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            userID = savedInstanceState.getString(SAVED_INSTANCE_USER_ID);
        }

        resolver = getContentResolver();

        Intent intentFrom = getIntent(); // Get the Intent that called for this Activity to open
        userID = intentFrom.getExtras().getString(POLICE_USER_ID); // Get the data that was sent

        // Layout manager that allows the user to flip through the pages
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // getSupportFragmentManager allows use to interact with the fragments
        // MyFragmentPagerAdapter will return a fragment based on an index that is passed
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Initialize the Sliding Tab Layout
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        // Connect the viewPager with the sliding tab layout
        slidingTabLayout.setViewPager(viewPager);

        MainActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                }, R.string
                        .snackbar_permission_text
                , REQUEST_PERMISSIONS);

    }

    private void clearTickets() {
        resolver.delete(TICKET_URL, null, null);
    }

    @Override
    public void onBackPressed() {
        FileUtil.deleteTempFiles(getExternalFilesDir(null));
        clearTickets();
        new LogoutGrpcTask().execute();
        super.onBackPressed();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_permission_received), Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onPause() {
//        if(isFinishing()) {
//            FileUtil.deleteTempFiles(getExternalFilesDir(null));
//            clearTickets();
//        }
//        super.onPause();
//    }

        @Override
    protected void onDestroy() {
        if(!isChangingConfigurations()) {
            FileUtil.deleteTempFiles(getExternalFilesDir(null));
            clearTickets();
            new LogoutGrpcTask().execute();
        }
        super.onDestroy();
    }

    private class LogoutGrpcTask extends AsyncTask<Void, Void, List<String>> {
        private ManagedChannel mChannel;

        @Override
        protected void onPreExecute() {
            mChannel = ManagedChannelBuilder.forAddress(HOST_IP, PORT)
                    .usePlaintext(true)
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... nothing) {
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                LogoutRequest request = LogoutRequest.newBuilder()
                        .setUserId(userID)
                        .build();
                LogoutReply reply = blockingStub.hareLogout(request);
                Boolean theSucess = reply.getLogoutSuccess();
                resultList.add(String.valueOf(theSucess));
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
            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_USER_ID, userID);

        super.onSaveInstanceState(outState);
    }
}
