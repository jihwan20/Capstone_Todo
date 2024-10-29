package com.bjh.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmHelper {

    public static void setAlarm(Context context, ScheduleDTO schedule) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // scheduleDate를 기준으로 Calendar 객체 생성
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = dateFormat.parse(schedule.getScheduleDate());
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return; // 날짜 파싱 오류 시 알람 설정하지 않음
        }

        // 시간 설정
        String[] startTimeParts = schedule.getStartTime().split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
        calendar.set(Calendar.SECOND, 0); // 초를 0으로 설정

        // 알람 시간을 1시간 전으로 설정
        Calendar alarmTime = (Calendar) calendar.clone();
        alarmTime.add(Calendar.HOUR_OF_DAY, -1);

        // 현재 시간
        Calendar now = Calendar.getInstance();

        // 현재 시간이 알람 시간보다 늦다면 알람 설정하지 않음
        if (now.after(alarmTime)) {
            return; // 알람을 설정하지 않음
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("scheduleText", schedule.getScheduleText());
        intent.putExtra("scheduleId", schedule.getScheduleId());

        // PendingIntent 설정
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, schedule.getScheduleId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 알람 설정
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }
    }

    // 알람 취소 메서드
    public static void cancelAlarm(Context context, ScheduleDTO schedule) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, schedule.getScheduleId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
