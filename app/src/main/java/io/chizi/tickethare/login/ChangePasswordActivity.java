package io.chizi.tickethare.login;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.chizi.ticket.LoginReply;
import io.chizi.ticket.PasswordRequest;
import io.chizi.ticket.TicketGrpc;
import io.chizi.tickethare.MainActivity;
import io.chizi.tickethare.R;
import io.chizi.tickethare.database.DBProvider;
import io.chizi.tickethare.util.FileUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.chizi.tickethare.database.DBProvider.KEY_PASSWORD;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_CITY;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_DEPT;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_NAME;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_PORTRAIT_URI;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_SECTION;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_SQUAD;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_TYPE;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.util.AppConstants.HOST_IP;
import static io.chizi.tickethare.util.AppConstants.JPEG_FILE_SUFFIX;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PORT;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class ChangePasswordActivity extends AppCompatActivity {
    private static String PACKAGE_NAME;
    private static final String LOG_TAG = ChangePasswordActivity.class.getName();

    private String userID;
    private String password;
    private String newPassword;
    private String reNewPassword;

    private String policeName;
    private String policeType;
    private String policeCity;
    private String policeDept;
    private String policeSquad;
    private String policeSection;
    private String policePortraitPath;


    private EditText userIDEditText;
    private EditText passwordEditText;
    private EditText newPasswordEditText;
    private EditText reNewPasswordEditText;

    private Button changePassButton;
    private TextView loginLink;
    private ProgressDialog progressDialog;


    ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        userIDEditText = (EditText) findViewById(R.id.user_id);
        passwordEditText = (EditText) findViewById(R.id.edittext_password);
        newPasswordEditText = (EditText) findViewById(R.id.edittext_new_password);
        reNewPasswordEditText = (EditText) findViewById(R.id.edittext_re_new_password);

        resolver = getContentResolver();

        changePassButton = (Button) findViewById(R.id.btn_change_password);
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        loginLink = (TextView) findViewById(R.id.link_login);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    public void changePassword() {
        Log.d(LOG_TAG, "ChangePassword");
        userID = userIDEditText.getText().toString();
        password = passwordEditText.getText().toString();
        newPassword = newPasswordEditText.getText().toString();
        reNewPassword = reNewPasswordEditText.getText().toString();
        if (!validate()) {
            return;
        }
        changePassButton.setEnabled(false);
        new GrpcTask().execute();
    }


    private class GrpcTask extends AsyncTask<Void, Void, List<String>> {
        private ManagedChannel mChannel;

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                prepareProgressDialog();
            }
            mChannel = ManagedChannelBuilder.forAddress(HOST_IP, PORT)
                    .usePlaintext(true)
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... nothing) {
            if (progressDialog == null) {
                prepareProgressDialog();
            }
            ArrayList<String> resultList = new ArrayList<String>();
            try {
                TicketGrpc.TicketBlockingStub blockingStub = TicketGrpc.newBlockingStub(mChannel);
                PasswordRequest request = PasswordRequest.newBuilder()
                        .setUserId(userID)
                        .setPassword(password)
                        .setNewPassword(newPassword)
                        .build();
                LoginReply reply = blockingStub.hareChangePassword(request);
                Boolean theSucess = reply.getLoginSuccess();
                resultList.add(String.valueOf(theSucess));
                resultList.add(reply.getPoliceName());
                resultList.add(reply.getPoliceType());
                resultList.add(reply.getPoliceCity());
                resultList.add(reply.getPoliceDept());
                resultList.add(reply.getPoliceSquad());
                resultList.add(reply.getPoliceSection());
                if (theSucess) {
                    ByteString portraitByteString = reply.getPolicePortrait();
                    byte[] portraitByte = new byte[portraitByteString.size()];
                    portraitByteString.copyTo(portraitByte, 0);
                    policePortraitPath = FileUtil.getStorageDir(ChangePasswordActivity.this) + "/" + userID + JPEG_FILE_SUFFIX;
                    FileUtil.writeFile(portraitByte, policePortraitPath);
                }

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
            dismissProgressDialog();

            if (resultList != null) {
                String mSuccess = resultList.get(0);
                if (mSuccess != null && mSuccess.equals("true")) {
                    policeName = resultList.get(1);
                    policeType = resultList.get(2);
                    policeCity = resultList.get(3);
                    policeDept = resultList.get(4);
                    policeSquad = resultList.get(5);
                    policeSection = resultList.get(6);
                    onChangePasswordSuccess();
                } else {
                    onChangePasswordFailed();
                }
            } else {
                onChangePasswordFailed();
            }
        }
    }

    public void onChangePasswordSuccess() {
        Toast.makeText(getBaseContext(), R.string.toast_change_password_success, Toast.LENGTH_LONG).show();
        changePassButton.setEnabled(true);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userID);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_POLICE_NAME, policeName);
        values.put(KEY_POLICE_TYPE, policeType);
        values.put(KEY_POLICE_CITY, policeCity);
        values.put(KEY_POLICE_DEPT, policeDept);
        values.put(KEY_POLICE_SQUAD, policeSquad);
        values.put(KEY_POLICE_SECTION, policeSection);
        values.put(KEY_POLICE_PORTRAIT_URI, policePortraitPath);
        resolver.insert(DBProvider.POLICE_URL, values);

        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
        intent.putExtra(POLICE_USER_ID, userID);
        startActivity(intent);
    }

    public void onChangePasswordFailed() {
        Toast.makeText(getBaseContext(), R.string.toast_change_password_failed, Toast.LENGTH_LONG).show();
        changePassButton.setEnabled(true);
    }

    public boolean validate() {
        if (userID.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_userid, Toast.LENGTH_LONG).show();
            userIDEditText.setError(getString(R.string.request_wrong_userid));
            return false;
        } else {
            userIDEditText.setError(null);
        }
        if (password.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_password, Toast.LENGTH_LONG).show();
            passwordEditText.setError(getString(R.string.request_wrong_password));
            return false;
        } else {
            passwordEditText.setError(null);
        }
        if (newPassword.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_new_password, Toast.LENGTH_LONG).show();
            newPasswordEditText.setError(getString(R.string.request_wrong_new_password));
            return false;
        } else {
            newPasswordEditText.setError(null);
        }
        if (reNewPassword.isEmpty() || !(reNewPassword.equals(newPassword))) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_repassword, Toast.LENGTH_LONG).show();
            reNewPasswordEditText.setError(getString(R.string.request_wrong_repassword));
            return false;
        } else {
            reNewPasswordEditText.setError(null);
        }
        return true;
    }

    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progress_dialog_creating_account));
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
        progressDialog = null;
    }

}
