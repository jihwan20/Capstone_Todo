package com.bjh.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ScheduleListActivity extends AppCompatActivity {

    private TextView dateTextView;
    private FloatingActionButton fabAddSchedule;
    private RecyclerView recyclerViewSchedules;
    private ScheduleDBHelper scheduleDBHelper;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleDTO> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        dateTextView = findViewById(R.id.dateTextView);
        fabAddSchedule = findViewById(R.id.fab_add_schedule);
        recyclerViewSchedules = findViewById(R.id.recyclerViewSchedules);

        // RecyclerView 설정
        String selectedDate = getIntent().getStringExtra("selectedDate");
        dateTextView.setText(selectedDate);

        recyclerViewSchedules.setLayoutManager(new LinearLayoutManager(this));
        scheduleDBHelper = new ScheduleDBHelper(this);
        scheduleList = scheduleDBHelper.getSchedulesByDate(selectedDate);
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        recyclerViewSchedules.setAdapter(scheduleAdapter);

        fabAddSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleListActivity.this, AddScheduleActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 목록 새로고침
        String selectedDate = getIntent().getStringExtra("selectedDate");
        scheduleList.clear();
        scheduleList.addAll(scheduleDBHelper.getSchedulesByDate(selectedDate));
        scheduleAdapter.notifyDataSetChanged();
    }
}
