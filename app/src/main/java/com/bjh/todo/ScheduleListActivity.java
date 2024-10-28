package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleListActivity extends AppCompatActivity {

    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private ScheduleDBHelper scheduleDBHelper;
    private SharedPreferences sharedPreferences;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        selectedDate = getIntent().getStringExtra("selectedDate"); // MainActivity에서 전달받은 날짜
        initializeViews(); // 뷰 초기화

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        loadSchedulesForDate(selectedDate); // 해당 날짜의 일정 로드
    }

    private void initializeViews() {
        scheduleRecyclerView = findViewById(R.id.scheduleListRecyclerView);
        scheduleDBHelper = new ScheduleDBHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(null, scheduleId -> {
            Intent intent = new Intent(ScheduleListActivity.this, ScheduleDetailActivity.class);
            intent.putExtra("scheduleId", scheduleId);
            intent.putExtra("selectedDate", selectedDate); // 선택된 날짜 추가
            startActivity(intent);
        });
        scheduleRecyclerView.setAdapter(scheduleAdapter);
    }

    private void loadSchedulesForDate(String date) {
        String userId = sharedPreferences.getString("userId", "User");
        List<ScheduleDTO> schedules = scheduleDBHelper.getSchedulesByDate(date, userId);
        schedules.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
        scheduleAdapter.updateSchedules(schedules);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedulesForDate(selectedDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
