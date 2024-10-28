package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddScheduleActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MAP = 1;

    private ScheduleDBHelper scheduleDBHelper;
    private String loggedInUserId;
    private EditText editTextTitle, editTextLocation;
    private TimePicker timePickerStart, timePickerEnd;
    private TextView selectedDateTextView;
    private TextView textViewStartTime, textViewEndTime;
    private boolean isStartPickerVisible = false;
    private boolean isEndPickerVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        scheduleDBHelper = new ScheduleDBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getString("userId", null);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        // 추가한 TextView
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);

        String selectedDate = getIntent().getStringExtra("selectedDate");
        selectedDateTextView.setText(String.format("선택된 날짜: %s", selectedDate));

        findViewById(R.id.btnFindAddress).setOnClickListener(view -> {
            Intent intent = new Intent(AddScheduleActivity.this, MapActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });

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

        timePickerStart.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            textViewStartTime.setText(selectedTime);
            timePickerStart.setVisibility(View.GONE);
        });

        timePickerEnd.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            textViewEndTime.setText(selectedTime);
            timePickerEnd.setVisibility(View.GONE);
        });

        findViewById(R.id.btnSave).setOnClickListener(view -> saveSchedule());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            String selectedAddress = data.getStringExtra("selectedAddress");
            if (selectedAddress != null) {
                editTextLocation.setText(selectedAddress); // 한글 주소 입력
            } else {
                Toast.makeText(this, "주소를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveSchedule() {
        String title = editTextTitle.getText().toString();
        String location = editTextLocation.getText().toString();
        String date = getIntent().getStringExtra("selectedDate");

        // 사용자가 선택한 시작 및 종료 시간을 가져옴
        String startTime = textViewStartTime.getText().toString();
        String endTime = textViewEndTime.getText().toString();

        ScheduleDTO schedule = new ScheduleDTO(0, date, title, loggedInUserId, startTime, endTime, location);
        scheduleDBHelper.insertSchedule(schedule);

        Toast.makeText(this, "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
