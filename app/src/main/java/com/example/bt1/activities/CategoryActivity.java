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

    // Category cards - Danh mục thực phẩm chức năng
    private MaterialCardView categoryVitamin, categoryDigestion, categoryHormone;
    private MaterialCardView categoryTreatment, categoryOther1, categoryOther2;

    // Price range cards
    private MaterialCardView priceUnder5m, price5m10m;
    private MaterialCardView price10m20m, priceAbove20m;

    // View all texts
    private TextView textViewAllCategories, textViewAllPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        // Ánh xạ các view
        initViews();

        // Thiết lập search functionality
        setupSearchListener();

        // Thiết lập click listeners cho categories
        setupCategoryClickListeners();

        // Thiết lập click listeners cho price ranges
        setupPriceRangeClickListeners();

        // Thiết lập click listeners cho "Xem tất cả"
        setupViewAllClickListeners();

        // Thiết lập bottom navigation
        //setupBottomNavigation();
    }

    private void initViews() {
        // editSearchCategory = findViewById(R.id.edit_search_category); // Layout không có search
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Category cards - Danh mục thực phẩm chức năng
        categoryVitamin = findViewById(R.id.brand_iphone);
        categoryDigestion = findViewById(R.id.brand_samsung);
        categoryHormone = findViewById(R.id.brand_xiaomi);
        categoryTreatment = findViewById(R.id.brand_oppo);
        categoryOther1 = findViewById(R.id.brand_vivo);
        categoryOther2 = findViewById(R.id.brand_realme);

        // Price range cards (chỉ có 2 cards trong layout)
        priceUnder5m = findViewById(R.id.price_under_5m);
        price5m10m = findViewById(R.id.price_5m_10m);

        // View all texts
        textViewAllCategories = findViewById(R.id.text_view_all_brands);
        textViewAllPrices = findViewById(R.id.text_view_all_prices);
    }

    private void setupSearchListener() {
        if (editSearchCategory != null) {
            editSearchCategory.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(editSearchCategory.getText().toString().trim());
                    return true;
                }
                return false;
            });
        }
    }

    private void performSearch(String query) {
        if (!query.isEmpty()) {
            Toast.makeText(this, "Tìm kiếm danh mục: " + query, Toast.LENGTH_SHORT).show();
            // TODO: Implement actual search functionality
        }
    }

    private void setupCategoryClickListeners() {
        categoryVitamin.setOnClickListener(v -> {
            navigateToShop("category", "Vitamin & Khoáng chất");
        });

        categoryDigestion.setOnClickListener(v -> {
            navigateToShop("category", "Sinh lý - Nội tiết tố");
        });

        categoryHormone.setOnClickListener(v -> {
            navigateToShop("category", "Cải thiện tăng cường chức năng");
        });

        categoryTreatment.setOnClickListener(v -> {
            navigateToShop("category", "Hỗ trợ điều trị");
        });

        categoryOther1.setOnClickListener(v -> {
            navigateToShop("category", "Hỗ trợ tiêu hóa");
        });

        categoryOther2.setOnClickListener(v -> {
            navigateToShop("category", "Thần kinh não");
        });
    }

    private void setupPriceRangeClickListeners() {
        priceUnder5m.setOnClickListener(v -> {
            navigateToShop("price", "Dưới 1 triệu");
        });

        price5m10m.setOnClickListener(v -> {
            navigateToShop("price", "Trên 1 triệu");
        });
    }

    private void setupViewAllClickListeners() {
        // Xem tất cả danh mục thực phẩm chức năng
        textViewAllCategories.setOnClickListener(v -> {
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

    /*
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
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }*/
}