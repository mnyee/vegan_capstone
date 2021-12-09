package com.caucap2021_1_2_10.lightroad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IntroActivity.this.startActivity(new Intent(IntroActivity.this, MainActivity.class));
                IntroActivity.this.finish();
            }
        }, 1000);

    }
}
