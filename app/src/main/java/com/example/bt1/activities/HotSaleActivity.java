package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.HotSaleAdapter;
import com.example.bt1.global;
import java.util.ArrayList;
import java.util.List;

public class HotSaleActivity extends AppCompatActivity implements HotSaleAdapter.OnHotSaleItemClickListener {

    private ImageView btnBack, btnSearch;
    private TextView textCountdownHours, textCountdownMinutes, textCountdownSeconds;
    private Button btnFilterCategory, btnFilterPrice;
    private RecyclerView recyclerViewHotSale;

    private HotSaleAdapter hotSaleAdapter;
    private List<Product> hotSaleProducts;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_sale);

        initViews();
        setupRecyclerView();
        loadHotSaleProducts();
        setupListeners();
        startCountdownTimer();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        textCountdownHours = findViewById(R.id.text_countdown_hours);
        textCountdownMinutes = findViewById(R.id.text_countdown_minutes);
        textCountdownSeconds = findViewById(R.id.text_countdown_seconds);
        btnFilterCategory = findViewById(R.id.btn_filter_category);
        btnFilterPrice = findViewById(R.id.btn_filter_price);
        recyclerViewHotSale = findViewById(R.id.recycler_view_hot_sale);
    }

    private void setupRecyclerView() {
        hotSaleProducts = new ArrayList<>();
        hotSaleAdapter = new HotSaleAdapter(this, hotSaleProducts, this);
        recyclerViewHotSale.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewHotSale.setAdapter(hotSaleAdapter);
    }

    private void loadHotSaleProducts() {
        // Sản phẩm thực phẩm chức năng đang khuyến mãi
        hotSaleProducts.clear();

        // Lấy dữ liệu từ global và thêm giảm giá
        global globalData = new global();
        List<Product> allProducts = globalData.getDefaultData();
        
        // Chỉ lấy sản phẩm đang có deal
        for (Product product : allProducts) {
            if (product.getOnDeal() != null && product.getOnDeal()) {
                hotSaleProducts.add(product);
            }
        }

        hotSaleAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng tìm kiếm", Toast.LENGTH_SHORT).show();
        });

        btnFilterCategory.setOnClickListener(v -> {
            Toast.makeText(this, "Lọc theo danh mục", Toast.LENGTH_SHORT).show();
        });

        btnFilterPrice.setOnClickListener(v -> {
            Toast.makeText(this, "Lọc theo giá", Toast.LENGTH_SHORT).show();
        });
    }

    private void startCountdownTimer() {
        // Set countdown to 12 hours 34 minutes 56 seconds (in milliseconds)
        long timeInMillis = (12 * 60 * 60 + 34 * 60 + 56) * 1000;

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

                textCountdownHours.setText(String.format("%02d", hours));
                textCountdownMinutes.setText(String.format("%02d", minutes));
                textCountdownSeconds.setText(String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                textCountdownHours.setText("00");
                textCountdownMinutes.setText("00");
                textCountdownSeconds.setText("00");
                Toast.makeText(HotSaleActivity.this, "Flash Sale đã kết thúc!", Toast.LENGTH_LONG).show();
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onProductClick(Product product) {
        Toast.makeText(this, "Xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to product detail
    }

    @Override
    public void onAddToCart(Product product) {
        Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        // TODO: Add to cart logic
    }

    @Override
    public void onAddToFavorites(Product product) {
        Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
        // TODO: Add to favorites logic
    }
}