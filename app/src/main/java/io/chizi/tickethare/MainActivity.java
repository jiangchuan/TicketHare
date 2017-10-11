package io.chizi.tickethare;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import io.chizi.ticket.LogoutReply;
import io.chizi.ticket.LogoutRequest;
import io.chizi.ticket.TicketGrpc;
import io.chizi.tickethare.pager.MyFragmentPagerAdapter;
import io.chizi.tickethare.pager.SlidingTabLayout;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PORT;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_USER_ID;


/**
 * Created by Jiangchuan on 9/4/16.
 */

//public class MainActivity extends FragmentActivity {
public class MainActivity extends AppCompatActivity {
    private ManagedChannel mChannel;

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
        Intent intentFrom = getIntent(); // Get the Intent that called for this Activity to open
        userID = intentFrom.getExtras().getString(POLICE_USER_ID); // Get the data that was sent
//        String ipAddress = intentFrom.getExtras().getString(SET_IP_ADDRESS);
//        if (ipAddress == null || ipAddress.isEmpty()) {
//            ipAddress = HOST_IP;
//        }
        String ipAddress = ((TicketApplication) getApplication()).getIpAddress();
        mChannel = ManagedChannelBuilder.forAddress(ipAddress, PORT).usePlaintext(true).build();
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
    }

    private void clearTickets() {
        resolver.delete(TICKET_URL, null, null);
    }

    @Override
    public void onBackPressed() {
        showExitCheckDialog();
    }

    private void showExitCheckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.alert_dialog_exit_confirm));
        builder.setCancelable(false)
                .setPositiveButton(R.string.alert_dialog_check_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        FileUtil.deleteTempFiles(getExternalFilesDir(null));
//                        clearTickets();
                        new LogoutGrpcTask().execute();
                        MainActivity.super.onBackPressed();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_check_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private class LogoutGrpcTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
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
                boolean theSucess = reply.getLogoutSuccess();
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
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_USER_ID, userID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
//            FileUtil.deleteTempFiles(getExternalFilesDir(null));
//            clearTickets();
            new LogoutGrpcTask().execute();
        }
        mChannel.shutdown();
        super.onDestroy();
    }

}


//
//    private static final String TAG = "MRCar";
//    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 200;
//    private static final String sdcarddir = "/sdcard/" + MRCarUtil.ApplicationDir;
//    private Bitmap bmp;
//    private Bitmap Originbitmap = bmp;
//    private ImageView im;
//    private ImageButton buttonCamera;
//    private ImageButton buttonFolder;
//    private EditText et;
//    private boolean b2Recognition = true;
//    private Uri fileUri;
//    private static String filePath = null;
//
//    private class plateTask extends AsyncTask<String, Integer, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            Mat m = new Mat();
//            Utils.bitmapToMat(bmp, m);
//            try {
//                String license = MRCarUtil.plateRecognition(m.getNativeObjAddr(), m.getNativeObjAddr());
//                Utils.matToBitmap(m, bmp);
//                Message msg = new Message();
//                Bundle b = new Bundle();
//                b.putString("license", license);
//                b.putParcelable("bitmap", bmp);
//                msg.what = 1;
//                msg.setData(b);
//                mHandler.sendMessage(msg);
//            } catch (Exception e) {
//                Log.d(TAG, "exception occured!");
//            }
//            return null;
//        }
//    }
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    Log.i(TAG, "OpenCV loaded successfully");
//                    System.loadLibrary("mrcar");
//                    new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//                            initFile();
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            super.onPostExecute(aVoid);
//                            if (filePath != null) {
//                                bmp = BitmapFactory.decodeFile(filePath);
//                            } else {
//                                bmp = BitmapFactory.decodeFile(sdcarddir + "/" + MRCarUtil.initimgPath);
//                            }
//                            im.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    im.setImageBitmap(bmp);
//                                }
//                            }, 10);
//                            Originbitmap = bmp;
//                            if (bmp != null)
//                                new plateTask().execute();
//                        }
//                    }.execute();
//                }
//                break;
//                default: {
//                    super.onManagerConnected(status);
//                }
//                break;
//            }
//        }
//    };
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_main);
//        im = (ImageView) findViewById(R.id.imageView);
//        et = (EditText) findViewById(R.id.editText);
//        buttonCamera = (ImageButton) findViewById(R.id.buttonCamera);
//        buttonFolder = (ImageButton) findViewById(R.id.buttonFolder);
//        buttonCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                fileUri = MRMediaFileUtil.getOutputMediaFileUri(1);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//            }
//        });
//        buttonFolder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                intent.setType("image/*" );
//                startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
//            }
//        });
//        im.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (b2Recognition) {
//                    if (bmp != null)
//                        new plateTask().execute();
//                } else {
//                    bmp = Originbitmap;
//                    im.setImageBitmap(bmp);
//                    et.setText("");
//                }
//                b2Recognition = !b2Recognition;
//            }
//        });
//    }
//
//    public static Bitmap loadBitmap(ImageView im, String filepath) {
//        int width = im.getWidth();
//        int height = im.getHeight();
//        BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
//        factoryOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filepath, factoryOptions);
//        int imageWidth = factoryOptions.outWidth;
//        int imageHeight = factoryOptions.outHeight;
//        int scaleFactor = Math.min(imageWidth / width, imageHeight / height);
//        factoryOptions.inJustDecodeBounds = false;
//        factoryOptions.inSampleSize = scaleFactor;
//        factoryOptions.inPurgeable = true;
//        Bitmap bmp = BitmapFactory.decodeFile(filepath, factoryOptions);
//        im.setImageBitmap(bmp);
//        return bmp;
//    }
//
//    public void loadAndShowBitmap() {
//        bmp = loadBitmap(im, filePath);
//        Originbitmap = bmp;
//        et.setText("");
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
//            if (RESULT_OK == resultCode) {
//                if (data != null) {
//                    if (data.hasExtra("data")) {
//                        Bitmap thumbnail = data.getParcelableExtra("data");
//                        im.setImageBitmap(thumbnail);
//                    }
//                } else {
//                    filePath = fileUri.getPath();
//                }
//            }
//        } else if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
//            fileUri = data.getData();
//            filePath = MRMediaFileUtil.getPath(this, fileUri);
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    public Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    Bundle b = msg.getData();
//                    String str = b.getString("license");
//                    et.setText(b.getString("license"));
//                    im.setImageBitmap((Bitmap) b.getParcelable("bitmap"));
//                    break;
//                default:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void initFile() {
//        MRAssetUtil.CopyAssets(this, MRCarUtil.ApplicationDir, sdcarddir);
//    }
//
//    private void CopyOneFile(String filename) {
//        MRAssetUtil.CopyOneFile(filename, sdcarddir, getResources());
//    }
