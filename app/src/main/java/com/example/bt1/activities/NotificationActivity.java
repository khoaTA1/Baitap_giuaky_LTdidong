package com.example.bt1.activities;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.adapters.NotificationAdapter;
import com.example.bt1.models.Notification;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Chỉ reset badge counter, KHÔNG xóa nội dung thông báo
        SharedPreferencesManager.getInstance(this).resetNotificationCount();
    }

    private void loadNotifications() {
        String json = SharedPreferencesManager.getInstance(this).getNotifications();
        Type type = new TypeToken<ArrayList<Notification>>() {}.getType();
        notificationList = new Gson().fromJson(json, type);

        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }
    }
}
