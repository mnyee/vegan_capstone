package com.caucap2021_1_2_10.ddubuk2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.Button;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.util.Log; //코드 흐름을 확인하기 위함



public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "abcd";
    EditText inputPhoneNum;
    Button sendSMSBt;

    EditText inputCheckNum;
    Button checkBt;

    String SMSContents = "1234";

    static final int SMS_SEND_PERMISSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhoneNum = findViewById(R.id.input_phone_num);
        sendSMSBt = findViewById(R.id.send_sms_button);

        inputCheckNum = findViewById(R.id.input_check_num);
        checkBt = findViewById(R.id.check_button);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.d(TAG, "=== sms전송을 위한 퍼미션 확인 ===" );

            // For device above MarshMallow
            boolean permission = getWritePermission();
            if(permission) {
                // If permission Already Granted
                // Send You SMS here
                Log.d(TAG, "=== 퍼미션 허용 ===" );
            }
        }
        else{
            // Send Your SMS. You don't need Run time permission
            Log.d(TAG, "=== 퍼미션 필요 없는 버전임 ===" );
        }




    }

    public boolean getWritePermission(){
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 10);
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 10: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is Granted
                    // Send Your SMS here
                }
            }
        }
    }




}