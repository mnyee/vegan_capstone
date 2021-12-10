package com.caucap2021_1_2_10.ddubuk2;


import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import Adapter.pointAdapter;
import android.content.Intent;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imagebutton_map = (ImageButton) findViewById(R.id.map);
        ImageButton imagebutton_review = (ImageButton) findViewById(R.id.review);

        imagebutton_map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent mapintent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(mapintent);
            }
        });

        imagebutton_review.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent reviewintent = new Intent(getApplicationContext(),ListActivity.class);
                startActivity(reviewintent);
            }
        });


    }
}

