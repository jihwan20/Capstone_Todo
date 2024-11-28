package com.bjh.todo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;

public class NotificationHelper {

    // 알림 채널 생성
    public static final String CHANNEL_ID = "schedule_channel";
    public static final String CHANNEL_NAME = "Schedule Notifications";
    public static final String CHANNEL_DESCRIPTION = "Channel for schedule reminders";

    // 일정 알림 보내기
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // 위치 기반 알림 보내기
    public static void showLocationNotification(Context context, ScheduleDTO schedule) {
        // 일정의 위치와 사용자 위치가 50m 이내일 때 알림 생성
        String locationText = schedule.getScheduleText() + " 장소에 도착했습니다.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("위치 알림")
                .setContentText(locationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(schedule.getScheduleId(), builder.build());
        }
    }
    // 두 위치 간 거리 계산
    public static float calculateDistance(LatLng location1, LatLng location2) {
        float[] results = new float[1];
        Location.distanceBetween(location1.latitude, location1.longitude,
                location2.latitude, location2.longitude, results);
        return results[0];
    }
}
