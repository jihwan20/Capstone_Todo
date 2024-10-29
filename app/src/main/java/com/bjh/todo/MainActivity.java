package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.Manifest;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView; // 달력 뷰
    private Button loginLogoutButton; // 로그인/로그아웃 버튼
    private FloatingActionButton addTodo; // 일정 추가 버튼
    private LinearLayout extraBlock;
    private TextView extraBlockText; // 일정 정보를 표시할 TextView
    private ScheduleDBHelper scheduleDBHelper; // 일정 데이터베이스 헬퍼
    private SharedPreferences sharedPreferences; // 사용자 설정 저장을 위한 SharedPreferences
    private boolean isLoggedIn; // 로그인 상태

    // GPS 사용을 위한 멤버 변수 선언
    LocationManager locationManager;
    LocationListener locationListener;

    private String selectedDate; // 선택된 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar); // 레이아웃 설정

        // 권한 요청
        requestLocationPermissions();

        // 알림 채널 생성
        NotificationHelper.createNotificationChannel(this);

        // 뷰 초기화
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        loginLogoutButton = findViewById(R.id.login_logout_button); // 로그인/로그아웃 버튼 초기화
        addTodo = findViewById(R.id.add_schedule);
        calendarView = findViewById(R.id.calendarView);
        extraBlock = findViewById(R.id.extra_block);
        extraBlockText = findViewById(R.id.extra_block_text);
        scheduleDBHelper = new ScheduleDBHelper(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // 위치 리스너 초기화
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String message = String.format("위도: %f, 경도: %f", location.getLatitude(), location.getLongitude());
                Log.i("MyLocation", message);
            }
        };

        // 로그인 상태 확인
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        updateLoginLogoutButton(); // 버튼 텍스트 업데이트

        // 오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // 오늘 날짜를 CalendarView에 설정
        calendarView.setDate(calendar.getTimeInMillis(), true, true);
        selectedDate = String.format("%04d-%02d-%02d", cYear, cMonth + 1, cDayOfMonth);

        // 날짜 선택 리스너 설정
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

            // 한글 형식으로 날짜 표시
            String formattedSelectedDate = String.format("%d년 %d월 %d일", year, month + 1, dayOfMonth);
            extraBlockText.setText(formattedSelectedDate);

            loadSchedulesForDate(selectedDate);
        });

        // 로그인/로그아웃 버튼 클릭 리스너 설정
        loginLogoutButton.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1); // 로그인 후 결과를 받을 요청 코드
            } else {
                // 로그아웃 처리
                isLoggedIn = false;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                updateLoginLogoutButton(); // 버튼 텍스트 업데이트
                Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 일정 추가 버튼 클릭 리스너 설정
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

        // 해당 날짜 일정 목록 클릭 리스너
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
    }

    // 위치 권한 요청 메서드
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            startLocationUpdates(); // 권한이 이미 있다면 위치 업데이트 시작
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);

            // 마지막 위치 가져오기
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Log.i("MyLocation", "마지막 위도: " + lastLocation.getLatitude() + ", 경도: " + lastLocation.getLongitude());
            } else {
                Log.i("MyLocation", "마지막 위치 정보가 없습니다.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates(); // 권한이 승인되면 위치 업데이트 시작
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            updateLoginLogoutButton(); // 버튼 텍스트 업데이트
            extraBlockText.setText("로그인 후 날짜를 선택하세요.");
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (selectedDate != null) {
                loadSchedulesForDate(selectedDate);
            }
        }
    }

    // 로그인/로그아웃 버튼 텍스트 업데이트 메서드
    private void updateLoginLogoutButton() {
        if (isLoggedIn) {
            loginLogoutButton.setText("로그아웃");
        } else {
            loginLogoutButton.setText("로그인");
        }
    }
}
