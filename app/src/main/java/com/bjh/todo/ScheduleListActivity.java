package com.bjh.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleListActivity extends AppCompatActivity {

    private RecyclerView scheduleRecyclerView; // 일정 목록을 표시할 리사이클러 뷰
    private ScheduleAdapter scheduleAdapter; // 일정 어댑터
    private ScheduleDBHelper scheduleDBHelper; // 일정 데이터베이스 헬퍼
    private SharedPreferences sharedPreferences; // 사용자 설정 저장을 위한 SharedPreferences
    private String selectedDate; // 선택된 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list); // 레이아웃 설정

        initializeViews(); // 뷰 초기화

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        selectedDate = getIntent().getStringExtra("selectedDate"); // MainActivity에서 전달받은 날짜
        loadSchedulesForDate(selectedDate); // 해당 날짜의 일정 로드
    }

    private void initializeViews() {
        // 뷰 초기화
        scheduleRecyclerView = findViewById(R.id.scheduleListRecyclerView); // 리사이클러 뷰 초기화
        scheduleDBHelper = new ScheduleDBHelper(this); // 데이터베이스 헬퍼 초기화
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE); // SharedPreferences 초기화

        // 리사이클러 뷰의 레이아웃 매니저 및 어댑터 설정
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(null, scheduleId -> {
            // 일정 선택 시 상세 보기로 이동
            Intent intent = new Intent(ScheduleListActivity.this, ScheduleDetailActivity.class);
            intent.putExtra("scheduleId", scheduleId);
            startActivity(intent);
        });
        scheduleRecyclerView.setAdapter(scheduleAdapter); // 어댑터 설정
    }

    private void loadSchedulesForDate(String date) {
        String userId = sharedPreferences.getString("userId", "User"); // 사용자 ID 가져오기
        List<ScheduleDTO> schedules = scheduleDBHelper.getSchedulesByDate(date, userId); // 일정 로드

        // 시작 시간에 따라 일정 정렬
        schedules.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));

        // 일정 목록 업데이트
        scheduleAdapter.updateSchedules(schedules);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 선택된 날짜에 해당하는 일정을 다시 로드
        loadSchedulesForDate(selectedDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 이전 액티비티로 돌아가기
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
