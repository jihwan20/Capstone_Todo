package com.bjh.todo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class AlarmHelper {

    public static void setAlarm(Context context, ScheduleDTO schedule) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String[] startTimeParts = schedule.getStartTime().split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));

        // 알람 시간을 1시간 전으로 설정
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("scheduleText", schedule.getScheduleText());

        // PendingIntent 설정
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, schedule.getScheduleId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 알람 설정
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
