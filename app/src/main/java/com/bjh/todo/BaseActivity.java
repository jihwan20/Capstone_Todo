package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // 로그인 상태가 아니면 LoginActivity로 리디렉션
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // 현재 Activity 종료
        }
    }
}
