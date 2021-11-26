package com.caucap2021_1_2_10.ddubuk2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import android.location.Geocoder;
import java.util.ArrayList;

import Adapter.pointAdapter;
import ted.gun0912.clustering.naver.TedNaverClustering;



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
        Button btnMark2 = (Button) findViewById(R.id.btnmark2);

        //네이버 지도
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        btnMark1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarker(marker1, 37.5067893, 126.960823, R.drawable.ic_baseline_place_24, 0); /*중대병원 마커*/

                marker1.setOnClickListener(new Overlay.OnClickListener() {
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

        btnMark2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarker(marker2, 37.50919257274509, 126.96342325389423, R.drawable.ic_baseline_place_24, 10); /*흑석역*/

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
        UiSettings uiSettings = naverMap.getUiSettings();


        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Basic);

        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
        naverMap.setSymbolScale(0.7f);
        naverMap.setIndoorEnabled(true);




        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(37.50375115212309, 126.95585107704552),   // 초기위치 : 학교
                15,
                0,
                180

        );
        naverMap.setCameraPosition(cameraPosition);

        //------- 마커 클러스터 실행----------
/*        TedNaverClustering.with(this, naverMap)
                .items(getItems())
                .make();*/
        //-------- 마커 클러스터 실행---------

    }

    //--------마커 클러스터링 함수 (현재 난수로 좌표찍히는중)-------------
/*    private ArrayList<NaverItem> getItems() {
        LatLngBounds bounds = naverMap.getContentBounds();
        ArrayList<NaverItem> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            NaverItem temp = new NaverItem((bounds.getNorthLatitude() - bounds.getSouthLatitude()) * Math.random() + bounds.getSouthLatitude(),
                    (bounds.getEastLongitude() - bounds.getWestLongitude()) * Math.random() + bounds.getWestLongitude()
            );
            items.add(temp);
        }
        return items;
    }*/
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
