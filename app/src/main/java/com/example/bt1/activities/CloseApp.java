package com.example.bt1.activities;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.bt1.utils.DBHelper;

public class CloseApp extends Application implements LifecycleObserver {

    private DBHelper dbhelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbhelper = new DBHelper(this);

        // Đăng ký observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver());
    }

    private class AppLifecycleObserver implements DefaultLifecycleObserver {

        @Override
        public void onStop(LifecycleOwner owner) {
            // App xuống background
            dbhelper.clearTable();
            Log.d(">>> Close App", "Đã xóa bảng product");
        }
    }
}
