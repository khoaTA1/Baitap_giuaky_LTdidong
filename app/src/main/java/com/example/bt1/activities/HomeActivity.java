package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    // Khai báo các view
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private BottomNavigationView bottomNavigationView;
    private EditText editSearch;
    private ImageView iconFavorites;
    private ImageView iconNotification;
    private MaterialCardView cardHotSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Ánh xạ các view
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        editSearch = findViewById(R.id.edit_search);
        iconFavorites = findViewById(R.id.icon_favorites);
        iconNotification = findViewById(R.id.icon_notification);
        cardHotSale = findViewById(R.id.card_hot_sale);

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập search functionality
        setupSearchListener();

        // Thiết lập click listeners cho header icons
        setupHeaderClickListeners();

        // Thiết lập click listeners cho các card features
        setupFeatureClickListeners();

        // Thiết lập và gán sự kiện cho BottomNavigationView
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        // 1. Tạo dữ liệu mẫu
        createSampleData();

        // 2. Tạo Adapter với click listener
        productAdapter = new ProductAdapter(this, productList, this);

        // 3. Thiết lập Layout Manager (dạng lưới 2 cột)
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // 4. Gán Adapter cho RecyclerView
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void createSampleData() {
        productList = new ArrayList<>();
        productList.add(new Product("OPPO Find X9 12GB 256GB", "22.990.000₫", R.drawable.iphone_15));
        productList.add(new Product("Điện thoại iPhone 16 Pro Max 256GB", "30.590.000₫", R.drawable.samsung_s23));
        productList.add(new Product("Samsung Galaxy S24", "21.490.000₫", R.drawable.xiaomi_13t));
        productList.add(new Product("Xiaomi 14T Pro", "15.990.000₫", R.drawable.iphone_14));
    }

    private void setupSearchListener() {
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(editSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void setupHeaderClickListeners() {
        // Click vào icon favorites để chuyển sang trang yêu thích
        if (iconFavorites != null) {
            iconFavorites.setOnClickListener(v -> {
                Toast.makeText(this, "Chuyển sang trang yêu thích", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
            });
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy icon favorites", Toast.LENGTH_LONG).show();
        }

        // Click vào icon notification
        if (iconNotification != null) {
            iconNotification.setOnClickListener(v -> {
                Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupFeatureClickListeners() {
        // Click vào Hot Sale để chuyển sang trang hot sale
        if (cardHotSale != null) {
            cardHotSale.setOnClickListener(v -> {
                Toast.makeText(this, "Chuyển sang trang Hot Sale", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HotSaleActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onProductClick(Product product) {
        // Xử lý khi click vào sản phẩm - chuyển sang trang chi tiết
        Toast.makeText(this, "Xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
        
        try {
            Intent intent = new Intent();
            intent.setClassName(this, "com.example.bt1.ProductDetailActivity");
            intent.putExtra("product", product);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: Không thể mở trang chi tiết", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch(String query) {
        if (!query.isEmpty()) {
            Toast.makeText(this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
            // TODO: Implement actual search functionality
        }
    }

    private void setupBottomNavigation() {
        // Đánh dấu mục "Home" là đang được chọn khi khởi động
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Gán sự kiện khi một mục được chọn
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_categories) {
                Intent intent = new Intent(this, CategoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                Toast.makeText(HomeActivity.this, "Chuyển sang Giỏ hàng", Toast.LENGTH_SHORT).show();
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
