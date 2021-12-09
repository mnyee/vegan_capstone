package com.caucap2021_1_2_10.lightroad;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {


    // 센서
    private SensorManager sensorManager;
    private float light = (float) -1.0; // 광량 센서
    private float proximity = (float) -1.0; // 근접 센서

    private long pathNum = 0;

    private String servername="";

    // 위치
    protected LocationManager locationManager;
    Location location;
    private double latitude; // 휴대폰위치 위도가 저장됨
    private double longitude; // 휴대폰위치 경도가 저장됨

    // 지도
    private GoogleMap mMap; // 구글맵
    private double cameraLatitude; // 휴대폰위치가 아닌 지도가 가리키는 위치
    private double cameraLongitude; // 휴대폰위치가 아닌 지도가 가리키는 위치
    private double cameraZoom; // 지도의 줌 정도
    private boolean isMapReady; // 맵이 준비되었는지 확인

    private boolean isDrawerOpen = false;
    private String DrawerSort="";


    // 앱 상에서 지도의 크기
    private double mapDpWidth;
    private double mapDpHeight;


    // 소리내기 기능에 필요
    private SoundPool bellSound;
    private int soundId = -1;
    private int streamId = -1;
    AudioManager audioManager;


    final Geocoder geocoder = new Geocoder(this); // 검색에 필요한 지오코더

    // 위치센서 재인식 조건설정
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000;

    // 현재 페이지 상태. 페이지에 해당
    private ArrayList<PageState> pageQueue;


    // 권한 설정에 해당
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_LOCATION_REQUEST_CODE = 100;
    int PERMISSIONS_READ_PHONE_STATE = 101;
    String[] REQUIRED_LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    // 휴대전화 고유의 문자열 가져오기
    private String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "x";
        }
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }


    // 수집테스트 색상조건
    boolean colorCounter = true;
    SharedPreferences sharedPref = null;



    public void sharedSave(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String sharedLoad(String key){
        return sharedPref.getString(key,"");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 레이아웃 설정
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        servername = getString(R.string.server_name);

        // 크기 가져오기
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;

        sharedPref = getPreferences(Context.MODE_PRIVATE);



        // 위치
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);
                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    if (isNetworkEnabled) {
                        LocationListener ll = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                for(int i = 0; i< pageQueue.size(); i++){
                                    pageQueue.get(i).locationMove(i+1== pageQueue.size());
                                }
                                if(toggleButtonState[4]) {
                                    findViewById(R.id.imageView2).setBackgroundColor(colorCounter?Color.argb(255,0,0,0): Color.argb(255,50,50,5));
                                    colorCounter = !colorCounter;
                                    measureLightData();
                                }
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {
                            }

                            @Override
                            public void onProviderEnabled(String s) {
                            }

                            @Override
                            public void onProviderDisabled(String s) {
                            }
                        };
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, ll);
                        if (locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                else
                    location = null;
            }
        }
        catch (Exception e) {
        }

        // 권한
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkLocationPermission();
        }
        if (!checkPhoneStateStatus()) {
            showDialogForPhoneStateServiceSetting();
        }else {
            checkPhoneStatePermission();
        }

        // 센서
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener mListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] v = event.values;
                switch(event.sensor.getType()) {
                    case Sensor.TYPE_LIGHT:
                        light = v[0];
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        proximity = v[0];
                        break;
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            sensorManager.registerListener(mListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null)
            sensorManager.registerListener(mListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                    SensorManager.SENSOR_DELAY_FASTEST);


        // 지도 붙이기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 잠깐 메뉴화면을 눌러도 지도를 유지시키도록 하기
        if (savedInstanceState != null) {
            cameraLatitude = savedInstanceState.getDouble("KEY_LOCATION_LATI");
            cameraLongitude = savedInstanceState.getDouble("KEY_LOCATION_LONGI");
            cameraZoom = savedInstanceState.getDouble("KEY_CAMERA_ZOOM");
            //stateList = (ArrayList<PageState>) savedInstanceState.getSerializable("KEY_WORKING_STATE");
        }
        else{
            getLocation();
            cameraLatitude = latitude;
            cameraLongitude = longitude;
            cameraZoom = 16.5;
            pageQueue = new ArrayList<PageState>();

        }

        // 버튼 클릭할 때 동작은 이 함수에서 정의
        setButtonClickListener();

        // 드로어 설정
        InitanimateDownDrawer();

        // 위치 검색
        ((EditText)findViewById(R.id.searchAddr)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final boolean isEnterEvent = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;

                final boolean isEnterUpEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_UP;
                final boolean isEnterDownEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_DOWN;

                if (actionId == EditorInfo.IME_ACTION_DONE || isEnterUpEvent) {
                    if(geoAddToLo(((EditText)findViewById(R.id.searchAddr)).getText().toString())) {
                        if(findViewById(R.id.AddrText)!=null) ((EditText)findViewById(R.id.AddrText)).setText(((EditText)findViewById(R.id.searchAddr)).getText().toString());
                        ((EditText)findViewById(R.id.searchAddr)).setText("");
                    }
                    else
                        Toast.makeText(MainActivity.this,"해당 주소는 찾을 수 없습니다",Toast.LENGTH_SHORT);

                }
                return false;
            }
        });
        ((EditText)findViewById(R.id.searchAddr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDownDrawer();
            }
        });

        // 플래시라이트 기능 초기설정
        initFlashlight();
        // 소리내기 기능 초기설정
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        bellSound = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        soundId = bellSound.load(this, R.raw.alm, 1);
    }

    // 잠깐 메뉴화면을 눌러도 지도를 유지시키도록 하기
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putDouble("KEY_CAMERA_ZOOM", cameraZoom);
            outState.putDouble("KEY_LOCATION_LATI", cameraLatitude);
            outState.putDouble("KEY_LOCATION_LONGI", cameraLongitude);
            //outState.putSerializable("KEY_WORKING_STATE",stateList);
        }
        super.onSaveInstanceState(outState);
    }

    // 플래시라이트

    private static CameraManager mCameraManager;
    private static ImageButton mImageButtonFlashOnOff;
    private static boolean mFlashOn = false;
    private boolean isSupportFlash = false;
    private  String mCameraId = null;
    void initFlashlight() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            isSupportFlash = true;

            mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

            if (mCameraId == null) {
                try {
                    for (String id : mCameraManager.getCameraIdList()) {
                        CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                        Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                        Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                        if (flashAvailable != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                            mCameraId = id;
                            break;
                        }
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void flashLight () {
        mFlashOn = toggleButtonState[5];
        try {
            mCameraManager.setTorchMode(mCameraId, mFlashOn);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 단일화면 앱에서 페이지 상태를 다루기
    abstract class PageState{ // 페이지 상태를 정의
        abstract public boolean enter(); // 페이지 생성/시작. true면 상태를 이 상태로 갱신, false면 상태 갱신 안 함
        public void locationMove(boolean isNow){}; // 휴대폰 위치 움직일 때, isNow는 현상태인지 아닌지에 대한 변수
        public void CameraMove(boolean isNow){}; // 지도 움직일 때, isNow는 현상태인지 아닌지에 대한 변수
        abstract public boolean exit(); // 페이지 종료될 때. true면 상태 제거, false면 제거 안함.
        public void reEnter(){}; // 앞의 페이지가 종료됨으로 의하여 이 페이지로 복귀될 때.
    }
    public void putState(PageState ps){
        if(ps.enter())
            pageQueue.add(ps);
    }
    // 현재 내 위치를 보여주는 상태
    class YourLocationState extends PageState{
        Marker YourMarker;
        long etime = 0L;
        @Override
        public boolean enter() {
            YourMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
            return true;
        }
        @Override
        public void locationMove(boolean isNow){
            YourMarker.setPosition(new LatLng(latitude, longitude));
            if(isNow) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            }
        }

        @Override
        public boolean exit() {
            if(System.currentTimeMillis()-etime < 3000) finish();
            else{
                etime = System.currentTimeMillis();
                Toast.makeText(MainActivity.this,"한 번 더 누르면 종료됩니다",Toast.LENGTH_SHORT).show();

            }
            return false;
        }

        @Override
        public void reEnter(){
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
    }

    // 득정 위치를 지정해서 보여줌.
    class FixedLocationPageState extends PageState{

        Marker FixedMarker;
        LatLng ll;

        FixedLocationPageState(LatLng ll){
            this.ll=ll;
        }

        @Override
        public boolean enter() {
            if(pageQueue.get(pageQueue.size()-1) instanceof FixedLocationPageState){
                ((FixedLocationPageState) pageQueue.get(pageQueue.size()-1)).changeLat(ll);
                return false;
            }
            FixedMarker = mMap.addMarker(new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.defaultMarker(120)));
            FixedMarker.setPosition(ll);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
            return true;
        }

        @Override
        public boolean exit() {
            FixedMarker.remove();
            return true;
        }

        public void changeLat(LatLng ll){
            FixedMarker.setPosition(ll);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
        }
    }

    // 주소 문자열을 받아서 FixedLocationPageState 상태로 만들어주는 함수
    public boolean geoAddToLo(String str){
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName
                    (str, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list != null) {
            if (list.size() == 0) {

            } else {

                Address addr = list.get(0);
                putState(new FixedLocationPageState(new LatLng(addr.getLatitude(), addr.getLongitude())));
                return true;
            }
        }
        Toast.makeText(MainActivity.this,"해당 주소를 찾을 수 없습니다",Toast.LENGTH_SHORT);
        return false;

    }

    // 야외인지 아닌지 측정. 아직 미완성.
    public int outdoorMeasure(){

        if(proximity<0) return 0;
        return 0;

    }


    // 위치정보 얻기
    public void getLocation(){
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        String address = getCurrentAddress(latitude, longitude);
    }

    // 현재 위치 문자열로 얻기
    public String getCurrentAddress( double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }


    // 권한설정
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grandResults) {
        if ( permsRequestCode == PERMISSIONS_LOCATION_REQUEST_CODE && grandResults.length == REQUIRED_LOCATION_PERMISSIONS.length) {
            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
            }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_LOCATION_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_LOCATION_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public boolean checkLocationServicesStatus () {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean checkPhoneStateStatus () {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED;
    }
    private void showDialogForPhoneStateServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("권한 허용");
        builder.setMessage("기기지정을 사용하기 위해서\n"
                + "권한을 허용해 주세요.");
        builder.setCancelable(true);
        builder.setPositiveButton("허용", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_READ_PHONE_STATE);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서\n"
                + "위치 서비스를 설정해 주세요.");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    void checkLocationPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_LOCATION_PERMISSIONS[0])) {
                Toast.makeText(MainActivity.this, "위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_LOCATION_PERMISSIONS,
                        PERMISSIONS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_LOCATION_PERMISSIONS,
                        PERMISSIONS_LOCATION_REQUEST_CODE);
            }
        }
    }

    void checkPhoneStatePermission(){
        int hasPhoneStatePermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE);

        if (hasPhoneStatePermission == PackageManager.PERMISSION_GRANTED){

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(MainActivity.this, "기기정보 열람 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_LOCATION_PERMISSIONS,
                        PERMISSIONS_READ_PHONE_STATE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_LOCATION_PERMISSIONS,
                        PERMISSIONS_READ_PHONE_STATE);
            }
        }
        int hasPhoneStatePermission2 = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE);

        if (hasPhoneStatePermission2 == PackageManager.PERMISSION_GRANTED){

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CALL_PHONE)) {
                Toast.makeText(MainActivity.this, "전화 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE}, 1020);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE}, 1020);
            }
        }
    }

    // 지도 초기설정
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng here= new LatLng(cameraLatitude, cameraLongitude);
        isMapReady = true;
        putState(new YourLocationState());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, (float) cameraZoom));

        mMap.setMinZoomPreference(16.0f);
        mMap.setMaxZoomPreference(18.0f);

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mLPty |= 1;

        mapLoad();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                putState(new FixedLocationPageState(latLng));
            }
        });

        //if(true){lightGatherTest();}
    }
    @Override
    public void onCameraMove() {
        Log.d("<><>","MOVE");

        LatLng nLL = mMap.getCameraPosition().target;
        float nZoom = mMap.getCameraPosition().zoom;
        findViewById(R.id.colorMap).setScaleX((float) pow(2.0,nZoom-cmsZoom));
        findViewById(R.id.colorMap).setScaleY((float) pow(2.0,nZoom-cmsZoom));
        findViewById(R.id.colorMap).setTranslationY((float)( 3.0*(nLL.latitude-cmsLL.latitude)/(0.00002138051*cos(toRadians(nLL.latitude))*65536.0/(pow(2.0,nZoom)))));
        findViewById(R.id.colorMap).setTranslationX((float) -( 3.0*(nLL.longitude-cmsLL.longitude)/(0.00002139714*65536.0/(pow(2.0,nZoom)))));
    }
    @Override
    public void onCameraIdle() {
        for(int i = 0; i< pageQueue.size(); i++){
            pageQueue.get(i).CameraMove(i+1== pageQueue.size());
        }
        mapLoad();
        Log.d("<><>","IDLE");

    }
    @Override
    public void onCameraMoveCanceled() {
        Log.d("<><>","CANCEL");
    }

    LatLng cmsLL = null;
    float cmsZoom;
    @Override
    public void onCameraMoveStarted(int i) {
        Log.d("<><>","START");
        if(cmsLL == null) {
            cmsLL = mMap.getCameraPosition().target;
            cmsZoom = mMap.getCameraPosition().zoom;
        }
    }

    private boolean isFocused = false;
    private int mLPty = 0; // 서로 다른 두 함수가 전부 실행되어야 mapLoad 기능을 문제없이 실행할 수 있기에, 그 함수가 실행될 때 패러티를 기록하는 용도
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!isFocused){
            isFocused = true;
            mLPty |= 2;
            float density = getResources().getDisplayMetrics().density;
            mapDpWidth = findViewById(R.id.colorMap).getWidth() / density;
            mapDpHeight = findViewById(R.id.colorMap).getHeight() / density;
            mapLoad();
        }
    }
    public void mapLoad(){
        if(mLPty >= 3) {
            getAndShowCctvData();
            getAndShowStreetlampData();
            getAndShowLightMapData();
        }
    }

    static abstract public class OnClickListenetIncludesView implements View.OnClickListener{

        View v;
        OnClickListenetIncludesView(View c){
            this.v=c;
        }
    }

    static abstract public class OnClickListenetIncludesObj<T> implements View.OnClickListener{

        T o;
        OnClickListenetIncludesObj(T o){
            this.o=o;
        }
    }

    // 뒤로가기 버튼 누르면 페이지가 큐에서 하나씩 제거된다
    public void onBackPressed(){
        if(isDrawerOpen){
            isDrawerOpen=false;
            closeDownDrawer();
        }
        else if(pageQueue.get(pageQueue.size()-1).exit()){
            pageQueue.remove(pageQueue.size()-1);
            pageQueue.get(pageQueue.size()-1).reEnter();
        }
    }

    // 버튼 정의
    int toggleButtonList[]={
            R.id.cctvbutton,
            R.id.streetlampbutton,
            R.id.lightbutton,
            R.id.massagebutton,
            R.id.lightrecordbutton,
            R.id.flashlightbutton,
            R.id.soundbutton
    };
    int toggleButtonDoingColor[] = {
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 255, 100, 100),
    };
    int toggleButtonCompleteColor[] = {
            Color.argb(255, 0, 212, 0),
            Color.argb(255, 212, 212, 30),
            Color.argb(255, 212, 212, 30),
            Color.argb(255, 30, 30, 212),
            Color.argb(255, 212, 212, 30),
            Color.argb(255, 180, 180, 240),
            Color.argb(255, 255, 30, 30),
    };
    int toggleButtonFailColor[] = {
            0,
            Color.argb(255, 212, 0, 0),
            Color.argb(255, 212, 0, 0),
            Color.argb(255, 212, 0, 0),
            Color.argb(255, 212, 0, 0),
            Color.argb(255, 212, 0, 0),
            Color.argb(255, 212, 0, 0),
            Color.argb(255, 212, 0, 0),
    };
    boolean toggleButtonState[] = {
            false, false, false, false, false, false, false
    };
    boolean toggleButtonCompleteLoad[] = {
            false, false, false, false, false, false, false
    };

    void buttonToggle(int i){
        toggleButtonState[i] = !toggleButtonState[i];
        if(toggleButtonState[i]){
            ((ImageView)((FrameLayout) findViewById(toggleButtonList[i])).getChildAt(0)).
                    setColorFilter(toggleButtonCompleteLoad[i]?toggleButtonCompleteColor[i]:toggleButtonDoingColor[i], PorterDuff.Mode.SRC_IN);
        }
        else ((ImageView)((FrameLayout) findViewById(toggleButtonList[i])).getChildAt(0)).
                setColorFilter(null);
    }
    public void toggleComplete(int i, boolean ok){
        toggleButtonCompleteLoad[i]=ok;
        if(toggleButtonState[i]) ((ImageView)((FrameLayout) findViewById(toggleButtonList[i])).getChildAt(0)).
                setColorFilter(toggleButtonCompleteLoad[i]?toggleButtonCompleteColor[i]:toggleButtonDoingColor[i], PorterDuff.Mode.SRC_IN);
    }


    // 버튼 클릭 리스너를 전부 모아둠
    private void setButtonClickListener(){
        findViewById(R.id.cctvbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToggle(0);
                setCctvVisible();
            }
        });


        findViewById(R.id.streetlampbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToggle(1);
                setStreetlampVisible();
            }
        });

        findViewById(R.id.lightrecordbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToggle(4);
                if(toggleButtonState[4]){
                    Toast.makeText(MainActivity.this, "광도 측정 데이터를 수집합니다.", Toast.LENGTH_SHORT).show();
                    ((ImageView)((FrameLayout) findViewById(toggleButtonList[4])).getChildAt(0)).setColorFilter(toggleButtonCompleteColor[4], PorterDuff.Mode.SRC_IN);


                    ContentValues limit = new ContentValues();



                    new NetworkTask(servername+"/pathCounter.php", limit) {
                        @Override
                        protected void onPostExecute(String s) {
                            Log.d("<><>","<"+s+">");
                            pathNum = Integer.parseInt(s);
                            Toast.makeText(MainActivity.this, "광도 측정 데이터를 수집합니다.", Toast.LENGTH_SHORT).show();
                            measureLightData();

                        }
                    }.execute();
                }
                else{
                    Toast.makeText(MainActivity.this, "광도 측정 데이터를 수집하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.lightbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToggle(2);
                setLightmapVisible();

            }
        });

        findViewById(R.id.searchbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(geoAddToLo(((EditText)findViewById(R.id.searchAddr)).getText().toString())) ((EditText)findViewById(R.id.searchAddr)).setText("");
            }
        });
        findViewById(R.id.flashlightbutton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                buttonToggle(5);
                flashLight();
                toggleComplete(5, true);
            }
        });

        findViewById(R.id.settingbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDownDrawer("Setting");

                removeallDrawen();

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ConstraintLayout c;

                c= (ConstraintLayout) vi.inflate(R.layout.layout_edit, null);
                ((TextView) c.findViewById(R.id.NormalText)).setText("긴급통화 전화번호 설정");
                c.setOnClickListener(new OnClickListenetIncludesView(c) {
                    @Override
                    public void onClick(View view) {
                        if(v.findViewById(R.id.l2).getVisibility()==View.VISIBLE){
                            v.findViewById(R.id.l2).setVisibility(View.GONE);
                            v.findViewById(R.id.l3).setVisibility(View.GONE);
                        }
                        else{
                            v.findViewById(R.id.l2).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.l3).setVisibility(View.VISIBLE);
                        }
                    }

                });
                c.findViewById(R.id.b1).setOnClickListener(new OnClickListenetIncludesView(c){
                    @Override
                    public void onClick(View view) {
                        Pattern p = Pattern.compile("[0-9]{10,11}");
                        Matcher m = p.matcher(((EditText)v.findViewById(R.id.EditText)).getText().toString());
                        boolean b = m.matches();
                        if(b) {
                            sharedSave("Phone", ((EditText) v.findViewById(R.id.EditText)).getText().toString());
                            v.findViewById(R.id.l2).setVisibility(View.GONE);
                            v.findViewById(R.id.l3).setVisibility(View.GONE);
                        }
                        else
                            Toast.makeText(MainActivity.this,"유효한 전화번호가 아닙니다. -는 없애 주세요.",Toast.LENGTH_SHORT).show();
                    }
                });
                c.findViewById(R.id.b2).setOnClickListener(new OnClickListenetIncludesView(c){
                    @Override
                    public void onClick(View view) {
                        ((EditText)v.findViewById(R.id.EditText)).setText(sharedLoad("Phone"));
                        v.findViewById(R.id.l2).setVisibility(View.GONE);
                        v.findViewById(R.id.l3).setVisibility(View.GONE);
                    }
                });
                ((EditText)c.findViewById(R.id.EditText)).setText(sharedLoad("Phone"));
                addDrawen(c);


                addDrawen(vi.inflate(R.layout.blankindrawen, null));

                c= (ConstraintLayout) vi.inflate(R.layout.layout_sound, null);
                ((TextView) c.findViewById(R.id.NormalText)).setText("긴급소리 음량 설정");

                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                SeekBar volume = (SeekBar)c.findViewById(R.id.seekBarValume);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volume.setMax(maxVolume);
                volume.setProgress(currentVolume);
                volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                });
                c.setOnClickListener(new OnClickListenetIncludesView(c) {
                    @Override
                    public void onClick(View view) {
                        if(v.findViewById(R.id.l2).getVisibility()==View.VISIBLE){
                            v.findViewById(R.id.l2).setVisibility(View.GONE);
                        }
                        else{
                            v.findViewById(R.id.l2).setVisibility(View.VISIBLE);
                        }
                    }

                });
                addDrawen(c);

                addDrawen(vi.inflate(R.layout.blankindrawen, null));

                c= (ConstraintLayout) vi.inflate(R.layout.layout_cradit, null);
                c.setOnClickListener(new OnClickListenetIncludesView(c) {
                    @Override
                    public void onClick(View view) {
                        if(v.findViewById(R.id.l2).getVisibility()==View.VISIBLE){
                            v.findViewById(R.id.l2).setVisibility(View.GONE);
                        }
                        else{
                            v.findViewById(R.id.l2).setVisibility(View.VISIBLE);
                        }
                    }

                });
                addDrawen(c);
            }
        });






        findViewById(R.id.favoritebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoriteDrawer();

            }
        });


        // =============== 볼륨조절 추가부분 ================
        findViewById(R.id.soundbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToggle(6);



                if(toggleButtonState[6]) {
                    streamId = bellSound.play(soundId, 1.0F, 1.0F, 1, -1, 1.0F);


                }
                else {
                    bellSound.stop(streamId);

                }
                toggleComplete(6, true);

            }
        });



