package com.example.bt1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // Khai báo các view
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private MaterialCardView categoryIphone, categorySamsung, categoryXiaomi;
    private BottomNavigationView bottomNavigationView; // Thêm biến cho thanh điều hướng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Tạm thời vô hiệu hóa để BottomNav không bị trong suốt
        setContentView(R.layout.home); // Sử dụng layout home.xml đã có BottomNavigationView

        // Ánh xạ các view
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Ánh xạ BottomNavigationView

        // Ánh xạ các danh mục
        LinearLayout categoryGroup = findViewById(R.id.category_group);
        categoryIphone = findViewById(R.id.category_iphone);
        categorySamsung = findViewById(R.id.category_samsung);
        categoryXiaomi = findViewById(R.id.category_xiaomi);

        // Xử lý Window Insets cho layout gốc (RelativeLayout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, v.getPaddingTop(), systemBars.right, v.getPaddingBottom());
            return insets;
        });

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Gán sự kiện click cho các danh mục
        setupCategoryClickListeners();

        // Thiết lập và gán sự kiện cho BottomNavigationView
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        // 1. Tạo dữ liệu mẫu
        createSampleData();

        // 2. Tạo Adapter
        productAdapter = new ProductAdapter(this, productList);

        // 3. Thiết lập Layout Manager (dạng lưới 2 cột)
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // 4. Gán Adapter cho RecyclerView
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void createSampleData() {
        productList = new ArrayList<>();
        productList.add(new Product("iPhone 15 256GB", "29.990.000₫", R.drawable.iphone_15, "6.1 inch", "8 GB", "256 GB"));
        productList.add(new Product("iPhone 14 128GB", "25.990.000₫", R.drawable.iphone_14, "6.1 inch", "6 GB", "128 GB"));
        productList.add(new Product("Samsung S24 Ultra", "27.990.000₫", R.drawable.samsung_s23, "6.8 inch", "12 GB", "512 GB"));
        productList.add(new Product("Xiaomi 14", "19.990.000₫", R.drawable.xiaomi_13t, "6.7 inch", "12 GB", "256 GB"));
    }

    private void setupCategoryClickListeners() {
        categoryIphone.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Bạn đã chọn danh mục iPhone", Toast.LENGTH_SHORT).show();
            // TODO: Lọc sản phẩm theo iPhone
        });

        categorySamsung.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Bạn đã chọn danh mục Samsung", Toast.LENGTH_SHORT).show();
            // TODO: Lọc sản phẩm theo Samsung
        });

        categoryXiaomi.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Bạn đã chọn danh mục Xiaomi", Toast.LENGTH_SHORT).show();
            // TODO: Lọc sản phẩm theo Xiaomi
        });
    }

    /**
     * Hàm thiết lập và xử lý sự kiện cho BottomNavigationView
     */
    private void setupBottomNavigation() {
        // Đánh dấu mục "Home" là đang được chọn khi khởi động
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Gán sự kiện khi một mục được chọn
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Đã ở trang chủ, không cần làm gì
                return true;
            } else if (itemId == R.id.nav_categories) {
                // Chuyển sang CategoryActivity (bạn sẽ cần tạo Activity này)
                Toast.makeText(HomeActivity.this, "Chuyển sang Danh mục", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_cart) {
                // Chuyển sang CartActivity (bạn sẽ cần tạo Activity này)
                Toast.makeText(HomeActivity.this, "Chuyển sang Giỏ hàng", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getApplicationContext(), CartActivity.class));
                // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Chuyển sang ProfileActivity
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                // Gửi email sang (nếu cần)
                // intent.putExtra("USER_EMAIL", getIntent().getStringExtra("USER_EMAIL"));
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Hiệu ứng chuyển trang mượt
                finish(); // Đóng HomeActivity
                return true;
            }

            return false;
        });
    }
}
