package io.chizi.tickethare;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;


import java.io.File;

import io.chizi.tickethare.acquire.AcquireFragment;
import io.chizi.tickethare.pager.MyFragmentPagerAdapter;
import io.chizi.tickethare.pager.SlidingTabLayout;
import io.chizi.tickethare.util.FileUtil;

import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_RANGE_END;
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_RANGE_START;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.RANGE_URL;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.REQUEST_PERMISSIONS;


/**
 * Created by Jiangchuan on 9/4/16.
 */

//public class MainActivity extends FragmentActivity {
public class MainActivity extends RuntimePermissionsActivity {
    // Database
    private ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolver = getContentResolver();

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
        super.onBackPressed();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_permission_received), Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onDestroy() {
//        if(!isChangingConfigurations()) {
//            FileUtil.deleteTempFiles(getExternalFilesDir(null));
//            clearTickets();
//        }
//        super.onDestroy();
//    }
}
