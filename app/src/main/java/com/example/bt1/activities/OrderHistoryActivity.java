package com.example.bt1.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.adapters.OrderHistoryAdapter;
import com.example.bt1.models.Order;
import com.example.bt1.repositories.OrderRepo;
import com.example.bt1.utils.SharedPreferencesManager;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;
    private OrderRepo orderRepo;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Tìm empty state layout
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, orderList);
        recyclerView.setAdapter(adapter);
        
        // Initialize OrderRepo
        orderRepo = new OrderRepo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        // Lấy userId
        String userId = SharedPreferencesManager.getInstance(this).getUserId();
        
        if (userId == null) {
            Log.w(">>> OrderHistoryActivity", "User chưa đăng nhập");
            showEmptyState();
            return;
        }
        
        Log.d(">>> OrderHistoryActivity", "Loading orders for userId: " + userId);
        
        // Load orders từ Firebase (nguồn dữ liệu duy nhất)
        orderRepo.getOrdersByUserId(userId, orders -> {
            runOnUiThread(() -> {
                if (orders != null && !orders.isEmpty()) {
                    orderList.clear();
                    orderList.addAll(orders);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    if (emptyStateLayout != null) {
                        emptyStateLayout.setVisibility(View.GONE);
                    }
                    
                    Log.d(">>> OrderHistoryActivity", "Loaded " + orders.size() + " orders from Firebase");
                } else {
                    // Không có orders trong Firebase
                    Log.d(">>> OrderHistoryActivity", "No orders found for this user");
                    showEmptyState();
                }
            });
        });
    }
    
    private void showEmptyState() {
        Log.d(">>> OrderHistoryActivity", "Showing empty state");
        orderList.clear();
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.GONE);
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }
}

