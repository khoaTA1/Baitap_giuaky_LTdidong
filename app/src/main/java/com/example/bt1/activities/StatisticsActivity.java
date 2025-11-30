package com.example.bt1.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Order;
import com.example.bt1.models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private ImageView btnBack, btnExport;
    private MaterialButton btnToday, btnWeek, btnMonth, btnCustom;
    private TextView tvRevenue, tvRevenueChange, tvTotalOrders, tvAvgOrder;
    private TextView tvProductsSold, tvNewCustomers;
    private TextView tvPendingCount, tvShippingCount, tvCompletedCount, tvCancelledCount;
    private ProgressBar progressPending, progressShipping, progressCompleted, progressCancelled;
    private RecyclerView recyclerTopProducts;
    private TextView tvNoProducts;
    
    private FirebaseFirestore db;
    private String currentRange = "today";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnExport = findViewById(R.id.btn_export);
        btnToday = findViewById(R.id.btn_today);
        btnWeek = findViewById(R.id.btn_week);
        btnMonth = findViewById(R.id.btn_month);
        btnCustom = findViewById(R.id.btn_custom);
        
        tvRevenue = findViewById(R.id.tv_revenue);
        tvRevenueChange = findViewById(R.id.tv_revenue_change);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvAvgOrder = findViewById(R.id.tv_avg_order);
        tvProductsSold = findViewById(R.id.tv_products_sold);
        tvNewCustomers = findViewById(R.id.tv_new_customers);
        
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvShippingCount = findViewById(R.id.tv_shipping_count);
        tvCompletedCount = findViewById(R.id.tv_completed_count);
        tvCancelledCount = findViewById(R.id.tv_cancelled_count);
        
        progressPending = findViewById(R.id.progress_pending);
        progressShipping = findViewById(R.id.progress_shipping);
        progressCompleted = findViewById(R.id.progress_completed);
        progressCancelled = findViewById(R.id.progress_cancelled);
        
        recyclerTopProducts = findViewById(R.id.recycler_top_products);
        tvNoProducts = findViewById(R.id.tv_no_products);
        
        recyclerTopProducts.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnExport.setOnClickListener(v -> {
            // TODO: Export data to Excel/PDF
        });
        
        btnToday.setOnClickListener(v -> loadDataForRange("today"));
        btnWeek.setOnClickListener(v -> loadDataForRange("week"));
        btnMonth.setOnClickListener(v -> loadDataForRange("month"));
        btnCustom.setOnClickListener(v -> {
            // TODO: Show date picker dialog
        });
    }

    private void loadData() {
        // TODO: Load statistics from Firebase/Database
        loadDataForRange("today");
    }

    private void loadDataForRange(String range) {
        currentRange = range;
        
        // Highlight selected button
        resetButtonStyles();
        switch (range) {
            case "today":
                btnToday.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                break;
            case "week":
                btnWeek.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                break;
            case "month":
                btnMonth.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                break;
        }
        
        // Calculate date range
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        Date startDate;
        
        switch (range) {
            case "today":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                break;
            case "week":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = calendar.getTime();
                break;
            case "month":
                calendar.add(Calendar.MONTH, -1);
                startDate = calendar.getTime();
                break;
            default:
                startDate = new Date(0); // All time
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String startDateStr = sdf.format(startDate);
        String endDateStr = sdf.format(endDate);
        
        // Load orders from Firebase
        db.collection("orders")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                double totalRevenue = 0;
                int totalOrders = 0;
                int productsSold = 0;
                int pendingCount = 0;
                int shippingCount = 0;
                int completedCount = 0;
                int cancelledCount = 0;
                
                Map<Long, Integer> productCountMap = new HashMap<>();
                
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    try {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            // Check if order is in date range
                            String orderDate = order.getOrderDate();
                            if (orderDate != null && orderDate.compareTo(startDateStr) >= 0 && orderDate.compareTo(endDateStr) <= 0) {
                                totalOrders++;
                                
                                // Count by status
                                String status = order.getStatus();
                                boolean isConfirmedOrCompleted = "Đã xác nhận".equals(status) || "Đang giao".equals(status) || "Hoàn thành".equals(status);
                                
                                if ("Đang xử lý".equals(status) || "Chờ xác nhận".equals(status)) {
                                    pendingCount++;
                                } else if ("Đang giao".equals(status) || "Đã xác nhận".equals(status)) {
                                    shippingCount++;
                                    // ⭐ ĐÃ XÁC NHẬN THÌ TÍNH DOANH THU VÀ SẢN PHẨM
                                    totalRevenue += order.getTotalAmount();
                                    List<Product> products = order.getProducts();
                                    if (products != null) {
                                        for (Product product : products) {
                                            productsSold += product.getQuantity();
                                            long productId = product.getId();
                                            productCountMap.put(productId, productCountMap.getOrDefault(productId, 0) + product.getQuantity());
                                        }
                                    }
                                } else if ("Hoàn thành".equals(status)) {
                                    completedCount++;
                                    // ⭐ HOÀN THÀNH CŨNG TÍNH DOANH THU VÀ SẢN PHẨM
                                    totalRevenue += order.getTotalAmount();
                                    List<Product> products = order.getProducts();
                                    if (products != null) {
                                        for (Product product : products) {
                                            productsSold += product.getQuantity();
                                            long productId = product.getId();
                                            productCountMap.put(productId, productCountMap.getOrDefault(productId, 0) + product.getQuantity());
                                        }
                                    }
                                } else if ("Đã hủy".equals(status)) {
                                    cancelledCount++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Statistics", "Error parsing order", e);
                    }
                }
                
                // Update UI
                tvRevenue.setText(String.format(Locale.getDefault(), "%,.0f đ", totalRevenue));
                tvTotalOrders.setText(String.valueOf(totalOrders));
                
                double avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0;
                tvAvgOrder.setText(String.format(Locale.getDefault(), "%,.0f đ", avgOrder));
                
                tvProductsSold.setText(String.valueOf(productsSold));
                tvNewCustomers.setText("0"); // TODO: Implement customer tracking
                
                tvPendingCount.setText(String.valueOf(pendingCount));
                tvShippingCount.setText(String.valueOf(shippingCount));
                tvCompletedCount.setText(String.valueOf(completedCount));
                tvCancelledCount.setText(String.valueOf(cancelledCount));
                
                // Calculate progress percentages
                int totalOrdersForProgress = pendingCount + shippingCount + completedCount + cancelledCount;
                if (totalOrdersForProgress > 0) {
                    progressPending.setProgress((pendingCount * 100) / totalOrdersForProgress);
                    progressShipping.setProgress((shippingCount * 100) / totalOrdersForProgress);
                    progressCompleted.setProgress((completedCount * 100) / totalOrdersForProgress);
                    progressCancelled.setProgress((cancelledCount * 100) / totalOrdersForProgress);
                } else {
                    progressPending.setProgress(0);
                    progressShipping.setProgress(0);
                    progressCompleted.setProgress(0);
                    progressCancelled.setProgress(0);
                }
                
                // TODO: Show top products based on productCountMap
                
                Log.d("Statistics", "Loaded statistics for " + range + ": " + totalOrders + " orders, revenue: " + totalRevenue);
            })
            .addOnFailureListener(e -> {
                Log.e("Statistics", "Error loading statistics", e);
                // Set default values on error
                tvRevenue.setText("0 đ");
                tvRevenueChange.setText("↑ +0% so với kỳ trước");
                tvTotalOrders.setText("0");
                tvAvgOrder.setText("0 đ");
                tvProductsSold.setText("0");
                tvNewCustomers.setText("0");
                tvPendingCount.setText("0");
                tvShippingCount.setText("0");
                tvCompletedCount.setText("0");
                tvCancelledCount.setText("0");
            });
    }
    
    private void resetButtonStyles() {
        int defaultColor = getResources().getColor(android.R.color.transparent);
        btnToday.setBackgroundColor(defaultColor);
        btnWeek.setBackgroundColor(defaultColor);
        btnMonth.setBackgroundColor(defaultColor);
        btnCustom.setBackgroundColor(defaultColor);
    }
}
