package com.bjh.todo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MemberDetailsActivity extends AppCompatActivity {

    private EditText userIdEditText; // 사용자 ID 입력 필드
    private EditText userPwEditText; // 사용자 비밀번호 입력 필드
    private Button updateButton; // 비밀번호 수정 버튼
    private Button deleteButton; // 사용자 삭제 버튼
    private UserDBHelper userDBHelper; // 사용자 DB 헬퍼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        // SharedPreferences로 로그인 상태 확인
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // 로그인하지 않았다면 로그인 페이지로 이동
        if (!isLoggedIn) {
            Intent intent = new Intent(MemberDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
            return; // 추가적인 코드 실행 방지
        }

        userIdEditText = findViewById(R.id.userId);
        userPwEditText = findViewById(R.id.userPw);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 뒤로가기 버튼 클릭 리스너 추가
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // 툴바 제목 없애기
        getSupportActionBar().setTitle(""); // 빈 문자열로 제목 제거

        // Intent에서 사용자 ID 받기
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            userIdEditText.setText(userId); // 사용자 ID만 출력
            loadUserDetails(userId); // 사용자 세부 정보 로드
        }

        // 수정 버튼 클릭 리스너
        updateButton.setOnClickListener(v -> {
            String newPassword = userPwEditText.getText().toString();
            if (!newPassword.isEmpty()) {
                String hashedPassword = hashPassword(newPassword); // 비밀번호 암호화
                UserDTO user = new UserDTO();
                user.setUserId(userId);
                user.setUserPw(hashedPassword); // 암호화된 비밀번호 설정
                userDBHelper.updateUserPassword(user); // 비밀번호 업데이트 메소드 호출

                // 로그아웃 처리
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                Toast.makeText(this, "비밀번호가 수정되었습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();

                // 메인 화면으로 이동
                Intent intent = new Intent(MemberDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            // 사용자 확인 다이얼로그 생성
            new AlertDialog.Builder(this)
                    .setTitle("회원 탈퇴")
                    .setMessage("탈퇴하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        userDBHelper = new UserDBHelper(this);
                        String userIdString = userIdEditText.getText().toString(); // 사용자 ID 가져오기
                        userDBHelper.deleteUser(userIdString); // 삭제할 사용자 ID
                        Toast.makeText(this, "회원 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        // 로그아웃 처리
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.apply();

                        // 메인 화면으로 이동
                        Intent intent = new Intent(MemberDetailsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    })
                    .setNegativeButton("아니요", null) // "아니요" 버튼 클릭 시 아무 것도 하지 않음
                    .show(); // 다이얼로그 표시
        });
    }

    // 사용자 세부 정보를 로드하는 메서드 (비밀번호는 출력하지 않음)
    private void loadUserDetails(String userId) {
        userDBHelper = new UserDBHelper(this);
        UserDTO user = userDBHelper.getUserById(userId); // 사용자 정보 가져오기

        if (user != null) {
            // 비밀번호는 출력하지 않음
        } else {
            Toast.makeText(this, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 비밀번호를 SHA-256 해시로 암호화하는 메서드
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
