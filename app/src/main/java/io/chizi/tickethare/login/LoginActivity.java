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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.chizi.ticket.LoginReply;
import io.chizi.ticket.LoginRequest;
import io.chizi.ticket.TicketGrpc;
import io.chizi.tickethare.MainActivity;
import io.chizi.tickethare.R;
import io.chizi.tickethare.database.DBProvider;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.chizi.tickethare.database.DBProvider.KEY_PASSWORD;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_CITY;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_DEPT;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_NAME;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_STATION;
import static io.chizi.tickethare.database.DBProvider.KEY_POLICE_TYPE;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.util.AppConstants.HOST_IP;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.PORT;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();

    private String userID;
    private String password;
    private String reEnterPassword;
    private String policeName;
    private String policeType;
    private String policeCity;
    private String policeDept;
    private String policeStation;

    private EditText userIDEditText;
    private EditText passwordEditText;

    private Button loginButton;
    private TextView signupLink;
    private ProgressDialog progressDialog;

    ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIDEditText = (EditText) findViewById(R.id.user_id);
        passwordEditText = (EditText) findViewById(R.id.input_password);

        resolver = getContentResolver();

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink = (TextView) findViewById(R.id.link_signup);
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
                startActivity(intent);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(LOG_TAG, "Login");
        userID = userIDEditText.getText().toString();
        password = passwordEditText.getText().toString();
        if (!validate()) {
            return;
        }

        loginButton.setEnabled(false);
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
                LoginRequest loginRequest = LoginRequest.newBuilder().setUserId(userID).setPassword(password).build();
                LoginReply reply = blockingStub.login(loginRequest);
                resultList.add(String.valueOf(reply.getLoginSuccess()));
                resultList.add(reply.getPoliceName());
                resultList.add(reply.getPoliceType());
                resultList.add(reply.getPoliceCity());
                resultList.add(reply.getPoliceDept());
                resultList.add(reply.getPoliceStation());
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
                String loginSuccess = resultList.get(0);
                if (loginSuccess != null && loginSuccess.equals("true")) {
                    policeName = resultList.get(1);
                    policeType = resultList.get(2);
                    policeCity = resultList.get(3);
                    policeDept = resultList.get(4);
                    policeStation = resultList.get(5);
                    onLoginSuccess();
                } else {
                    onLoginFailed();
                }
            } else {
                onLoginFailed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), R.string.toast_login_success, Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userID);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_POLICE_NAME, policeName);
        values.put(KEY_POLICE_TYPE, policeType);
        values.put(KEY_POLICE_CITY, policeCity);
        values.put(KEY_POLICE_DEPT, policeDept);
        values.put(KEY_POLICE_STATION, policeStation);
        resolver.insert(DBProvider.POLICE_URL, values);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(POLICE_USER_ID, userID);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.toast_login_failed, Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
//        if (userID.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userID).matches()) {
        if (userID.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_userid, Toast.LENGTH_LONG).show();
            userIDEditText.setError(getString(R.string.request_wrong_userid));
            return false;
        } else {
            userIDEditText.setError(null);
        }

//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
        if (password.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_password, Toast.LENGTH_LONG).show();
            passwordEditText.setError(getString(R.string.request_wrong_password));
            return false;
        } else {
            passwordEditText.setError(null);
        }

        return true;
    }

    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progress_dialog_logining));
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
        progressDialog = null;
    }
}
