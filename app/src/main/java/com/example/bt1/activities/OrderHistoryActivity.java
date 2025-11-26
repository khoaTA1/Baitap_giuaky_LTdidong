package com.example.bt1.activities;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.adapters.OrderHistoryAdapter;
import com.example.bt1.models.Order;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(orderList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        String json = SharedPreferencesManager.getInstance(this).getOrders();
        Type type = new TypeToken<ArrayList<Order>>() {}.getType();
        List<Order> loadedOrders = new Gson().fromJson(json, type);

        if (loadedOrders != null) {
            orderList.clear();
            orderList.addAll(loadedOrders);
            adapter.notifyDataSetChanged();
        }
    }
}