// =============== 볼륨조절 추가부분 ================

// ================긴급통화 추가 부분===================
        findViewById(R.id.callbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context c = view.getContext();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sharedLoad("Phone")));

                try{
                    c.startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
// ================긴급통화 추가 부분===================

// ================설정 추가 부분 ======================

// ================설정 추가 부분 ======================

// ================주변 위치 탑색=======================
        findViewById(R.id.nearlocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToggle(3);
                //showPlaceInformation(currentPosition);
                //findViewById(R.id.layout);
            }
        });

// ================주변 위치 탐색=======================

    }

    public void removeallDrawen(){
        ((ViewGroup)findViewById(R.id.downdraw_linear)).removeAllViews();
    }
    public void addDrawen(View v){
        ((ViewGroup)findViewById(R.id.downdraw_linear)).addView(v);
        /*


         */


    }
    public void setFavoriteDrawer(){
        openDownDrawer("Favorite");
        removeallDrawen();
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout c;


        String s = sharedLoad("Favorites");
        String[] sc = s.split(";");

        c= (ConstraintLayout) vi.inflate(R.layout.layout_vid, null);
        c.findViewById(R.id.addAddr).setOnClickListener(new OnClickListenetIncludesObj<View>(c) {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String s = ((EditText)o.findViewById(R.id.AddrText)).getText().toString();
                if(s.length()>0 && !s.contains(";")){
                    PageState ps = pageQueue.get(pageQueue.size()-1);
                    double[] d = new double[2];
                    if(ps instanceof FixedLocationPageState){
                        d[0]=((FixedLocationPageState)ps).ll.latitude;
                        d[1]=((FixedLocationPageState)ps).ll.longitude;
                    }
                    else if(ps instanceof YourLocationState){
                        d[0]=latitude;
                        d[1]=longitude;
                    }
                    String k = sharedLoad("Favorites");
                    String[] sc = k.split(";");
                    k = s + ";"+ String.valueOf(d[0]) +  ";"+ String.valueOf(d[1]) +( k.length()>0? ";" + String.join(";",((String[])(sc.length>27?Arrays.copyOfRange(sc,0,27):sc))) : "");
                    sharedSave("Favorites",k);
                    DrawerSort="";
                    setFavoriteDrawer();
                }
                else Toast.makeText(MainActivity.this, "제대로 된 장소 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
        });
        addDrawen(c);
        addDrawen(vi.inflate(R.layout.blankindrawen, null));

        if(sc.length>=3){
            for(int i=0;i*3<sc.length;i++){
                c= (ConstraintLayout) vi.inflate(R.layout.layout_adddel, null);
                ((TextView) c.findViewById(R.id.NormalText)).setText(sc[i*3]);
                double[] d = new double[2];
                d[0]=Double.valueOf(sc[i*3+1]);
                d[1]=Double.valueOf(sc[i*3+2]);
                c.findViewById(R.id.realback).setOnClickListener(new OnClickListenetIncludesObj<double[]>(d) {
                    @Override
                    public void onClick(View view) {
                        putState(new FixedLocationPageState(new LatLng(o[0],o[1])));
                    }
                });
                c.findViewById(R.id.delete).setOnClickListener(new OnClickListenetIncludesObj<Integer>(i) {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        String k = sharedLoad("Favorites");
                        String[] sc = k.split(";");
                        String newk = o>0?String.join(";",(Arrays.copyOfRange(sc,0,o*3))):"";
                        if(o*3+3<sc.length && o>0) newk += ";";
                        if(o*3+3<sc.length) newk += String.join(";",(Arrays.copyOfRange(sc,o*3+3,sc.length )));
                        sharedSave("Favorites",newk);
                        DrawerSort="";
                        setFavoriteDrawer();
                    }
                });

                addDrawen(c);
                addDrawen(vi.inflate(R.layout.blankindrawen, null));
            }
        }
    }
    Animation translateup;
    Animation translatedown;
    private void InitanimateDownDrawer(){
        translateup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up);
        translatedown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down);


    }
    private void openDownDrawer(String s){

        if(DrawerSort.equals(s)){
            closeDownDrawer();
        }
        else
        {
            DrawerSort=s;
            findViewById(R.id.downdraw).setVisibility(View.VISIBLE);
            if(!isDrawerOpen) findViewById(R.id.downdraw).startAnimation(translateup);
            isDrawerOpen = true;
        }

    }
    private void closeDownDrawer(){
        DrawerSort="";
        findViewById(R.id.downdraw).setVisibility(View.INVISIBLE);
        if(isDrawerOpen) findViewById(R.id.downdraw).startAnimation(translatedown);
        isDrawerOpen = false;
    }

    //전화번호 등록에 필요한 함수
    private EditText showsettingDialog() {
        /*
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout settingLayout = (LinearLayout) vi.inflate(R.layout.phonecall, null);
        final EditText num = (EditText) settingLayout.findViewById(R.id.phonenum);

        new AlertDialog.Builder(this).setTitle("Phone").setView(settingLayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "전화번호 : " + num.getText().toString() , Toast.LENGTH_SHORT).show(); } }).show();
        return num;
        */
        return null;
    }



    private Bitmap bt = null;

    // 수집한 데이터를 서버로 보냄
    private void measureLightData() {
        if(proximity>0){
            ContentValues limit = new ContentValues();
            limit.put("la", latitude);
            limit.put("lo", longitude);
            limit.put("lux", light);
            limit.put("uuid", GetDevicesUUID(MainActivity.this));
            limit.put("path", pathNum);
            new NetworkTask(servername+"/measureLight.php", limit) {
                @Override
                protected void onPostExecute(String s) {

                }
            }.execute();
        }
    }




    static class SingleDrawnCircle {
        public double la;
        public double lo;
        public Circle cl;
        public boolean loaded;
        SingleDrawnCircle(double la, double lo, Circle cl){
            this.la=la;
            this.lo=lo;
            this.cl=cl;
            loaded = true;
        }
    };


    // CCTV, 가로등 위치 저장소
    HashMap<Integer, SingleDrawnCircle> loadedCctv = new HashMap<Integer, SingleDrawnCircle>();
    HashMap<Integer, SingleDrawnCircle> loadedStreetlamp = new HashMap<Integer, SingleDrawnCircle>();



    // 버튼 상태에 따라 오버랩할 정보를 보이냐 보이지 않냐 결정하는 함수
    public void setCctvVisible(){
        Iterator<Integer> iter = loadedCctv.keySet().iterator();
        while(iter.hasNext()) {
            Integer key = iter.next();
            loadedCctv.get(key).cl.setVisible(toggleButtonState[0]);
        }
    }
    public void setStreetlampVisible(){
        Iterator<Integer> iter = loadedStreetlamp.keySet().iterator();
        while(iter.hasNext()) {
            Integer key = iter.next();
            loadedStreetlamp.get(key).cl.setVisible(toggleButtonState[1]);
        }
    }
    public void setLightmapVisible(){
        findViewById(R.id.colorMap).setVisibility(toggleButtonState[2]?View.VISIBLE:View.INVISIBLE);
    }





    // 광량 정보 오버랩은 색 데이터를 점마다 가져와서 비트맵 만들어 오버랩한다. 대대적 수정 예정
    public void getAndShowLightMapData() {
        toggleComplete(2, false);

        final int colorscale = 4;

        if (bt == null){
            bt = Bitmap.createBitmap(findViewById(R.id.colorMap).getWidth() / colorscale, findViewById(R.id.colorMap).getHeight() / colorscale, Bitmap.Config.ARGB_8888);
        }

        toggleComplete(2, false);
        //findViewById(R.id.colorMap).setVisibility(View.INVISIBLE);
        double d[] = getBoundary(1.0);
        int w = (int)findViewById(R.id.colorMap).getWidth() / colorscale;
        int h = (int)findViewById(R.id.colorMap).getHeight() / colorscale;
        ContentValues limit = new ContentValues();
        limit.put("la0", d[2]);
        limit.put("la1", d[0]);
        limit.put("lo0", d[1]);
        limit.put("lo1", d[3]);
        limit.put("w",w);
        limit.put("h",h);


        new NetworkBinaryTask(servername+"/lightMapGain.php", limit) {
            @Override
            protected void onPostExecute(byte[] b) {

                findViewById(R.id.colorMap).setScaleX(1.0f);
                findViewById(R.id.colorMap).setScaleY(1.0f);
                findViewById(R.id.colorMap).setTranslationX(0.0f);
                findViewById(R.id.colorMap).setTranslationY(1.0f);

                cmsLL = mMap.getCameraPosition().target;
                cmsZoom = mMap.getCameraPosition().zoom;

                int w = (int)findViewById(R.id.colorMap).getWidth() / colorscale;
                int h = (int)findViewById(R.id.colorMap).getHeight() / colorscale;
                byte[] xa= {(byte)90,(byte)135,(byte)195,(byte)255,(byte)135,(byte)195,(byte)255,(byte)135,(byte)195,(byte)255,(byte)135,(byte)195,(byte)255,(byte)135,(byte)195,(byte)255};
                byte[] xr= {(byte)0,(byte)85,(byte)170,(byte)255,(byte)85,(byte)170,(byte)255,(byte)85,(byte)170,(byte)255,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
                byte[] xg ={(byte)0,(byte)0,(byte)0,(byte)0,(byte)42,(byte)85,(byte)127,(byte)85,(byte)170,(byte)255,(byte)85,(byte)170,(byte)255,(byte)85,(byte)170,(byte)255};
                byte[] xb ={(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)85,(byte)170,(byte)255};
                byte[] buf = new byte[h * w * 4];




                for (int i = 0; i < h * w; i++) {
                    int xk = ((b[i/2]>>(i%2==0?4:0)) & 0x0f);
                    buf[i*4] = xa[xk];
                    buf[i*4 + 1] = xr[xk];
                    buf[i*4 + 2] = xg[xk];
                    buf[i*4 + 3] = xb[xk];
                }
                IntBuffer intBuf = ByteBuffer.wrap(buf).asIntBuffer();
                int[] intarray = new int[intBuf.remaining()];
                intBuf.get(intarray);

                bt.setPixels(intarray, 0, w, 0, 0, w, h);
                Bitmap bitmapScaled = Bitmap.createScaledBitmap(bt, bt.getWidth() * 4, bt.getHeight() * 4, true);
                Drawable drawable = new BitmapDrawable(MainActivity.this.getResources(), bitmapScaled);
                ((ImageView) findViewById(R.id.colorMap)).setImageDrawable(drawable);
                ((ImageView) findViewById(R.id.colorMap)).setScaleType(ImageView.ScaleType.FIT_XY);
                ((ImageView) findViewById(R.id.colorMap)).setAdjustViewBounds(true);
                toggleComplete(2, true);
                findViewById(R.id.colorMap).setVisibility(toggleButtonState[2]?View.VISIBLE:View.INVISIBLE);

            }
        }.execute();
    }

    // 지도의 경계선 위경도 알아내는 함수. TRY AND ERROR로 알아냄
    public double[] getBoundary(double scale){
        LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        double zoom = mMap.getCameraPosition().zoom;
        double returnValue[] = {0.0,0.0,0.0,0.0};
        returnValue[0]=center.latitude + (mapDpHeight*0.00002138051*cos(toRadians(center.latitude))*65536.0/(pow(2.0,zoom)*2));
        returnValue[1]=center.longitude - (mapDpWidth*0.00002139714*65536.0/(pow(2.0,zoom)*2));
        returnValue[2]=center.latitude - (mapDpHeight*0.00002138051*cos(toRadians(center.latitude))*65536.0/(pow(2.0,zoom)*2));
        returnValue[3]=center.longitude + (mapDpWidth*0.00002139714*65536.0/(pow(2.0,zoom)*2));
        return returnValue;
    }

    // CCTV 데이터 서버에서 가져오기
    public void getAndShowCctvData(){
        toggleComplete(0, false);
        double d[] = getBoundary(3.0);
        ContentValues limit = new ContentValues();
        limit.put("la0", d[2]);
        limit.put("la1", d[0]);
        limit.put("lo0", d[1]);
        limit.put("lo1", d[3]);
        new NetworkTask(servername+"/cctv.php", limit) {
            @Override
            protected void onPostExecute(String s) {
                JSONArray jsonA0 = null;
                JSONArray jsonA1 = null;
                try {
                    jsonA0 = new JSONArray(s);
                    for (int i = 0; i < jsonA0.length(); i++) {
                        jsonA1 = jsonA0.getJSONArray(i);
                        Integer key = jsonA1.getInt(0);
                        if(loadedCctv.containsKey(key)){
                            if(loadedCctv.get(key).cl.isVisible() == false){
                                loadedCctv.get(key).cl.setVisible(toggleButtonState[0]);
                            }
                        }
                        else{
                            Circle cl = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(jsonA1.getDouble(1), jsonA1.getDouble(2)))
                                    .radius(20)
                                    .strokeWidth(0)
                                    .fillColor(Color.argb(180, 0, 212, 0))
                            );
                            SingleDrawnCircle sdc = new SingleDrawnCircle(jsonA1.getDouble(1), jsonA1.getDouble(2), cl);
                            sdc.loaded = toggleButtonState[0];
                            cl.setVisible(toggleButtonState[0]);
                            loadedCctv.put(key, sdc);
                        }
                    }

                    if(toggleButtonState[0]){
                        ((ImageView)((FrameLayout) findViewById(toggleButtonList[0])).getChildAt(0)).setColorFilter(toggleButtonCompleteColor[0], PorterDuff.Mode.SRC_IN);
                    }

                } catch (JSONException e) {
                    // TODO - ERROR CONTROL
                }
                toggleComplete(0, true);
            }
        }.execute();

    }

    // 가로등 데이터 서버에서 가져오기
    public void getAndShowStreetlampData(){
        toggleComplete(1, false);
        double d[] = getBoundary(3.0);
        ContentValues limit = new ContentValues();
        limit.put("la0", d[2]);
        limit.put("la1", d[0]);
        limit.put("lo0", d[1]);
        limit.put("lo1", d[3]);
        new NetworkTask(servername+"/streetlight.php", limit) {
            @Override
            protected void onPostExecute(String s) {
                JSONArray jsonA0 = null;
                JSONArray jsonA1 = null;
                try {
                    jsonA0 = new JSONArray(s);
                    for (int i = 0; i < jsonA0.length(); i++) {
                        jsonA1 = jsonA0.getJSONArray(i);
                        Integer key = jsonA1.getInt(0);
                        if(loadedStreetlamp.containsKey(key)){
                            if(loadedStreetlamp.get(key).cl.isVisible() == false){
                                loadedStreetlamp.get(key).cl.setVisible(toggleButtonState[1]);
                            }
                        }
                        else{
                            Circle cl = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(jsonA1.getDouble(1), jsonA1.getDouble(2)))
                                    .radius(10)
                                    .strokeWidth(0)
                                    .fillColor(Color.argb(180, 212, 212, 0))
                            );
                            SingleDrawnCircle sdc = new SingleDrawnCircle(jsonA1.getDouble(1), jsonA1.getDouble(2), cl);
                            sdc.loaded = toggleButtonState[1];
                            cl.setVisible(toggleButtonState[1]);
                            loadedStreetlamp.put(key, sdc);
                        }
                    }
                    if(toggleButtonState[1]){
                        ((ImageView)((FrameLayout) findViewById(toggleButtonList[1])).getChildAt(0)).setColorFilter(toggleButtonCompleteColor[1], PorterDuff.Mode.SRC_IN);
                    }

                } catch (JSONException e) {
                    // TODO - ERROR CONTROL
                }
                toggleComplete(1, true);

            }
        }.execute();
    }

    // 수집 테스팅
    public  void lightGatherTest(){
        findViewById(R.id.alllayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
        findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.imageView2).bringToFront();
            }
        });
        buttonToggle(4);


    }

}



