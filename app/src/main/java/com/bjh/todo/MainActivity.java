package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView loginInfo, weeklyScheduleText;
    private Button todaySchedule, calendar, notifications, logout;
    private SharedPreferences sharedPreferences;

    private static final String TAG = "MainActivity"; // 로그 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginInfo = findViewById(R.id.login_info);
        weeklyScheduleText = findViewById(R.id.weekly_schedule_text);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        todaySchedule = findViewById(R.id.today_schedule);
        calendar = findViewById(R.id.calendar);
        notifications = findViewById(R.id.notifications);
        logout = findViewById(R.id.logout_button);

        // 로그인 상태 체크
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d(TAG, "isLoggedIn: " + isLoggedIn); // 로그인 상태 출력

        if (isLoggedIn) {
            String userId = sharedPreferences.getString("userId", "User");
            loginInfo.setText(userId + "님 환영합니다!");
            weeklyScheduleText.setText("이번 주 일정이 없습니다."); // 기본 텍스트 설정
        } else {
            loginInfo.setText("로그인 해주세요");
            weeklyScheduleText.setText("로그인 해주세요");
        }

        // 로그아웃 이벤트 처리
        logout.setOnClickListener(v -> {
            // 로그아웃 처리
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.remove("userId");
            editor.apply();

            // 메인 페이지로 이동 (리셋)
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 오늘 일정 클릭 시
        /*todaySchedule.setOnClickListener(v -> {
            /if (isLoggedIn) {
                // 오늘 일정 Activity로 이동
                Intent intent = new Intent(MainActivity.this, TodayScheduleActivity.class);
                startActivity(intent);
            } else {
                promptLogin();
            }
        });
        */
        // 캘린더 클릭 시
        calendar.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            } else {
                promptLogin();
            }
        });

        // 알림 클릭 시
        notifications.setOnClickListener(v -> {
            if (isLoggedIn) {
                // 알림 Activity로 이동 (구현 필요)
            } else {
                promptLogin();
            }
        });

        // 로그인 페이지로 이동하는 클릭 이벤트
        loginInfo.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // weekly_schedule_text 클릭 이벤트
        weeklyScheduleText.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // 로그인 프롬프트 메서드
    private void promptLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
