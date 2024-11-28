package com.bjh.todo;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class LocationService {

    private Context context;
    private ScheduleDBHelper scheduleDBHelper;
    private SharedPreferences sharedPreferences;
    private String selectedDate;

    public LocationService(Context context, ScheduleDBHelper scheduleDBHelper, SharedPreferences sharedPreferences) {
        this.context = context;
        this.scheduleDBHelper = scheduleDBHelper;
        this.sharedPreferences = sharedPreferences;
    }

    public void startLocationUpdates(String selectedDate) {
        this.selectedDate = selectedDate;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                handleLocationChange(location);
            }

            // 다른 LocationListener 메서드들은 필요에 따라 추가
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // 권한이 없으면 리턴
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListener);
    }

    private void handleLocationChange(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // 오늘 등록된 일정 가져오기
        List<ScheduleDTO> schedules = scheduleDBHelper.getSchedulesByDate(selectedDate, sharedPreferences.getString("userId", ""));

        SharedPreferences prefs = context.getSharedPreferences("LocationAlertPrefs", Context.MODE_PRIVATE);

        for (ScheduleDTO schedule : schedules) {
            LatLng scheduleLocation = getLatLngFromAddress(schedule.getLocation());

            if (scheduleLocation != null) {
                // 위치 간 거리 계산
                float distance = NotificationHelper.calculateDistance(currentLocation, scheduleLocation);

                // 50m 이내에 접근했을 때만 알림 표시
                if (distance <= 50) {
                    // 알림이 이미 울린 상태인지 확인
                    boolean hasNotified = prefs.getBoolean("hasNotified_" + schedule.getScheduleId(), false);

                    if (!hasNotified) {
                        // 알림을 울리고, 알림 상태를 기록
                        NotificationHelper.showLocationNotification(context, schedule);
                        prefs.edit().putBoolean("hasNotified_" + schedule.getScheduleId(), true).apply();
                    }
                }
            }
        }
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopLocationUpdates() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates((LocationListener) context);
    }
}