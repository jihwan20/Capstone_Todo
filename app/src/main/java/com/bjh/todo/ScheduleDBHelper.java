package com.bjh.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SCHEDULES = "schedules";

    // 일정 테이블 컬럼
    private static final String COLUMN_SCHEDULE_ID = "schedule_id";
    private static final String COLUMN_SCHEDULE_DATE = "schedule_date";
    private static final String COLUMN_SCHEDULE_TEXT = "schedule_text";
    private static final String COLUMN_USER_NO_FK = "user_no_fk";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_LOCATION = "location";

    public ScheduleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSchedulesTable = "CREATE TABLE " + TABLE_SCHEDULES + " ("
                + COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SCHEDULE_DATE + " TEXT, "
                + COLUMN_SCHEDULE_TEXT + " TEXT, "
                + COLUMN_USER_NO_FK + " INTEGER, "
                + COLUMN_START_TIME + " TEXT, "
                + COLUMN_END_TIME + " TEXT, "
                + COLUMN_LOCATION + " TEXT)";
        db.execSQL(createSchedulesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        onCreate(db);
    }

    // 일정 추가 메서드
    public void insertSchedule(ScheduleDTO schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SCHEDULE_DATE, schedule.getScheduleDate());
        values.put(COLUMN_SCHEDULE_TEXT, schedule.getScheduleText());
        values.put(COLUMN_USER_NO_FK, schedule.getUserNoFk());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_LOCATION, schedule.getLocation());

        db.insert(TABLE_SCHEDULES, null, values);
        db.close();
    }

    // 일정 목록 가져오기 메서드
    public List<ScheduleDTO> getSchedulesByDate(String date) {
        List<ScheduleDTO> scheduleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 정확한 컬럼 이름 사용
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULES + " WHERE " + COLUMN_SCHEDULE_DATE + " = ?", new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID));
                String scheduleDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_DATE));
                String scheduleText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TEXT));
                int userNoFk = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_NO_FK));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));

                scheduleList.add(new ScheduleDTO(id, scheduleDate, scheduleText, userNoFk, startTime, endTime, location));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }
}
