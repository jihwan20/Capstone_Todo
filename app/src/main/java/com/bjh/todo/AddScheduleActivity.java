package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class AddScheduleActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MAP = 1; // 주소 선택을 위한 요청 코드

    private ScheduleDBHelper scheduleDBHelper; // 데이터베이스 헬퍼
    private String loggedInUserId; // 로그인한 사용자 ID
    private EditText editTextTitle, editTextLocation; // 제목 및 위치 입력 필드
    private TimePicker timePickerStart, timePickerEnd; // 시작 및 종료 시간 피커
    private TextView selectedDateTextView; // 선택된 날짜 텍스트뷰
    private TextView textViewStartTime, textViewEndTime; // 시작 및 종료 시간 표시 텍스트뷰
    private boolean isStartPickerVisible = false; // 시작 시간 피커 가시성 플래그
    private boolean isEndPickerVisible = false; // 종료 시간 피커 가시성 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule); // 레이아웃 설정

        // 데이터베이스 헬퍼 및 로그인한 사용자 ID 초기화
        scheduleDBHelper = new ScheduleDBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getString("userId", null);

        // 뷰 초기화 및 설정 메서드 호출
        initializeViews();
        setupToolbar();
        setupDateTextView();
        setupAddressButton();
        setupSaveButton();
    }

    // 뷰 초기화 메서드
    private void initializeViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);

        // 시간 텍스트뷰 클릭 리스너 추가
        textViewStartTime.setOnClickListener(view -> {
            isStartPickerVisible = !isStartPickerVisible;
            timePickerStart.setVisibility(isStartPickerVisible ? View.VISIBLE : View.GONE);
            if (isStartPickerVisible) {
                timePickerStart.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
                    textViewStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                });
            }
        });

        textViewEndTime.setOnClickListener(view -> {
            isEndPickerVisible = !isEndPickerVisible;
            timePickerEnd.setVisibility(isEndPickerVisible ? View.VISIBLE : View.GONE);
            if (isEndPickerVisible) {
                timePickerEnd.setOnTimeChangedListener((view12, hourOfDay, minute) -> {
                    textViewEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                });
            }
        });
    }

    // 툴바 설정 메서드
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // 툴바를 액션바로 설정

        // 뒤로가기 버튼 클릭 리스너 추가
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // 이전 Activity로 돌아가기

        // 툴바 제목 없애기
        getSupportActionBar().setTitle(""); // 빈 문자열로 제목 제거
    }

    // 선택된 날짜 텍스트뷰 설정 메서드
    private void setupDateTextView() {
        String selectedDate = getIntent().getStringExtra("selectedDate");
        selectedDateTextView.setText(String.format("날짜: %s", selectedDate)); // 날짜 표시
    }

    // 주소 선택 버튼 설정 메서드
    private void setupAddressButton() {
        findViewById(R.id.btnFindAddress).setOnClickListener(view -> {
            Intent intent = new Intent(AddScheduleActivity.this, MapActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP); // 주소 선택 화면으로 이동
        });
    }

    // 저장 버튼 설정 메서드
    private void setupSaveButton() {
        findViewById(R.id.btnSave).setOnClickListener(view -> saveSchedule()); // 저장 클릭 리스너 추가
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            String selectedAddress = data.getStringExtra("selectedAddress");
            if (selectedAddress != null) {
                editTextLocation.setText(selectedAddress); // 주소 입력
            } else {
                Toast.makeText(this, "주소를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show(); // 오류 메시지 표시
            }
        }
    }

    // 일정 저장 메서드
    private void saveSchedule() {
        String title = editTextTitle.getText().toString();
        String location = editTextLocation.getText().toString();
        String date = getIntent().getStringExtra("selectedDate");

        // 사용자가 선택한 시작 및 종료 시간을 가져옴
        String startTime = textViewStartTime.getText().toString();
        String endTime = textViewEndTime.getText().toString();

        // 일정 DTO 생성 및 데이터베이스에 저장
        ScheduleDTO schedule = new ScheduleDTO(0, date, title, loggedInUserId, startTime, endTime, location);
        scheduleDBHelper.insertSchedule(schedule);

        Toast.makeText(this, "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show(); // 저장 완료 메시지 표시
        finish(); // Activity 종료
    }
}
