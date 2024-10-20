package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText userIdInput, userPwInput;
    private Button loginButton;
    private TextView signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdInput = findViewById(R.id.user_id);
        userPwInput = findViewById(R.id.user_pw);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signUp_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdInput.getText().toString();
                String userPw = userPwInput.getText().toString();

                // 로그인 확인 로직 추가 (예: DB에서 확인)

                // 로그인 성공 시 SharedPreferences에 정보 저장
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true); // 로그인 상태
                editor.putString("userId", userId); // 사용자 ID 저장
                editor.apply();

                // MainActivity로 돌아가기
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 로그인 액티비티 종료
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
}
