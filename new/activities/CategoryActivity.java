package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.example.bt1.R;

public class CategoryActivity extends AppCompatActivity {

    private EditText editSearchCategory;
    private BottomNavigationView bottomNavigationView;
    
    // Brand cards
    private MaterialCardView brandIphone, brandSamsung, brandXiaomi;
    private MaterialCardView brandOppo, brandVivo, brandRealme;
    
    // Price range cards
    private MaterialCardView priceUnder5m, price5m10m;
    private MaterialCardView price10m20m, priceAbove20m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        // Ánh xạ các view
        initViews();

        // Thiết lập search functionality
        setupSearchListener();

        // Thiết lập click listeners cho brands
        setupBrandClickListeners();

        // Thiết lập click listeners cho price ranges
        setupPriceRangeClickListeners();

        // Thiết lập bottom navigation
        setupBottomNavigation();
    }

    private void initViews() {
        editSearchCategory = findViewById(R.id.edit_search_category);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Brand cards
        brandIphone = findViewById(R.id.brand_iphone);
        brandSamsung = findViewById(R.id.brand_samsung);
        brandXiaomi = findViewById(R.id.brand_xiaomi);
        brandOppo = findViewById(R.id.brand_oppo);
        brandVivo = findViewById(R.id.brand_vivo);
        brandRealme = findViewById(R.id.brand_realme);
        
        // Price range cards
        priceUnder5m = findViewById(R.id.price_under_5m);
        price5m10m = findViewById(R.id.price_5m_10m);
        price10m20m = findViewById(R.id.price_10m_20m);
        priceAbove20m = findViewById(R.id.price_above_20m);
    }

    private void setupSearchListener() {
        editSearchCategory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(editSearchCategory.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        if (!query.isEmpty()) {
            Toast.makeText(this, "Tìm kiếm danh mục: " + query, Toast.LENGTH_SHORT).show();
            // TODO: Implement actual search functionality
        }
    }

    private void setupBrandClickListeners() {
        brandIphone.setOnClickListener(v -> {
            Toast.makeText(this, "Xem sản phẩm iPhone", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to iPhone products
        });

        brandSamsung.setOnClickListener(v -> {
            Toast.makeText(this, "Xem sản phẩm Samsung", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Samsung products
        });

        brandXiaomi.setOnClickListener(v -> {
            Toast.makeText(this, "Xem sản phẩm Xiaomi", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Xiaomi products
        });

        brandOppo.setOnClickListener(v -> {
            Toast.makeText(this, "Xem sản phẩm OPPO", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to OPPO products
        });

        brandVivo.setOnClickListener(v -> {
            Toast.makeText(this, "Xem sản phẩm Vivo", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Vivo products
        });

        brandRealme.setOnClickListener(v -> {
            Toast.makeText(this, "Xem sản phẩm Realme", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Realme products
        });
    }

    private void setupPriceRangeClickListeners() {
        priceUnder5m.setOnClickListener(v -> {
            Toast.makeText(this, "Sản phẩm dưới 5 triệu", Toast.LENGTH_SHORT).show();
            // TODO: Filter products under 5M
        });

        price5m10m.setOnClickListener(v -> {
            Toast.makeText(this, "Sản phẩm từ 5-10 triệu", Toast.LENGTH_SHORT).show();
            // TODO: Filter products 5M-10M
        });

        price10m20m.setOnClickListener(v -> {
            Toast.makeText(this, "Sản phẩm từ 10-20 triệu", Toast.LENGTH_SHORT).show();
            // TODO: Filter products 10M-20M
        });

        priceAbove20m.setOnClickListener(v -> {
            Toast.makeText(this, "Sản phẩm trên 20 triệu", Toast.LENGTH_SHORT).show();
            // TODO: Filter products above 20M
        });
    }

    private void setupBottomNavigation() {
        // Đánh dấu mục "Categories" là đang được chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_categories);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_categories) {
                return true; // Already on categories page
            } else if (itemId == R.id.nav_cart) {
                Toast.makeText(this, "Chuyển sang Giỏ hàng", Toast.LENGTH_SHORT).show();
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