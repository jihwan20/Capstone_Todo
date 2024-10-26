package com.bjh.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // 선택한 날짜 정보를 String 형태로 변환
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            // ScheduleListActivity로 날짜 정보 전달
            Intent intent = new Intent(CalendarActivity.this, ScheduleListActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });
    }
}
