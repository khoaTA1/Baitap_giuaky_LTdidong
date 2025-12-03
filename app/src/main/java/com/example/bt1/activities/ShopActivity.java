package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.ProductAdapter;
import com.example.bt1.adapters.SearchSuggestionAdapter;
import com.example.bt1.repositories.ProductRepo;
import com.example.bt1.utils.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener, SearchSuggestionAdapter.OnSuggestionClickListener {

    // Views
    private ImageView btnBack;
    private ImageView btnSearch;
    private TextView textTitle;
    private MaterialButton btnFilterCategory;
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
    private ProductRepo productRepo;
    private DBHelper dbHelper;

    // Filter state
    private String filterType = ""; // "brand", "price", "all"
    private String filterValue = "";
    private String sortType = "name"; // "name", "price_low", "price_high"
    private boolean isLoadingProducts = false;
    private int PAGE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        // Initialize repositories
        productRepo = new ProductRepo();
        dbHelper = new DBHelper(this);
        
        // Initialize views
        initViews();

        // Get filter parameters from intent
        getFilterFromIntent();

        // Initialize data structures
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Setup bottom navigation
        setupBottomNavigation();

        // Load data from Firebase
        loadProductsFromFirebase();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        textTitle = findViewById(R.id.text_title);
        btnFilterCategory = findViewById(R.id.btn_filter_category);
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
            case "search":
                title = "Kết quả tìm kiếm";
                break;
            case "category":
                title = "Danh mục: " + filterValue;
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

    private void loadProductsFromFirebase() {
        if (isLoadingProducts) return;
        
        isLoadingProducts = true;
        
        // Hiển thị loading state nếu cần
        if (allProducts.isEmpty()) {
            recyclerViewProducts.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
        
        // Thử load từ SQLite cache trước
        List<Product> cachedProducts = dbHelper.getAllProducts();
        
        if (cachedProducts != null && !cachedProducts.isEmpty()) {
            android.util.Log.d("ShopActivity", "Loaded " + cachedProducts.size() + " products from cache");
            allProducts.clear();
            allProducts.addAll(cachedProducts);
            applyFilter();
            isLoadingProducts = false;
        }
        
        // Load từ Firebase (sẽ update cache và UI)
        productRepo.getProductsBatch(PAGE_SIZE, obj -> {
            isLoadingProducts = false;

            if (obj != null && obj instanceof List) {
                List<Product> products = (List<Product>) obj;
                android.util.Log.d(">>> ShopActivity", "Loaded " + products.size() + " products from Firebase");

                allProducts.clear();
                allProducts.addAll(products);

                // Cache vào SQLite
                try {
                    // dbHelper.clearTable();
                    dbHelper.insertProducts(products);
                    android.util.Log.d(">>> ShopActivity", "Cached products to SQLite");
                } catch (Exception e) {
                    android.util.Log.e("!!! ShopActivity", "Error caching products: " + e.getMessage());
                }

                // Apply filter và update UI
                applyFilter();
            } else {
                android.util.Log.e("!!! ShopActivity", "Failed to load products from Firebase");

                // Nếu không có cache, hiển thị empty state
                if (allProducts.isEmpty()) {
                    runOnUiThread(() -> {
                        recyclerViewProducts.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
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
        btnSearch.setOnClickListener(v -> showSearchDialog());

        // Lọc
        btnFilterCategory.setOnClickListener(v -> showBrandFilter());
        btnSort.setOnClickListener(v -> showSortOptions());

        // Clear filter
        btnClearFilter.setOnClickListener(v -> clearFilter());
    }

    private void showSearchDialog() {
        // Create dialog with custom layout
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Material_Light_NoActionBar);
        dialog.setContentView(R.layout.dialog_search);

        // Get views from dialog
        ImageView btnClose = dialog.findViewById(R.id.btn_close_search);
        SearchView searchView = dialog.findViewById(R.id.search_view);
        RecyclerView recyclerSuggestions = dialog.findViewById(R.id.recycler_suggestions);
        LinearLayout suggestionsHeader = dialog.findViewById(R.id.suggestions_header);
        TextView textSuggestionCount = dialog.findViewById(R.id.text_suggestion_count);
        LinearLayout emptyStateView = dialog.findViewById(R.id.empty_state);
        LinearLayout noResultsView = dialog.findViewById(R.id.no_results_state);
        MaterialButton btnSearchAll = dialog.findViewById(R.id.btn_search_all);

        // Setup suggestions adapter
        List<Product> suggestions = new ArrayList<>();
        SearchSuggestionAdapter suggestionsAdapter = new SearchSuggestionAdapter(
            this,
            suggestions,
            product -> {
                // When suggestion clicked, go to product detail
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
                dialog.dismiss();
            }
        );

        recyclerSuggestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggestions.setAdapter(suggestionsAdapter);

        // Close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Search all button
        final String[] currentQuery = {""};
        btnSearchAll.setOnClickListener(v -> {
            if (!currentQuery[0].isEmpty()) {
                filterType = "search";
                filterValue = currentQuery[0];
                applyFilter();
                dialog.dismiss();
            }
        });

        // Setup search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // When user submits search, show all results
                filterType = "search";
                filterValue = query;
                applyFilter();
                dialog.dismiss();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery[0] = newText;

                if (newText.isEmpty()) {
                    // Show empty state
                    emptyStateView.setVisibility(View.VISIBLE);
                    noResultsView.setVisibility(View.GONE);
                    recyclerSuggestions.setVisibility(View.GONE);
                    suggestionsHeader.setVisibility(View.GONE);
                    btnSearchAll.setVisibility(View.GONE);
                } else {
                    // Search for suggestions (max 3 results)
                    suggestions.clear();
                    String searchQuery = newText.toLowerCase().trim();

                    // First pass: Find products where NAME matches (higher priority)
                    for (Product product : allProducts) {
                        if (product.getName() != null &&
                            product.getName().toLowerCase().contains(searchQuery)) {
                            suggestions.add(product);
                            if (suggestions.size() >= 3) break; // Max 3 suggestions
                        }
                    }

                    // Second pass: If still need more, find by CATEGORY
                    if (suggestions.size() < 3) {
                        for (Product product : allProducts) {
                            if (suggestions.size() >= 3) break;
                            // Skip if already added (name match)
                            if (suggestions.contains(product)) continue;

                            if (product.getCategory() != null &&
                                product.getCategory().toLowerCase().contains(searchQuery)) {
                                suggestions.add(product);
                            }
                        }
                    }

                    if (suggestions.isEmpty()) {
                        // Show no results state
                        emptyStateView.setVisibility(View.GONE);
                        noResultsView.setVisibility(View.VISIBLE);
                        recyclerSuggestions.setVisibility(View.GONE);
                        suggestionsHeader.setVisibility(View.GONE);
                        btnSearchAll.setVisibility(View.GONE);
                    } else {
                        // Show suggestions
                        emptyStateView.setVisibility(View.GONE);
                        noResultsView.setVisibility(View.GONE);
                        recyclerSuggestions.setVisibility(View.VISIBLE);
                        suggestionsHeader.setVisibility(View.VISIBLE);
                        btnSearchAll.setVisibility(View.VISIBLE);

                        textSuggestionCount.setText(suggestions.size() + " kết quả");
                        suggestionsAdapter.updateSuggestions(suggestions);
                    }
                }

                return true;
            }
        });

        // Show dialog first
        dialog.show();

        // Request focus and show keyboard after dialog is shown
        searchView.post(() -> {
            searchView.setIconified(false);
            searchView.requestFocus();

            // Show soft keyboard
            android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchView.findFocus(), android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public void onSuggestionClick(Product product) {
        // Handle suggestion click
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void showBrandFilter() {
        // Hiển thị dialog chọn danh mục
        String[] categories = {
            "Tất cả",
            "Vitamin & Khoáng chất",
            "Sinh lý - Nội tiết tố",
            "Cải thiện tăng cường chức năng",
            "Hỗ trợ điều trị",
            "Hỗ trợ tiêu hóa",
            "Thần kinh não"
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn danh mục");
        builder.setItems(categories, (dialog, which) -> {
            if (which == 0) {
                filterType = "all";
                filterValue = "";
            } else {
                filterType = "category";
                filterValue = categories[which];
            }
            applyFilter();
        });
        builder.show();
    }

    private void showPriceFilter() {
        // Hiển thị dialog chọn khoảng giá
        String[] priceRanges = {
            "Tất cả",
            "Dưới 1 triệu",
            "Trên 1 triệu"
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
        String[] sortOptions = {"Tên sản phẩm", "Giá thấp đến cao", "Giá cao đến thấp"};

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

            if ("search".equals(filterType)) {
                // Tìm kiếm theo tên sản phẩm
                shouldInclude = product.getName().toLowerCase().contains(filterValue.toLowerCase());
            } else if ("brand".equals(filterType)) {
                shouldInclude = product.getName().contains(filterValue);
            } else if ("category".equals(filterType)) {
                // Lọc theo category field của Product
                shouldInclude = product.getCategory() != null &&
                               product.getCategory().equals(filterValue);
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
            case "Dưới 1 triệu":
                return price < 1000000;
            case "Trên 1 triệu":
                return price >= 1000000;
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
            if ("search".equals(filterType)) {
                filterText = "Tìm kiếm: " + filterValue;
            } else if ("brand".equals(filterType)) {
                filterText += "Thương hiệu " + filterValue;
            } else if ("category".equals(filterType)) {
                filterText += "Danh mục " + filterValue;
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
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        // Đánh dấu mục "Home" là đang được chọn khi khởi động
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Gán sự kiện khi một mục được chọn
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_setting) {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                finish();
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