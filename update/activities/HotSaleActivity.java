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
        // Simulate loading hot sale products with discounts
        hotSaleProducts.clear();

        // Create hot sale products with original and sale prices
        Product product1 = new Product("iPhone 15 Pro Max 256GB", "21.413.000₫", R.drawable.iphone_15);
        product1.setOriginalPrice("30.590.000₫");
        product1.setDiscountPercent(30);
        product1.setRating(4.8f);
        product1.setSoldCount("1.2k");
        hotSaleProducts.add(product1);

        Product product2 = new Product("Samsung Galaxy S24 Ultra", "20.293.000₫", R.drawable.samsung_s23);
        product2.setOriginalPrice("28.990.000₫");
        product2.setDiscountPercent(30);
        product2.setRating(4.7f);
        product2.setSoldCount("856");
        hotSaleProducts.add(product2);

        Product product3 = new Product("Xiaomi 14T Pro 256GB", "11.193.000₫", R.drawable.xiaomi_13t);
        product3.setOriginalPrice("15.990.000₫");
        product3.setDiscountPercent(30);
        product3.setRating(4.5f);
        product3.setSoldCount("2.1k");
        hotSaleProducts.add(product3);

        Product product4 = new Product("OPPO Find X8 Pro", "16.093.000₫", R.drawable.iphone_14);
        product4.setOriginalPrice("22.990.000₫");
        product4.setDiscountPercent(30);
        product4.setRating(4.4f);
        product4.setSoldCount("645");
        hotSaleProducts.add(product4);

        Product product5 = new Product("Vivo V30 Pro 5G", "9.093.000₫", R.drawable.iphone_15);
        product5.setOriginalPrice("12.990.000₫");
        product5.setDiscountPercent(30);
        product5.setRating(4.3f);
        product5.setSoldCount("1.8k");
        hotSaleProducts.add(product5);

        Product product6 = new Product("Realme GT 6T", "6.293.000₫", R.drawable.samsung_s23);
        product6.setOriginalPrice("8.990.000₫");
        product6.setDiscountPercent(30);
        product6.setRating(4.2f);
        product6.setSoldCount("3.2k");
        hotSaleProducts.add(product6);

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