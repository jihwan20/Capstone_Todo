package com.bjh.todo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddScheduleActivity extends AppCompatActivity {

    private ScheduleDBHelper scheduleDBHelper;
    private String loggedInUserId;
    private EditText editTextTitle, editTextLocation;
    private TimePicker timePickerStart, timePickerEnd;
    private TextView selectedDateTextView; // 선택된 날짜 표시할 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        scheduleDBHelper = new ScheduleDBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getString("userId", null);

        if (loggedInUserId == null) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);
        selectedDateTextView = findViewById(R.id.selectedDateTextView); // 추가된 부분

        // 선택된 날짜를 가져와서 표시
        String selectedDate = getIntent().getStringExtra("selectedDate");
        selectedDateTextView.setText(String.format("선택된 날짜: %s", selectedDate));

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

        ScheduleDTO schedule = new ScheduleDTO(0, date, title, loggedInUserId, startTime, endTime, location);
        scheduleDBHelper.insertSchedule(schedule);

        Toast.makeText(this, "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
