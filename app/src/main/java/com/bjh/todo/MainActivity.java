package com.bjh.todo;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button loginLogoutButton;
    private Button viewMemberDetailsButton;
    private FloatingActionButton addTodo;
    private LinearLayout extraBlock;
    private TextView extraBlockText;
    private ScheduleDBHelper scheduleDBHelper;
    private SharedPreferences sharedPreferences;
    private boolean isLoggedIn;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // 알림 채널 생성
        NotificationHelper.createNotificationChannel(this);

        // 권한 요청
        requestNotificationPermission();

        // 뷰 초기화
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        loginLogoutButton = findViewById(R.id.login_logout_button);
        addTodo = findViewById(R.id.add_schedule);
        calendarView = findViewById(R.id.calendarView);
        extraBlock = findViewById(R.id.extra_block);
        extraBlockText = findViewById(R.id.extra_block_text);
        scheduleDBHelper = new ScheduleDBHelper(this);
        viewMemberDetailsButton = findViewById(R.id.view_member_details_button);

        // 실시간 위도 경도 log
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.i("MyLocation", "위도: " + currentLocation.latitude + ", 경도: " + currentLocation.longitude);

                // 오늘 등록된 일정의 위치 가져오기
                List<ScheduleDTO> schedules = scheduleDBHelper.getSchedulesByDate(selectedDate, sharedPreferences.getString("userId", ""));
                for (ScheduleDTO schedule : schedules) {
                    // 스케줄에 있는 주소 지명을 위도 경도로 변경
                    LatLng scheduleLocation = getLatLngFromAddress(schedule.getLocation());

                    if (scheduleLocation != null) {
                        // 일정 위치와 현 위치 사이에 거리 계싼
                        float distance = calculateDistance(currentLocation, scheduleLocation);
                        // 50m 이내면 알람 표시
                        if (distance <= 50) {
                            showLocationNotification(schedule);
                        }
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);

        // 로그인 상태 확인
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        updateLoginLogoutButton(); // 버튼 텍스트 업데이트

        // 오늘 날짜 설정
        Calendar calendar = Calendar.getInstance();
        selectedDate = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        calendarView.setDate(calendar.getTimeInMillis(), true, true);

        // 날짜 선택 리스너
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadSchedulesForDate(selectedDate);
        });

        // 로그인/로그아웃 버튼 클릭 리스너
        loginLogoutButton.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
            } else {
                // 로그아웃 처리
                isLoggedIn = false;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                updateLoginLogoutButton();
                Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                // onResume() 호출
                onResume(); // 또는 직접 UI 업데이트
            }
        });

        // 일정 추가 버튼 클릭 리스너
        addTodo.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivityForResult(intent, 2);
            }
        });

        // 일정 목록 클릭 리스너
        extraBlock.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, ScheduleListActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivityForResult(intent, 2);
            }
        });

        viewMemberDetailsButton.setOnClickListener(v -> {
            // 로그인 상태 확인
            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 페이지로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                // 로그인한 경우 MemberDetailsActivity로 이동
                Intent intent = new Intent(MainActivity.this, MemberDetailsActivity.class);
                String userId = sharedPreferences.getString("userId", null);
                if (userId != null) {
                    intent.putExtra("userId", userId);
                }
                startActivity(intent);
            }
        });
    }

    // 주소를 위도와 경도로 변환
    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
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

    // 두 위치 간 거리 계산
    private float calculateDistance(LatLng location1, LatLng location2) {
        float[] results = new float[1]; // 결과를 저장할 배열 생성
        // 두 위치 간의 거리 계산
        Location.distanceBetween(
                location1.latitude, location1.longitude, // 첫 번째 위치의 위도와 경도
                location2.latitude, location2.longitude, // 두 번째 위치의 위도와 경도
                results // 결과를 저장할 배열
        );
        return results[0]; // 계산된 거리 반환 (미터 단위)
    }

    // 위치 기반 알림 생성
    private void showLocationNotification(ScheduleDTO schedule) {
        // 알림을 표시하기 전에 로그를 출력합니다.
        Log.i("Notification", "알림 표시: " + schedule.getScheduleText());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                schedule.getScheduleId(),
                notificationIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("일정 알림")
                .setContentText(schedule.getScheduleText() + " 장소에 도착했습니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(schedule.getScheduleId(), builder.build());
        }
    }

    // 알림 권한 요청 메소드
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "알림 권한이 부여되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 로그인 상태 확인
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (selectedDate != null) {
            loadSchedulesForDate(selectedDate);
        }
    }

    private void loadSchedulesForDate(String date) {
        String userId = sharedPreferences.getString("userId", "User");
        List<ScheduleDTO> schedules = scheduleDBHelper.getSchedulesByDate(date, userId);

        schedules.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));

        if (!isLoggedIn) {
            extraBlockText.setText("로그인 해주세요.");
            extraBlockText.setGravity(Gravity.CENTER);
            return;
        }

        if (schedules.isEmpty()) {
            extraBlockText.setText("해당 날짜에 일정이 없습니다.");
            extraBlockText.setGravity(Gravity.CENTER);
        } else {
            StringBuilder scheduleList = new StringBuilder();
            for (ScheduleDTO schedule : schedules) {
                scheduleList.append(schedule.getStartTime()).append(" ~ ")
                        .append(schedule.getEndTime()).append(" : ")
                        .append(schedule.getScheduleText()).append(" \n");
                // 알람 설정 추가
                AlarmHelper.setAlarm(this, schedule);
            }
            extraBlockText.setText(scheduleList.toString());
            extraBlockText.setGravity(Gravity.START);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            isLoggedIn = true;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userId", data.getStringExtra("userId"));
            editor.apply();
            updateLoginLogoutButton();
            extraBlockText.setText("로그인 후 날짜를 선택하세요.");
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (selectedDate != null) {
                loadSchedulesForDate(selectedDate);
            }
        }
    }

    private void updateLoginLogoutButton() {
        if (isLoggedIn) {
            loginLogoutButton.setText("로그아웃");
        } else {
            loginLogoutButton.setText("로그인");
        }
    }
}
