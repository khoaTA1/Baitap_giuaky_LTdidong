package com.example.bt1.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.bt1.R;
import com.example.bt1.activities.MainActivity;

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

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            if (isVibrationEnabled(context)) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 200, 100, 300});
            } else {
                channel.enableVibration(false);
            }

            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, String title, String message) {
        if (!isNotificationEnabled(context)) return;

        // Tạo Intent để mở MainActivity khi người dùng nhấn vào thông báo
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // Thay bằng icon của bạn
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent) // Gán hành động khi nhấn
                        .setAutoCancel(true); // Tự động xóa thông báo sau khi nhấn

        if (isVibrationEnabled(context)) {
            builder.setVibrate(new long[]{0, 200, 100, 300});
        }

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Sử dụng một ID duy nhất cho mỗi thông báo để tránh ghi đè
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Các phương thức SharedPreferences không đổi
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
