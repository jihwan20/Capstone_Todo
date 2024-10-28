package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ScheduleDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MAP = 1;

    private EditText scheduleTextView, locationTextView;
    private TimePicker timePickerStart, timePickerEnd;
    private TextView selectedDateTextView;
    private TextView textViewStartTime, textViewEndTime; // TimePicker 대신 사용할 TextView 추가
    private Button btnEdit, btnDelete;
    private ScheduleDBHelper scheduleDBHelper;
    private SharedPreferences sharedPreferences;
    private int scheduleId;
    private boolean isStartPickerVisible = false;
    private boolean isEndPickerVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        initializeViews();
        setupDateTextView();

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        scheduleDBHelper = new ScheduleDBHelper(this);
        scheduleId = getIntent().getIntExtra("scheduleId", -1);

        if (scheduleId != -1) {
            loadScheduleDetails(scheduleId);
        }

        btnEdit.setOnClickListener(v -> updateSchedule());
        btnDelete.setOnClickListener(v -> deleteSchedule());
    }

    private void initializeViews() {
        scheduleTextView = findViewById(R.id.scheduleTextView);
        locationTextView = findViewById(R.id.editTextLocation);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);

        // TimePicker 대신 사용할 TextView
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 뒤로가기 버튼 클릭 리스너 추가
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // 툴바 제목 없애기
        getSupportActionBar().setTitle(""); // 빈 문자열로 제목 제거

        findViewById(R.id.btnFindAddress).setOnClickListener(view -> {
            Intent intent = new Intent(ScheduleDetailActivity.this, MapActivity.class);
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
    }

    // 선택된 날짜 텍스트뷰 설정 메서드
    private void setupDateTextView() {
        String selectedDate = getIntent().getStringExtra("selectedDate");
        selectedDateTextView.setText(String.format("날짜: %s", selectedDate)); // 날짜 표시
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadScheduleDetails(int scheduleId) {
        ScheduleDTO schedule = scheduleDBHelper.getSchedulesById(scheduleId);

        if (schedule != null) {
            scheduleTextView.setText(schedule.getScheduleText());
            String[] startTimeParts = schedule.getStartTime().split(":");
            timePickerStart.setHour(Integer.parseInt(startTimeParts[0]));
            timePickerStart.setMinute(Integer.parseInt(startTimeParts[1]));
            textViewStartTime.setText(schedule.getStartTime()); // 시간 텍스트 설정

            String[] endTimeParts = schedule.getEndTime().split(":");
            timePickerEnd.setHour(Integer.parseInt(endTimeParts[0]));
            timePickerEnd.setMinute(Integer.parseInt(endTimeParts[1]));
            textViewEndTime.setText(schedule.getEndTime()); // 시간 텍스트 설정

            locationTextView.setText(schedule.getLocation());
        }
    }

    private void updateSchedule() {
        String updatedScheduleText = scheduleTextView.getText().toString();
        String updatedStartTime = String.format("%02d:%02d",
                timePickerStart.getHour(), timePickerStart.getMinute());
        String updatedEndTime = String.format("%02d:%02d",
                timePickerEnd.getHour(), timePickerEnd.getMinute());
        String updatedLocation = locationTextView.getText().toString();

        // schedule 객체 생성
        ScheduleDTO schedule = scheduleDBHelper.getSchedulesById(scheduleId); // 현재 일정 가져오기

        ScheduleDTO updatedSchedule = new ScheduleDTO(scheduleId,
                schedule.getScheduleDate(), // 날짜 정보
                updatedScheduleText,
                sharedPreferences.getString("userId", "User"),
                updatedStartTime,
                updatedEndTime,
                updatedLocation);

        scheduleDBHelper.updateSchedule(updatedSchedule);
        Toast.makeText(this, "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteSchedule() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("정말로 이 일정을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    scheduleDBHelper.deleteSchedule(scheduleId);
                    Toast.makeText(this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 선택된 주소를 받는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            if (data != null) {
                String selectedAddress = data.getStringExtra("selectedAddress");
                if (selectedAddress != null) {
                    locationTextView.setText(selectedAddress);
                }
            }
        }
    }
}