// 서버 통신 관련 클래스
abstract class NetworkTask extends AsyncTask<Void, Void, String> {
    private String url;
    private ContentValues values;
    public NetworkTask(String url, ContentValues values){
        this.url = url;
        this.values = values;
    }
    @Override protected String doInBackground(Void... params) {
        String result; // 요청 결과를 저장할 변수.
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
        return result;
    }
    abstract protected void onPostExecute(String s);
}
class RequestHttpURLConnection {
    public String request(String _url, ContentValues _params) {
        HttpURLConnection urlConn = null; // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();
        if (_params != null) { // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            String key;
            String value;
            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();
                if (isAnd) sbParams.append("&");
                sbParams.append(key).append("=").append(value);
                if (_params.size() >= 2) isAnd = true;
            }
        }
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(15000);
            urlConn.setReadTimeout(15000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencode");
            String strParams = sbParams.toString();
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            String line;
            String page = "";
            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null) urlConn.disconnect();
        }
        return null;
    }
}

abstract class NetworkBinaryTask extends AsyncTask<Void, Void, byte[]> {
    private String url;
    private ContentValues values;
    public NetworkBinaryTask(String url, ContentValues values){
        this.url = url;
        this.values = values;
    }
    @Override protected byte[] doInBackground(Void... params) {
        byte[] result; // 요청 결과를 저장할 변수.
        RequestHttpURLBinaryConnection requestHttpURLConnection = new RequestHttpURLBinaryConnection();
        result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
        return result;
    }
    abstract protected void onPostExecute(byte[] s);
}


