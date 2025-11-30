package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.bt1.R;
import com.example.bt1.utils.Notify;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingActivity extends AppCompatActivity {
    private MaterialSwitch darkThemeSwitch, notifyEnableSwitch, vibrateOnNotifySwitch;
    private ImageView backButton;
    private TextView textVersionNumber, textBuildNumber;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        boolean isNotifyEnable = Notify.isNotificationEnabled(this);
        boolean isVibrateEnable = Notify.isVibrationEnabled(this);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        backButton = findViewById(R.id.back_button);

        darkThemeSwitch = findViewById(R.id.dark_theme_switch);
        notifyEnableSwitch = findViewById(R.id.notify_enable_switch);
        vibrateOnNotifySwitch = findViewById(R.id.enable_vibrate_on_notify_switch);
        
        textVersionNumber = findViewById(R.id.text_version_number);
        textBuildNumber = findViewById(R.id.text_build_number);
        
        // Set version info from BuildConfig
        setVersionInfo();

        // Đăng ký callback khi nhấn nút quay lại
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backBtnProcess();
            }
        });

        backButton.setOnClickListener(v -> {
            backBtnProcess();
        });

        // áp dụng lại màu thumb sau khi recreate activity
        updateSwitchColor(darkThemeSwitch, isDark);
        updateSwitchColor(notifyEnableSwitch, isNotifyEnable);
        updateSwitchColor(vibrateOnNotifySwitch, isVibrateEnable);

        // cài đặt nền tối
        darkThemeSwitch.setChecked(isDark);

        darkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // Lưu trạng thái
            prefs.edit().putBoolean("dark_mode", isChecked).apply();

            // Đổi theme
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            recreate();
        });

        // cài đặt bật thông báo
        notifyEnableSwitch.setChecked(isNotifyEnable);

        notifyEnableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Notify.setNotificationEnabled(this, isChecked);

            updateSwitchColor(notifyEnableSwitch, isChecked);
        });

        // cài đặt bật rung khi thông báo
        vibrateOnNotifySwitch.setChecked(isVibrateEnable);

        vibrateOnNotifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Notify.setVibrationEnabled(this, isChecked);

            Notify.createNotificationChannel(this);

            updateSwitchColor(vibrateOnNotifySwitch, isChecked);
        });
        
        // Setup bottom navigation
        setupBottomNavigation();
    }
    
    private void setVersionInfo() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            textVersionNumber.setText(packageInfo.versionName);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                textBuildNumber.setText(String.valueOf(packageInfo.getLongVersionCode()));
            } else {
                textBuildNumber.setText(String.valueOf(packageInfo.versionCode));
            }
        } catch (PackageManager.NameNotFoundException e) {
            textVersionNumber.setText("1.0");
            textBuildNumber.setText("1");
        }
    }

    // Hàm cập nhật màu của switch dựa trên trạng thái bật/tắt
    private void updateSwitchColor(MaterialSwitch sw, boolean isChecked) {
        int thumbColor = isChecked ? R.color.thumb_enable_color : R.color.thumb_disable_color;
        sw.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(this, thumbColor)));
    }

    private void backBtnProcess() {
        Intent intent = new android.content.Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void setupBottomNavigation() {
        // Đánh dấu mục "Setting" là đang được chọn
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_setting);
            
            // Gán sự kiện khi một mục được chọn
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_setting) {
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Intent intent = new Intent(this, CartActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                return false;
            });
        }
    }
}
