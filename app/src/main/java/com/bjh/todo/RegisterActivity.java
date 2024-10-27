package com.bjh.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText userIdInput, userPwInput;
    private Button register;
    private UserDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userIdInput = findViewById(R.id.userId);
        userPwInput = findViewById(R.id.userPw);
        register = findViewById(R.id.register);
        dbHelper = new UserDBHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userIdInput.getText().toString().trim();
                String pass = userPwInput.getText().toString().trim();

                // 입력 검증
                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 아이디 중복 확인
                if (dbHelper.isUserExists(user)) {
                    Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 중복되지 않으면 회원가입 진행
                    UserDTO newUser = new UserDTO(user, pass); // User 객체 생성
                    dbHelper.insertUser(newUser); // User 객체를 전달
                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                    // 로그인 화면으로 이동
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();  // RegisterActivity 종료
                }
            }
        });
    }
}