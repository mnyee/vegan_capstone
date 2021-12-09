package com.caucap2021_1_2_10.ddubuk2;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.pointAdapter;
import ted.gun0912.clustering.naver.TedNaverClustering;
import org.json.JSONArray;
import org.json.JSONException;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback  {
    private MapView mapView;
    private static NaverMap naverMap;


    //마커 변수 선언 및 초기화
    private Marker marker1 = new Marker();
    private Marker marker2 = new Marker();


    //Infowindow 변수 선언 및 초기화
    private InfoWindow infoWindow1 = new InfoWindow();
    private InfoWindow infoWindow2 = new InfoWindow();
    private InfoWindow infoWindow3 = new InfoWindow();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Geocoder geocoder;

    private String servername="";

    private boolean isDrawerOpen = false;
    private String DrawerSort="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 툴바 생성 및 세팅하는 부분
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button btnMark1 = (Button) findViewById(R.id.btnmark1);
        EditText searchAddr = (EditText)findViewById(R.id.searchAddr);


        //네이버 지도
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        servername = getString(R.string.server_name);



        btnMark1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarker(marker2, 37.54602901016027, 127.05354135579218, R.drawable.ic_baseline_place_24, 10); /*흑석역*/


                marker2.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        ViewGroup rootView = (ViewGroup) findViewById(R.id.drawer_layout);
                        pointAdapter adapter = new pointAdapter(MapActivity.this, rootView);

                        infoWindow3.setAdapter(adapter);
                        //인포창의 우선순위
                        infoWindow3.setZIndex(10);

                        //투명도 조정
                        infoWindow3.setAlpha(0.9f);

                        //인포창 표시
                        infoWindow3.open(marker2);
                        return false;
                    }
                });
            }

        });

        findViewById(R.id.searchbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String str=searchAddr.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                // 마커 생성
                Marker marker = new Marker();
                marker.setPosition(point);
                // 마커 추가
                marker.setMap(naverMap);

                // 해당 좌표로 화면 줌
//                naverMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(point);
                naverMap.moveCamera(cameraUpdate);
            }
        });


        //네비게이션 뷰 아이템 클릭시 이뤄지는 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                int id = item.getItemId();
                //각메뉴 클릭시 이뤄지는 이벤트
                switch (id){
                    case R.id.navigation_login:
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        break;

                    case R.id.navigation_review:
                        Intent reviewIntent = new Intent(getApplicationContext(), ListActivity.class);
                        startActivity(reviewIntent);
                        break;

                    case R.id.navigation_location_auth:
                        Intent locationIntent = new Intent(getApplicationContext(), LocationActivity.class);
                        startActivity(locationIntent);
                        break;

                    case R.id.navigation_mypage:
                        Intent mypageIntent = new Intent(getApplicationContext(), MypageActivity.class);
                        startActivity(mypageIntent);
                        break;
                }
                return true;
            }
        });
        Log.e("Frag", "Fragment");
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void setMarker(Marker marker,  double lat, double lng, int resourceID, int zIndex){
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);

        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));

        //마커의 투명도
        marker.setAlpha(0.8f);

        //마커 위치
        marker.setPosition(new LatLng(lat, lng));

        //마커 우선순위
        marker.setZIndex(zIndex);

        //마커 표시
        marker.setMap(naverMap);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;


        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Basic);

        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
        naverMap.setSymbolScale(0.7f);
        naverMap.setIndoorEnabled(true);





        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(37.52076497720581, 126.91596442465841), 
                10,
                0,
                0

        );
        naverMap.setCameraPosition(cameraPosition);

        //------- 마커 클러스터 실행----------
        TedNaverClustering.with(this, naverMap)
                .items(getItems())
                .make();
        //-------- 마커 클러스터 실행---------

    }

    //--------마커 클러스터링 함수 (현재 난수로 좌표찍히는중)-------------
    private ArrayList<NaverItem> getItems() {

        LatLngBounds bounds = naverMap.getContentBounds();
        ArrayList<NaverItem> items = new ArrayList<>();
        ContentValues limit = new ContentValues();
        new NetworkTask(servername+"/vege_db/map/load_latlng.php", limit) {

            @Override
            protected void onPostExecute(String s) {
                JSONArray jsonA0 = null;
                JSONArray jsonA1 = null;
                try{
                    jsonA0 = new JSONArray(s);
                    for(int i =  0; i< jsonA0.length();i++){
                        NaverItem temp = new NaverItem(jsonA1.getDouble(1), jsonA0.getDouble(2));
                        items.add(temp);
                    }

                } catch (JSONException e) {
                    // TODO - ERROR CONTROL
                }

            }
        }.execute();
        return items;
    }
    //----------마커 클러스터링 함수 (현재 난수로 좌표찍히는중)-------------






    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
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
