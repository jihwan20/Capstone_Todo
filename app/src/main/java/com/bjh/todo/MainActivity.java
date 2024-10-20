package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView loginInfo, weeklyScheduleText;
    private Button todaySchedule, calendar, notifications, logout;
    private SharedPreferences sharedPreferences;

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

        // 로그인 상태 체크
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userId = sharedPreferences.getString("userId", "User");
            loginInfo.setText(userId + "님 환영합니다!");
        } else {
            loginInfo.setText("로그인 해주세요");
            weeklyScheduleText.setText("로그인 해주세요");

            // login_info 클릭 이벤트
            loginInfo.setOnClickListener(v -> {
                if (!isLoggedIn) {
                    // 로그인 페이지로 이동
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            // weekly_schedule_text 클릭 이벤트
            weeklyScheduleText.setOnClickListener(v -> {
                if (!isLoggedIn) {
                    // 로그인 페이지로 이동
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        // 로그아웃 이벤트 처리
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            //로그아웃 처리
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.remove("userId");
            editor.apply();

            // 메인 페이지로 이동
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 오늘 일정 클릭 시
        todaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 오늘 일정 동작
            }
        });

        // 캘린더 클릭 시
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캘린더 동작
            }
        });

        // 알림 클릭 시
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알림 동작
            }
        });
    }
}
