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
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_SCHEDULES = "schedules";

    // 일정 테이블 컬럼
    private static final String COLUMN_SCHEDULE_ID = "schedule_id";
    private static final String COLUMN_SCHEDULE_DATE = "schedule_date";
    private static final String COLUMN_SCHEDULE_TEXT = "schedule_text";
    private static final String COLUMN_USER_ID_FK = "user_id_fk";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_LOCATION = "location";

    public ScheduleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSchedulesTable = "CREATE TABLE " + TABLE_SCHEDULES + " (" +
                COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SCHEDULE_DATE + " TEXT, " +
                COLUMN_SCHEDULE_TEXT + " TEXT, " +
                COLUMN_USER_ID_FK + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_LOCATION + " TEXT)";
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
        values.put(COLUMN_USER_ID_FK, schedule.getUserId());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_LOCATION, schedule.getLocation());

        db.insert(TABLE_SCHEDULES, null, values);
        db.close();
    }

    // 일정 목록 가져오기 메서드
    public List<ScheduleDTO> getSchedulesByDate(String date, String userId) {
        List<ScheduleDTO> scheduleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULES +
                        " WHERE " + COLUMN_SCHEDULE_DATE + " = ? AND " + COLUMN_USER_ID_FK + " = ?",
                new String[]{date, userId});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID));
                String scheduleDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_DATE));
                String scheduleText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TEXT));
                String userIdFk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));

                scheduleList.add(new ScheduleDTO(id, scheduleDate, scheduleText, userIdFk, startTime, endTime, location));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }

    // 일정 ID로 가져오기 메서드
    public ScheduleDTO getSchedulesById(int scheduleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ScheduleDTO schedule = null;

        Cursor cursor = db.query(TABLE_SCHEDULES, null,
                COLUMN_SCHEDULE_ID + " = ?", new String[]{String.valueOf(scheduleId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String scheduleDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_DATE));
            String scheduleText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TEXT));
            String userIdFk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK));
            String startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));

            schedule = new ScheduleDTO(scheduleId, scheduleDate, scheduleText, userIdFk, startTime, endTime, location);
            cursor.close();
        }
        db.close();
        return schedule;
    }

    // 일정 삭제 메서드
    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, COLUMN_SCHEDULE_ID + " = ?", new String[]{String.valueOf(scheduleId)});
        db.close();
    }

    // 일정 수정 메서드
    public void updateSchedule(ScheduleDTO schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SCHEDULE_DATE, schedule.getScheduleDate());
        values.put(COLUMN_SCHEDULE_TEXT, schedule.getScheduleText());
        values.put(COLUMN_USER_ID_FK, schedule.getUserId());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_LOCATION, schedule.getLocation());

        db.update(TABLE_SCHEDULES, values, COLUMN_SCHEDULE_ID + " = ?", new String[]{String.valueOf(schedule.getScheduleId())});
        db.close();
    }
}
