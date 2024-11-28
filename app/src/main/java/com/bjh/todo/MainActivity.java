package com.bjh.todo;

import android.Manifest;
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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  LocationListener {

    private CalendarView calendarView;
    private Button loginLogoutButton;
    private Button viewMemberDetailsButton;
    private FloatingActionButton addTodo;
    private LinearLayout extraBlock;
    private TextView extraBlockText;
    private ScheduleDBHelper scheduleDBHelper;
    private SharedPreferences sharedPreferences;
    private boolean isLoggedIn;
    private LocationService locationService;
    private LocationListener locationListener;
    private String selectedDate;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // 알림 채널 생성
        NotificationHelper.createNotificationChannel(this);

        // LocationManager 초기화
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 위치 권한 체크 후 위치 업데이트 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this); // MainActivity를 LocationListener로 등록
        }


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

        // LocationService 인스턴스 초기화
        locationService = new LocationService(this, new ScheduleDBHelper(this), sharedPreferences);

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

    // 위치 권한 요청 메소드
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
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
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 권한이 부여되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
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

        // 위치 업데이트 시작
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationService.startLocationUpdates(selectedDate);
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // 위치가 변경되었을 때 처리할 코드
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("LocationUpdate", "위치 변경: " + currentLocation.latitude + ", " + currentLocation.longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 상태 변경 처리
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        // 위치 제공자가 활성화된 경우 처리
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // 위치 제공자가 비활성화된 경우 처리
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 위치 업데이트를 멈추기 위해 등록된 리스너를 해제합니다.
        if (locationManager != null) {
            locationManager.removeUpdates(this);  // MainActivity를 LocationListener로 등록했으므로 이를 해제합니다.
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