class RequestHttpURLBinaryConnection {
    public byte[] request(String _url, ContentValues _params) {
        HttpURLConnection urlConn = null; // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();
        if (_params != null) { // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            String key;
            String value;
            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();
                if (isAnd) sbParams.append("&");
                sbParams.append(key).append("=").append(value);
                if (_params.size() >= 2) isAnd = true;
            }
        }
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(15000);
            urlConn.setReadTimeout(15000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(15000);
            urlConn.setReadTimeout(15000);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencode");
            String strParams = sbParams.toString();
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();
            urlConn.connect();
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) return null;

            int len = urlConn.getContentLength();
            byte[] tmpByte = new byte[len];
            byte[] returnValue = null;

            InputStream is = urlConn.getInputStream();
            int Read;
            for (;;) {
                Read = is.read(tmpByte);
                if (Read <= 0) {
                    break;
                }
                if(returnValue == null) {
                    byte[] newRV = new byte[Read];
                    System.arraycopy(tmpByte,0,newRV,0,Read);
                    returnValue = newRV;
                }
                else{
                    byte[] newRV = new byte[returnValue.length+ Read];
                    System.arraycopy(returnValue,0,newRV,0,returnValue.length);
                    System.arraycopy(tmpByte,0,newRV,returnValue.length,Read);
                    returnValue = newRV;
                }
            }

            return returnValue;
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null) urlConn.disconnect();
        }
        return null;
    }

}