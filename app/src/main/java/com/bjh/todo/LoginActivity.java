package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText userIdInput, userPwInput;
    private Button loginButton;
    private TextView signUpButton;
    private UserDBHelper dbuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 로그인 액티비티 종료
            return;
        }

        // UI 및 이벤트 설정 코드...
        userIdInput = findViewById(R.id.user_id);
        userPwInput = findViewById(R.id.user_pw);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signUp_button);

        dbuHelper = new UserDBHelper(this); // DBHelper 인스턴스 생성

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdInput.getText().toString().trim();
                String userPw = userPwInput.getText().toString().trim();

                // 입력 검증
                if (userId.isEmpty() || userPw.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 로그인 확인
                if (dbuHelper.checkUser(userId, userPw)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userId", userId);
                    editor.apply();

                    // MainActivity로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 로그인 액티비티 종료
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼 처리
        super.onBackPressed();
        finish(); // 현재 Activity 종료
    }
}
