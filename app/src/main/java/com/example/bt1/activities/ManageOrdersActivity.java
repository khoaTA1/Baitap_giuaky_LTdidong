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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class ManageOrdersActivity extends AppCompatActivity {

    private ImageView btnBack, btnRefresh;
    private RecyclerView recyclerOrders;
    private LinearLayout emptyState;
    private TextView tvPending, tvShipping, tvCompleted;
    private TabLayout tabLayout;
    
    private OrderRepo orderRepo;
    private OrderHistoryAdapter adapter;
    private List<Order> allOrders;
    private List<Order> filteredOrders;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        initViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnRefresh = findViewById(R.id.btn_refresh);
        recyclerOrders = findViewById(R.id.recycler_orders);
        emptyState = findViewById(R.id.empty_state);
        tvPending = findViewById(R.id.tv_pending);
        tvShipping = findViewById(R.id.tv_shipping);
        tvCompleted = findViewById(R.id.tv_completed);
        tabLayout = findViewById(R.id.tab_layout);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize data structures
        allOrders = new ArrayList<>();
        filteredOrders = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, filteredOrders, true); // true = admin view
        adapter.setOnOrderActionListener(new OrderHistoryAdapter.OnOrderActionListener() {
            @Override
            public void onConfirmOrder(Order order, int position) {
                showConfirmDialog(order, position);
            }
        });
        recyclerOrders.setAdapter(adapter);
        
        // Initialize Firebase
        orderRepo = new OrderRepo();
        db = FirebaseFirestore.getInstance();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnRefresh.setOnClickListener(v -> {
            loadData();
        });
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterOrdersByStatus(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadData() {
        // Load all orders from Firebase (admin sees all orders)
        db.collection("orders")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allOrders.clear();
                
                int pendingCount = 0;
                int shippingCount = 0;
                int completedCount = 0;
                
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    try {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            allOrders.add(order);
                            
                            // Count by status
                            String status = order.getStatus();
                            if ("Đang xử lý".equals(status) || "Chờ xác nhận".equals(status)) {
                                pendingCount++;
                            } else if ("Đang giao".equals(status)) {
                                shippingCount++;
                            } else if ("Hoàn thành".equals(status) || "Đã giao".equals(status)) {
                                completedCount++;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ManageOrders", "Error parsing order: " + doc.getId(), e);
                    }
                }
                
                // Update statistics
                tvPending.setText(String.valueOf(pendingCount));
                tvShipping.setText(String.valueOf(shippingCount));
                tvCompleted.setText(String.valueOf(completedCount));
                
                // Show all orders by default
                filterOrdersByStatus(0);
                
                Log.d("ManageOrders", "Loaded " + allOrders.size() + " orders");
            })
            .addOnFailureListener(e -> {
                Log.e("ManageOrders", "Error loading orders", e);
                showEmptyState();
            });
    }

    private void filterOrdersByStatus(int position) {
        filteredOrders.clear();
        
        switch (position) {
            case 0: // All
                filteredOrders.addAll(allOrders);
                break;
            case 1: // Pending
                for (Order order : allOrders) {
                    if ("Đang xử lý".equals(order.getStatus()) || "Chờ xác nhận".equals(order.getStatus())) {
                        filteredOrders.add(order);
                    }
                }
                break;
            case 2: // Confirmed
                for (Order order : allOrders) {
                    if ("Đã xác nhận".equals(order.getStatus())) {
                        filteredOrders.add(order);
                    }
                }
                break;
            case 3: // Shipping
                for (Order order : allOrders) {
                    if ("Đang giao".equals(order.getStatus())) {
                        filteredOrders.add(order);
                    }
                }
                break;
            case 4: // Completed
                for (Order order : allOrders) {
                    if ("Hoàn thành".equals(order.getStatus()) || "Đã giao".equals(order.getStatus())) {
                        filteredOrders.add(order);
                    }
                }
                break;
            case 5: // Cancelled
                for (Order order : allOrders) {
                    if ("Đã hủy".equals(order.getStatus())) {
                        filteredOrders.add(order);
                    }
                }
                break;
        }
        
        adapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (filteredOrders.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }
    
    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        recyclerOrders.setVisibility(View.GONE);
    }
    
    private void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
        recyclerOrders.setVisibility(View.VISIBLE);
    }
    
    private void showConfirmDialog(Order order, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận đơn hàng")
            .setMessage("Bạn có chắc muốn xác nhận đơn hàng #" + order.getOrderId() + "?\n\nSau khi xác nhận, khách hàng sẽ không thể hủy đơn hàng.")
            .setPositiveButton("Xác nhận", (dialog, which) -> {
                confirmOrder(order, position);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void confirmOrder(Order order, int position) {
        orderRepo.updateOrderStatus(order.getOrderId(), "Đã xác nhận", new OrderRepo.OnCompleteListener() {
            @Override
            public void onComplete(boolean success, String message) {
                if (success) {
                    Toast.makeText(ManageOrdersActivity.this, "Đã xác nhận đơn hàng #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
                    // Update local data
                    order.setStatus("Đã xác nhận");
                    adapter.notifyItemChanged(position);
                    // Reload to update statistics
                    loadData();
                } else {
                    Toast.makeText(ManageOrdersActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
