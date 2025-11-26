package com.example.bt1.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notify {
    private static final String PREF_NAME = "notify_pref";
    private static final String KEY_NOTIFICATION_ENABLE = "notification_enabled";
    private static final String KEY_VIBRATION_ENABLE = "vibration_enabled";

    public static final String CHANNEL_ID = "default_channel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "This is the default notification channel.";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.deleteNotificationChannel(CHANNEL_ID);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // cấu hình rung
            if (isVibrationEnabled(context)) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 200, 100, 300});  // Cấu hình rung
            } else {
                channel.enableVibration(false);  // Tắt rung
            }

            // Đăng ký channel trong NotificationManager
            //NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, String title, String message) {

        if (!isNotificationEnabled(context)) return;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (isVibrationEnabled(context)) {
            builder.setVibrate(new long[]{0, 200, 100, 300});
        }

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(1, builder.build());
    }

    public static void setNotificationEnabled(Context context, boolean enabled) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(KEY_NOTIFICATION_ENABLE, enabled).apply();
    }
    public static void setVibrationEnabled(Context context, boolean enabled) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(KEY_VIBRATION_ENABLE, enabled).apply();
    }

    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_NOTIFICATION_ENABLE, true);
    }

    public static boolean isVibrationEnabled(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_VIBRATION_ENABLE, true);
    }
}
