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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.chizi.ticket.AccountReply;
import io.chizi.ticket.AccountRequest;
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

public class SignupActivity extends AppCompatActivity {

    private static final String LOG_TAG = SignupActivity.class.getName();

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
    private EditText rePasswordEditText;
    private EditText policeNameEditText;
    private Spinner policeTypeSpinner;
    private Spinner policeCitySpinner;
    private EditText policeDeptEditText;
    private EditText policeStationEditText;

    private Button signupButton;
    private TextView loginLink;
    private ProgressDialog progressDialog;


    ContentResolver resolver; // Provides access to other applications Content Providers

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userIDEditText = (EditText) findViewById(R.id.user_id);
        passwordEditText = (EditText) findViewById(R.id.input_password);
        rePasswordEditText = (EditText) findViewById(R.id.input_reEnterPassword);
        policeNameEditText = (EditText) findViewById(R.id.input_name);
        policeTypeSpinner = (Spinner) findViewById(R.id.spinner_police_type);
        policeCitySpinner = (Spinner) findViewById(R.id.spinner_police_city);
        policeDeptEditText = (EditText) findViewById(R.id.police_dept);
        policeStationEditText = (EditText) findViewById(R.id.police_station);

        resolver = getContentResolver();

        signupButton = (Button) findViewById(R.id.btn_update_profile);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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

    public void signup() {
        Log.d(LOG_TAG, "Signup");
        userID = userIDEditText.getText().toString();
        password = passwordEditText.getText().toString();
        reEnterPassword = rePasswordEditText.getText().toString();
        policeName = policeNameEditText.getText().toString();
        policeType = policeTypeSpinner.getSelectedItem().toString();
        policeCity = policeCitySpinner.getSelectedItem().toString();
        policeDept = policeDeptEditText.getText().toString();
        policeStation = policeStationEditText.getText().toString();
        if (!validate()) {
            return;
        }
        signupButton.setEnabled(false);
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
                AccountRequest accountRequest = AccountRequest.newBuilder()
                        .setUserId(userID)
                        .setPassword(password)
                        .setPoliceName(policeName)
                        .setPoliceType(policeType)
                        .setPoliceCity(policeCity)
                        .setPoliceDept(policeDept)
                        .setPoliceStation(policeStation)
                        .build();
                AccountReply reply = blockingStub.createAccount(accountRequest);
                resultList.add(String.valueOf(reply.getCreateSuccess()));
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
                String createSuccess = resultList.get(0);
                if (createSuccess != null && createSuccess.equals("true")) {
                    onSignupSuccess();
                } else {
                    onSignupFailed();
                }
            } else {
                onSignupFailed();
            }
        }
    }

    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), R.string.toast_signup_success, Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userID);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_POLICE_NAME, policeName);
        values.put(KEY_POLICE_TYPE, policeType);
        values.put(KEY_POLICE_CITY, policeCity);
        values.put(KEY_POLICE_DEPT, policeDept);
        values.put(KEY_POLICE_STATION, policeStation);
        resolver.insert(DBProvider.POLICE_URL, values);

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.putExtra(POLICE_USER_ID, userID);
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.toast_signup_failed, Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
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
        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_repassword, Toast.LENGTH_LONG).show();
            rePasswordEditText.setError(getString(R.string.request_wrong_repassword));
            return false;
        } else {
            rePasswordEditText.setError(null);
        }
        if (policeName.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_police_name, Toast.LENGTH_LONG).show();
            policeNameEditText.setError(getString(R.string.request_wrong_police_name));
            return false;
        } else {
            policeNameEditText.setError(null);
        }
        if (policeDept.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_police_dept, Toast.LENGTH_LONG).show();
            policeDeptEditText.setError(getString(R.string.request_wrong_police_dept));
            return false;
        } else {
            policeDeptEditText.setError(null);
        }
        if (policeStation.isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.toast_wrong_police_station, Toast.LENGTH_LONG).show();
            policeStationEditText.setError(getString(R.string.request_wrong_police_station));
            return false;
        } else {
            policeStationEditText.setError(null);
        }
        return true;
    }

    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progress_dialog_creating_account));
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
        progressDialog = null;
    }

}
