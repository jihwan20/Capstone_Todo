package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button loginLogoutButton; // 로그인/로그아웃 버튼
    private Button notificationButton; // 알림 버튼
    private SharedPreferences sharedPreferences; // 사용자 설정 저장을 위한 SharedPreferences
    private boolean isLoggedIn; // 로그인 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // 레이아웃 설정

        // 뷰 초기화
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", false); // 로그인 상태 가져오기
        loginLogoutButton = findViewById(R.id.login_logout_button);
        notificationButton = findViewById(R.id.notification_button); // 알림 버튼 초기화

        updateLoginLogoutButton(); // 로그인/로그아웃 버튼 텍스트 업데이트

        // 로그인/로그아웃 버튼 클릭 리스너 설정
        loginLogoutButton.setOnClickListener(view -> {
            if (isLoggedIn) {
                // 로그아웃 처리
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.remove("userId");
                editor.apply();
                isLoggedIn = false;
                Toast.makeText(SettingsActivity.this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                updateLoginLogoutButton();

                // 메인 화면으로 이동
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 Activity 종료
            } else {
                // 로그인 화면으로 이동
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        });
/*
        // 알림 버튼 클릭 리스너 설정
        notificationButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, NotificationListActivity.class);
            startActivity(intent); // 알림 목록 화면으로 이동
        });

 */
    }

    private void updateLoginLogoutButton() {
        if (isLoggedIn) {
            loginLogoutButton.setText("로그아웃");
        } else {
            loginLogoutButton.setText("로그인");
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
        }
    }
}
