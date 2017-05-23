package io.chizi.tickethare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import io.chizi.tickethare.login.LoginActivity;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class SplashScreen extends Activity {
    private static int SPLASH_SCREEN_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
//                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);

                // Kills this Activity
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }

}
