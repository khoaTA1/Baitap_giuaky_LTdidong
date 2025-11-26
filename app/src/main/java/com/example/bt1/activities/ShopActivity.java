package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.ProductAdapter;
import com.example.bt1.global;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    // Views
    private ImageView btnBack;
    private ImageView btnSearch;
    private TextView textTitle;
    private MaterialButton btnFilterCategory;
    private MaterialButton btnFilterPrice;
    private MaterialButton btnSort;
    private LinearLayout currentFilterLayout;
    private TextView textCurrentFilter;
    private ImageView btnClearFilter;
    private RecyclerView recyclerViewProducts;
    private LinearLayout emptyState;
    private BottomNavigationView bottomNavigationView;

    // Data
    private ProductAdapter productAdapter;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    
    // Filter state
    private String filterType = ""; // "brand", "price", "all"
    private String filterValue = "";
    private String sortType = "name"; // "name", "price_low", "price_high"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        // Initialize views
        initViews();

        // Get filter parameters from intent
        getFilterFromIntent();

        // Setup data
        setupProductData();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Setup bottom navigation
        setupBottomNavigation();

        // Apply initial filter
        applyFilter();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        textTitle = findViewById(R.id.text_title);
        btnFilterCategory = findViewById(R.id.btn_filter_category);
        btnFilterPrice = findViewById(R.id.btn_filter_price);
        btnSort = findViewById(R.id.btn_sort);
        currentFilterLayout = findViewById(R.id.current_filter_layout);
        textCurrentFilter = findViewById(R.id.text_current_filter);
        btnClearFilter = findViewById(R.id.btn_clear_filter);
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        emptyState = findViewById(R.id.empty_state);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void getFilterFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            filterType = intent.getStringExtra("filter_type");
            filterValue = intent.getStringExtra("filter_value");
            
            if (filterType == null) filterType = "all";
            if (filterValue == null) filterValue = "";

            // Update title based on filter
            updateTitle();
        }
    }

    private void updateTitle() {
        String title = "Cửa hàng";
        
        switch (filterType) {
            case "brand":
                title = "Thương hiệu: " + filterValue;
                break;
            case "price":
                title = "Khoảng giá: " + filterValue;
                break;
            case "all":
                title = "Tất cả sản phẩm";
                break;
        }
        
        textTitle.setText(title);
    }

    private void setupProductData() {
        // Sử dụng dữ liệu thực phẩm chức năng từ global.java
        global globalData = new global();
        allProducts = globalData.getDefaultData();
        filteredProducts = new ArrayList<>(allProducts);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, filteredProducts, this);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        // Nút trở về
        btnBack.setOnClickListener(v -> onBackPressed());

        // Tìm kiếm
        btnSearch.setOnClickListener(v -> 
            Toast.makeText(this, "Tìm kiếm", Toast.LENGTH_SHORT).show());

        // Lọc
        btnFilterCategory.setOnClickListener(v -> showBrandFilter());
        btnFilterPrice.setOnClickListener(v -> showPriceFilter());
        btnSort.setOnClickListener(v -> showSortOptions());

        // Clear filter
        btnClearFilter.setOnClickListener(v -> clearFilter());
    }

    private void showBrandFilter() {
        // Hiển thị dialog chọn thương hiệu
        String[] brands = {"Tất cả", "LéAna", "Immuvita", "Nano", "Lacto"};
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn thương hiệu");
        builder.setItems(brands, (dialog, which) -> {
            if (which == 0) {
                filterType = "all";
                filterValue = "";
            } else {
                filterType = "brand";
                filterValue = brands[which];
            }
            applyFilter();
        });
        builder.show();
    }

    private void showPriceFilter() {
        // Hiển thị dialog chọn khoảng giá
        String[] priceRanges = {
            "Tất cả",
            "Dưới 10 triệu", 
            "10 - 20 triệu", 
            "20 - 30 triệu", 
            "Trên 30 triệu"
        };
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn khoảng giá");
        builder.setItems(priceRanges, (dialog, which) -> {
            if (which == 0) {
                filterType = "all";
                filterValue = "";
            } else {
                filterType = "price";
                filterValue = priceRanges[which];
            }
            applyFilter();
        });
        builder.show();
    }

    private void showSortOptions() {
        String[] sortOptions = {"Giá thấp đến cao", "Giá cao đến thấp"};
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sắp xếp theo");
        builder.setItems(sortOptions, (dialog, which) -> {
            switch (which) {
                case 0: sortType = "name"; break;
                case 1: sortType = "price_low"; break;
                case 2: sortType = "price_high"; break;
            }
            applyFilter(); 
        });
        builder.show();
    }

    private void applyFilter() {
        filteredProducts.clear();

        // Apply filter
        for (Product product : allProducts) {
            boolean shouldInclude = true;

            if ("brand".equals(filterType)) {
                shouldInclude = product.getName().contains(filterValue);
            } else if ("price".equals(filterType)) {
                shouldInclude = filterByPriceRange(product, filterValue);
            }

            if (shouldInclude) {
                filteredProducts.add(product);
            }
        }

        // Apply sorting
        applySorting();

        // Update UI
        updateFilterDisplay();
        updateProductList();
    }

    private boolean filterByPriceRange(Product product, String priceRange) {
        // Get numeric price directly
        double price = product.getPrice();
        
        // Debug log
        android.util.Log.d("ShopActivity", "Product: " + product.getName() + " Price: " + price + " Range: " + priceRange);
        
        switch (priceRange) {
            case "Dưới 10 triệu": 
                return price < 10000000;
            case "10 - 20 triệu": 
                return price >= 10000000 && price < 20000000;
            case "20 - 30 triệu": 
                return price >= 20000000 && price < 30000000;
            case "Trên 30 triệu": 
                return price >= 30000000;
            default: 
                return true;
        }
    }

    private void applySorting() {
        switch (sortType) {
            case "name":
                filteredProducts.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case "price_low":
                // sắp xếp thấp đến cao theo giá
                filteredProducts.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "price_high":
                // sắp xếp cao đến thấp theo giá
                filteredProducts.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
            default:
                // Không sắp xếp
                break;
        }
        
        // Cập nhật adapter sau khi sắp xếp
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    private void updateFilterDisplay() {
        if ("all".equals(filterType) || filterValue.isEmpty()) {
            currentFilterLayout.setVisibility(View.GONE);
        } else {
            currentFilterLayout.setVisibility(View.VISIBLE);
            String filterText = "Lọc theo: ";
            if ("brand".equals(filterType)) {
                filterText += "Thương hiệu " + filterValue;
            } else if ("price".equals(filterType)) {
                filterText += "Giá " + filterValue;
            }
            textCurrentFilter.setText(filterText);
        }
    }

    private void updateProductList() {
        if (filteredProducts.isEmpty()) {
            recyclerViewProducts.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewProducts.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            productAdapter.notifyDataSetChanged();
        }
    }

    private void clearFilter() {
        filterType = "all";
        filterValue = "";
        sortType = "name";
        updateTitle();
        applyFilter();
    }

    @Override
    public void onProductClick(Product product) {
        // Chuyển đến ProductDetailActivity
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

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_categories);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_categories) {
                return true;
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
    }
}