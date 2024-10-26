package com.bjh.todo;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    private ScheduleDBHelper scheduleDBHelper;
    private UserDBHelper userDBHelper;
    private int loggedInUserNo;
    private EditText editTextTitle, editTextLocation;
    private TimePicker timePickerStart, timePickerEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        // DBHelper 초기화
        scheduleDBHelper = new ScheduleDBHelper(this);
        userDBHelper = new UserDBHelper(this);

        // 로그인한 사용자 ID를 SharedPreferences에서 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String loggedInUserId = sharedPreferences.getString("userId", null);
        if (loggedInUserId == null) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            finish(); // 로그인 정보가 없으면 페이지 종료
            return;
        }

        // 로그인한 사용자의 user_no를 DB에서 조회
        loggedInUserNo = userDBHelper.getLoggedInUserNo(loggedInUserId);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);

        findViewById(R.id.btnSave).setOnClickListener(view -> saveSchedule());
    }

    private void saveSchedule() {
        String title = editTextTitle.getText().toString();
        String location = editTextLocation.getText().toString();
        String date = getIntent().getStringExtra("selectedDate");

        int startHour = timePickerStart.getHour();
        int startMinute = timePickerStart.getMinute();
        String startTime = String.format("%02d:%02d", startHour, startMinute);

        int endHour = timePickerEnd.getHour();
        int endMinute = timePickerEnd.getMinute();
        String endTime = String.format("%02d:%02d", endHour, endMinute);

        ScheduleDTO schedule = new ScheduleDTO(0, date, title, loggedInUserNo, startTime, endTime, location);
        scheduleDBHelper.insertSchedule(schedule);

        finish(); // 일정 등록 후 이전 화면으로 이동
    }
}