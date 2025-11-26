package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
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
    
    // View all texts
    private TextView textViewAllBrands, textViewAllPrices;

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

        // Thiết lập click listeners cho "Xem tất cả"
        setupViewAllClickListeners();

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
        
        // View all texts
        textViewAllBrands = findViewById(R.id.text_view_all_brands);
        textViewAllPrices = findViewById(R.id.text_view_all_prices);
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
            navigateToShop("brand", "iPhone");
        });

        brandSamsung.setOnClickListener(v -> {
            navigateToShop("brand", "Samsung");
        });

        brandXiaomi.setOnClickListener(v -> {
            navigateToShop("brand", "Xiaomi");
        });

        brandOppo.setOnClickListener(v -> {
            navigateToShop("brand", "OPPO");
        });

        brandVivo.setOnClickListener(v -> {
            navigateToShop("brand", "Vivo");
        });

        brandRealme.setOnClickListener(v -> {
            navigateToShop("brand", "Realme");
        });
    }

    private void setupPriceRangeClickListeners() {
        priceUnder5m.setOnClickListener(v -> {
            navigateToShop("price", "Dưới 10 triệu");
        });

        price5m10m.setOnClickListener(v -> {
            navigateToShop("price", "10 - 20 triệu");
        });

        price10m20m.setOnClickListener(v -> {
            navigateToShop("price", "20 - 30 triệu");
        });

        priceAbove20m.setOnClickListener(v -> {
            navigateToShop("price", "Trên 30 triệu");
        });
    }

    private void setupViewAllClickListeners() {
        // Xem tất cả hãng điện thoại
        textViewAllBrands.setOnClickListener(v -> {
            navigateToShop("all", "");
        });

        // Xem tất cả phân khúc giá
        textViewAllPrices.setOnClickListener(v -> {
            navigateToShop("all", "");
        });
    }

    private void navigateToShop(String filterType, String filterValue) {
        Intent intent = new Intent(CategoryActivity.this, ShopActivity.class);
        intent.putExtra("filter_type", filterType);
        intent.putExtra("filter_value", filterValue);
        
        // Debug log
        android.util.Log.d("CategoryActivity", "Navigate to shop - Type: " + filterType + ", Value: " + filterValue);
        
        startActivity(intent);
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