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
import android.widget.ImageButton;
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
import java.util.Vector;

import Adapter.pointAdapter;
import ted.gun0912.clustering.naver.TedNaverClustering;
import org.json.JSONArray;
import org.json.JSONException;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback  {
    private MapView mapView;
    private static NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

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


    private FusedLocationSource locationSource;


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

        EditText searchAddr = (EditText)findViewById(R.id.searchAddr);


        //네이버 지도
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        servername = getString(R.string.server_name);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


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
        ImageButton search = (ImageButton) findViewById(R.id.searchbutton);
        EditText searchAddr = (EditText) findViewById(R.id.searchAddr);


        naverMap.setLocationSource(locationSource);
        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Basic);

        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
        naverMap.setSymbolScale(0.7f);
        naverMap.setIndoorEnabled(true);

        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(37.484329587455754, 127.01609689622262),
                12,
                0,
                0

        );
        naverMap.setCameraPosition(cameraPosition);

        LatLngBounds bounds = naverMap.getContentBounds();
        ArrayList<NaverItem> items = new ArrayList<>();
        ContentValues limit = new ContentValues();



/*        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {
                freeActiveMarkers();

                for (LatLng markerPosition: items) {
                    Marker marker = new Marker();
                    marker.setWidth(70);
                    marker.setHeight(100);
                    marker.setPosition(markerPosition);
                    marker.setMap(naverMap);
                    activeMarkers.add(marker);
                }
            }
        });*/


        //------- 마커 클러스터 실행----------
        TedNaverClustering.with(this, naverMap)
                .items(getItems())
                .make();
        //-------- 마커 클러스터 실행---------

        search.setOnClickListener(new View.OnClickListener() {
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

    }
/*

    private Vector<Marker> activeMarkers;
    public final static double REFERANCE_LAT = 1 / 109.958489129649955;
    public final static double REFERANCE_LNG = 1 / 88.74;
    public final static double REFERANCE_LAT_X3 = 3 / 109.958489129649955;
    public final static double REFERANCE_LNG_X3 = 3 / 88.74;

    // 현재 카메라가 보고있는 위치
    public LatLng getCurrentPosition(NaverMap naverMap) {
        CameraPosition cameraPosition = naverMap.getCameraPosition();
        return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }

    public boolean withinSightMarker(LatLng currentPosition, LatLng markerPosition) {
        boolean withinSightMarkerLat = Math.abs(currentPosition.latitude - markerPosition.latitude) <= REFERANCE_LAT_X3;
        boolean withinSightMarkerLng = Math.abs(currentPosition.longitude - markerPosition.longitude) <= REFERANCE_LNG_X3;
        return withinSightMarkerLat && withinSightMarkerLng;
    }

    private void freeActiveMarkers() {
        if (activeMarkers == null) {
            activeMarkers = new Vector<Marker>();
            return;
        }
        for (Marker activeMarker: activeMarkers) {
            activeMarker.setMap(null);
        }
        activeMarkers = new Vector<Marker>();
    }
*/

/*    //--------마커 클러스터링 함수 (현재 난수로 좌표찍히는중)-------------
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
    //----------마커 클러스터링 함수 (현재 난수로 좌표찍히는중)-------------*/

    //--------마커 클러스터링 함수 -------------
    private ArrayList<NaverItem> getItems() {
        LatLngBounds bounds = naverMap.getContentBounds();
        ArrayList<NaverItem> items = new ArrayList<>();

        items.add(new NaverItem(37.5246467590332, 126.92683410644531));
        items.add(new NaverItem(37.4689826965332, 126.93840026855469));
        items.add(new NaverItem(37.529083251953125, 126.92189025878906));
        items.add(new NaverItem(37.47843551635742, 126.98231506347656));
        items.add(new NaverItem(37.480621337890625, 126.98323822021484));
        items.add(new NaverItem(37.477970123291016, 126.95721435546875));
        items.add(new NaverItem(37.521175384521484, 126.9185562133789));
        items.add(new NaverItem(37.503662109375, 127.0100326538086));
        items.add(new NaverItem(37.51717758178711, 126.90557861328125));
        items.add(new NaverItem(37.52184295654297, 126.92816162109375));
        items.add(new NaverItem(37.47835159301758, 126.95608520507812));
        items.add(new NaverItem(37.474891662597656, 127.04256439208984));
        items.add(new NaverItem(37.486785888671875, 127.01634216308594));
        items.add(new NaverItem(37.50286102294922, 127.02454376220703));
        items.add(new NaverItem(37.47911834716797, 126.95285034179688));
        items.add(new NaverItem(37.515625, 126.90752410888672));
        items.add(new NaverItem(37.522945404052734, 126.92428588867188));
        items.add(new NaverItem(37.47600555419922, 126.9771499633789));
        items.add(new NaverItem(37.4664306640625, 126.93685150146484));
        items.add(new NaverItem(37.477970123291016, 126.95721435546875));
        items.add(new NaverItem(37.50643539428711, 127.0068359375));
        items.add(new NaverItem(37.48375701904297, 127.03482055664062));
        items.add(new NaverItem(37.48411178588867, 126.90324401855469));
        items.add(new NaverItem(37.48514175415039, 127.01586151123047));
        items.add(new NaverItem(37.493595123291016, 126.89849090576172));
        items.add(new NaverItem(37.512603759765625, 126.92414855957031));
        items.add(new NaverItem(37.52606964111328, 126.89155578613281));
        items.add(new NaverItem(37.48301315307617, 126.9969482421875));
        items.add(new NaverItem(37.483646392822266, 126.95520782470703));
        items.add(new NaverItem(37.482181549072266 ,126.94163513183594 ));
        items.add(new NaverItem(37.478675842285156 , 126.9821548461914));
        items.add(new NaverItem( 37.46232986450195, 126.95233154296875));
        items.add(new NaverItem(37.48004150390625 ,126.95210266113281 ));
        items.add(new NaverItem(37.47433090209961 ,126.91799926757812 ));
        items.add(new NaverItem( 37.470481872558594,126.93447875976562 ));
        items.add(new NaverItem(37.483795166015625 , 126.92927551269531));
        items.add(new NaverItem(37.4698371887207 ,127.04195404052734 ));
        items.add(new NaverItem( 37.52496337890625,126.93901824951172 ));
        items.add(new NaverItem( 37.515625,126.90752410888672 ));
        items.add(new NaverItem( 37.515625, 126.90752410888672));
        items.add(new NaverItem(37.516876220703125 ,126.90438079833984 ));
        items.add(new NaverItem(37.471500396728516 ,126.98245239257812 ));
        items.add(new NaverItem( 37.51831817626953,126.89595031738281 ));
        items.add(new NaverItem(37.47875213623047 , 126.95467376708984));
        items.add(new NaverItem(37.49344253540039 ,127.01612091064453 ));
        items.add(new NaverItem( 37.519046783447266, 126.88628387451172));
        items.add(new NaverItem( 37.50643539428711,127.0068359375 ));
        items.add(new NaverItem(37.47794723510742 ,126.95262145996094 ));
        items.add(new NaverItem(37.48775863647461 , 127.01319122314453));
        items.add(new NaverItem( 37.495384216308594, 127.01643371582031));
        items.add(new NaverItem( 37.5021858215332, 126.99009704589844));
        items.add(new NaverItem(37.473854064941406 , 126.91732788085938));
        items.add(new NaverItem( 37.48548889160156, 127.017333984375));
        items.add(new NaverItem(37.49069595336914, 126.99148559570312 ));
        items.add(new NaverItem( 37.51095199584961 , 127.02018737792969));
        items.add(new NaverItem(37.502960205078125 , 127.0101318359375));
        items.add(new NaverItem(37.5037727355957 , 127.02079772949219));
        items.add(new NaverItem(37.49629211425781 , 126.9857177734375 ));
        items.add(new NaverItem(37.47736358642578, 126.98747253417969 ));
        items.add(new NaverItem(37.4817504882812,127.00470733642578 ));
        items.add(new NaverItem(37.48357009887695 , 126.99656677246094 ));
        items.add(new NaverItem(37.486053466796875,126.93956756591797 ));
        items.add(new NaverItem( 37.482547760009766,126.94361114501953 ));
        items.add(new NaverItem(37.47623062133789 ,126.93753051757812 ));
        items.add(new NaverItem(37.48585891723633 , 126.9559555053711));
        items.add(new NaverItem(37.484336853027344 ,127.03057861328125 ));
        items.add(new NaverItem(37.46940994262695 ,127.04126739501953 ));
        items.add(new NaverItem( 37.51215744018555, 126.916259765625));
        items.add(new NaverItem( 37.50609588623047,126.91094970703125 ));
        items.add(new NaverItem( 37.512298583984375, 126.92183685302734));
        items.add(new NaverItem(37.486656188964844 , 126.91316223144531));
        items.add(new NaverItem(37.46944808959961 , 126.93767547607422));
        items.add(new NaverItem(37.47056198120117 , 126.93363952636719));
        items.add(new NaverItem( 37.48203659057617,126.9296646118164 ));
        items.add(new NaverItem( 37.500999450683594, 126.91187286376953));
        items.add(new NaverItem( 37.52552795410156, 126.91923522949219));
        items.add(new NaverItem( 37.52056884765625,126.90328979492188 ));
        items.add(new NaverItem(37.51354217529297 , 126.9047622680664));
        items.add(new NaverItem(37.470855712890625,127.02538299560547 ));
        items.add(new NaverItem( 37.514949798583984,127.01422882080078 ));
        items.add(new NaverItem(37.49470520019531 ,127.02816009521484 ));
        items.add(new NaverItem(37.48482131958008 ,126.9320068359375 ));
        items.add(new NaverItem(37.49801254272461 , 126.99810028076172));
        items.add(new NaverItem(37.49243927001953 , 127.01140594482422));
        items.add(new NaverItem(37.521366119384766 , 126.92501831054688));
        items.add(new NaverItem(37.476436614990234,127.0437240600586));
        items.add(new NaverItem(37.47782897949219,126.9626693725586));
        items.add(new NaverItem(37.52021408081055,126.93179321289062));
        items.add(new NaverItem(37.51353454589844,126.92245483398438));
        items.add(new NaverItem(37.47857666015625,126.95526885986328));
        items.add(new NaverItem(37.49344253540039,127.01612091064453));
        items.add(new NaverItem(37.532928466796875 , 126.90283203125));
        items.add(new NaverItem(37.52606964111328 , 126.89155578613281));
        items.add(new NaverItem(37.51943588256836 , 126.8912582397461));
        items.add(new NaverItem(37.53740692138672 , 126.89381408691406));
        items.add(new NaverItem(37.50643539428711 , 127.0068359375));
        items.add(new NaverItem(37.50925827026367 ,127.00743103027344));
        items.add(new NaverItem(37.48385238647461, 126.93024444580078));
        items.add(new NaverItem(37.516876220703125,126.90438079833984));
        items.add(new NaverItem(37.47769546508789,126.98223114013672));
        items.add(new NaverItem(37.47795867919922,126.95787048339844));
        items.add(new NaverItem(37.48904037475586,126.92949676513672));
        items.add(new NaverItem(37.50784683227539,126.91120147705078));
        items.add(new NaverItem(37.492034912109375,127.02880096435547));
        items.add(new NaverItem(37.51598358154297,126.90611267089844));
        items.add(new NaverItem(37.47890090942383,126.95262908935547));
        items.add(new NaverItem(37.47922897338867,126.95348358154297));
        items.add(new NaverItem(37.52505874633789,126.92593383789062));
        items.add(new NaverItem(37.516876220703125,126.90438079833984));
        items.add(new NaverItem(37.498451232910156,127.02635955810547));
        items.add(new NaverItem(37.47732162475586,126.9581298828125));
        items.add(new NaverItem(37.50643539428711,127.0068359375));
        items.add(new NaverItem(37.49309158325195,127.01834106445312));
        items.add(new NaverItem(37.52027130126953,126.88864135742188));
        items.add(new NaverItem(37.501033782958984,127.01163482666016));
        items.add(new NaverItem(37.49732208251953,126.9856948852539));
        items.add(new NaverItem(37.46182632446289,127.03538513183594));
        items.add(new NaverItem(37.52701950073242,126.89825439453125));
        items.add(new NaverItem(37.48542022705078,127.01896667480469));
        items.add(new NaverItem(37.48019027709961,126.98341369628906));
        items.add(new NaverItem(37.520652770996094,126.889892578125));
        items.add(new NaverItem(37.52788543701172,126.92913818359375));
        items.add(new NaverItem(37.484378814697266,127.01445770263672));
        items.add(new NaverItem(37.465057373046875 , 126.95162200927734));
        items.add(new NaverItem(37.46232986450195,126.95233154296875));
        items.add(new NaverItem(37.47765350341797,126.95822143554688));
        items.add(new NaverItem(37.500160217285156,127.00437927246094));
        items.add(new NaverItem(37.496238708496094,126.99763488769531));
        items.add(new NaverItem(37.50178909301758,127.02458953857422));
        items.add(new NaverItem(37.47626495361328,126.96294403076172));
        items.add(new NaverItem(37.49604034423828,127.02531433105469));


        return items;
    }
    //----------마커 클러스터링 함수-------------




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