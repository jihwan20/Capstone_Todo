package com.bjh.todo;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 기본 위치를 서울로 설정
        LatLng seoul = new LatLng(36.622605, 127.48232666666667);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12));

        // 맵 클릭 리스너 추가
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear(); // 기존 마커 지우기
            mMap.addMarker(new MarkerOptions().position(latLng).title("선택한 위치"));

            // 주소를 가져오는 메소드 호출
            String address = getAddressFromLatLng(latLng);

            // 선택한 주소를 결과로 설정
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedAddress", address);
            setResult(RESULT_OK, resultIntent);
            finish(); // Activity 종료
        });
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                // 주소 반환 (한글)
                return addresses.get(0).getAddressLine(0); // 여기에서 한국어 주소가 반환됩니다.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "주소를 찾을 수 없습니다."; // 기본 메시지
    }
}
