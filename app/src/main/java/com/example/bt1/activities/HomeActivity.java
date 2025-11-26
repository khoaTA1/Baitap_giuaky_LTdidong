package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bt1.R;
import com.example.bt1.adapters.BannerAdapter;
import com.example.bt1.adapters.ProductAdapter;
import com.example.bt1.adapters.SearchSuggestionAdapter;
import com.example.bt1.models.Product;
import com.example.bt1.utils.SharedPreferencesManager;
import com.example.bt1.repositories.ProductRepo;
import com.example.bt1.utils.DBHelper;
import com.example.bt1.utils.RenderImage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener, SearchSuggestionAdapter.OnSuggestionClickListener {

    // Khai báo các view
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private BottomNavigationView bottomNavigationView;
    private EditText editSearch;
    private MaterialCardView cardSearch;
    private ImageView iconFavorites;
    private ImageView iconNotification;
    private TextView notificationBadge;
    private TextView textUserGreeting;
    private TextView textUserNameProfile;
    private com.google.android.material.button.MaterialButton btnLoginHome;
    private MaterialCardView cardFlashSale;
    private MaterialCardView cardHotSale;
    private TextView textTimerHours, textTimerMinutes, textTimerSeconds;
    private CountDownTimer countDownTimer;
    private ViewPager2 bannerViewPager;
    private android.widget.LinearLayout bannerIndicator;
    private android.os.Handler bannerHandler = new android.os.Handler();
    private int currentBannerPage = 0;
    private SharedPreferences prefs;
    
    // Repository and Database
    private ProductRepo productRepo;
    private DBHelper dbhelper;
    private RenderImage renderImage;
    private static final int PAGE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // khôi phục một số trạng thái ở phiên trước
        // bật nền tối
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (prefs.getBoolean("dark_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Ánh xạ các view
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        editSearch = findViewById(R.id.edit_search);
        cardSearch = findViewById(R.id.card_search);
        iconFavorites = findViewById(R.id.icon_favorites);
        iconNotification = findViewById(R.id.icon_notification);
        notificationBadge = findViewById(R.id.notification_badge);
        //textUserGreeting = findViewById(R.id.text_user_greeting);
        //textUserNameProfile = findViewById(R.id.text_user_name_profile);
        //btnLoginHome = findViewById(R.id.btn_login_home);
        cardFlashSale = findViewById(R.id.card_flash_sale);
        cardHotSale = findViewById(R.id.card_hot_sale);
        textTimerHours = findViewById(R.id.text_timer_hours);
        textTimerMinutes = findViewById(R.id.text_timer_minutes);
        textTimerSeconds = findViewById(R.id.text_timer_seconds);
        bannerViewPager = findViewById(R.id.banner_view_pager);
        bannerIndicator = findViewById(R.id.banner_indicator);

        // Thiết lập RecyclerView
        setupRecyclerView();
        
        // Load products from cache or Firebase
        updateProductCache(false);

        // Thiết lập banner slideshow
        setupBannerSlideshow();

        // Thiết lập search functionality
        setupSearchListener();

        // Thiết lập click listeners cho header icons
        setupHeaderClickListeners();

        // Thiết lập click listeners cho các card features
        setupFeatureClickListeners();

        // Thiết lập và gán sự kiện cho BottomNavigationView
        setupBottomNavigation();

        // Cập nhật số thông báo
        updateNotificationBadge();
        
        // Hiển thị tên người dùng
        //loadUserGreeting();

        // Bắt đầu đếm ngược
        startCountdown();
    }

    private void setupBannerSlideshow() {
        // Danh sách banner
        int[] bannerImages = {
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3,
                R.drawable.banner4,
                R.drawable.banner5
        };

        // Thiết lập adapter
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);

        // Tạo indicator dots
        setupBannerIndicator(bannerImages.length);

        // Auto-scroll banner mỗi 3 giây
        final Runnable bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentBannerPage == bannerImages.length) {
                    currentBannerPage = 0;
                }
                bannerViewPager.setCurrentItem(currentBannerPage++, true);
                bannerHandler.postDelayed(this, 1500);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 1500);

        // Cập nhật indicator khi swipe
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentBannerPage = position;
                updateBannerIndicator(position);
            }
        });
    }

    private void setupBannerIndicator(int count) {
        bannerIndicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0 ? R.drawable.circle_white_background : R.drawable.rounded_corner_8dp);
            dot.setAlpha(i == 0 ? 1.0f : 0.3f);
            bannerIndicator.addView(dot);
        }
    }

    private void updateBannerIndicator(int position) {
        for (int i = 0; i < bannerIndicator.getChildCount(); i++) {
            View dot = bannerIndicator.getChildAt(i);
            dot.setAlpha(i == position ? 1.0f : 0.3f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại số thông báo khi quay lại màn hình này
        updateNotificationBadge();
        // Cập nhật tên người dùng
        //loadUserGreeting();
        // Cập nhật trạng thái yêu thích trong danh sách sản phẩm
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }
    
    private void loadUserGreeting() {
        SharedPreferencesManager prefManager = SharedPreferencesManager.getInstance(this);
        if (prefManager.isLoggedIn()) {
            String fullName = prefManager.getUserName();
            if (fullName != null && !fullName.isEmpty()) {
                textUserGreeting.setText("Xin chào, " + fullName);
                textUserNameProfile.setText(fullName);
            } else {
                textUserGreeting.setText("Xin chào, Khách");
                textUserNameProfile.setText("Khách");
            }
            // Ẩn nút đăng nhập khi đã login
            btnLoginHome.setVisibility(android.view.View.GONE);
        } else {
            textUserGreeting.setText("Xin chào, Khách");
            textUserNameProfile.setText("Bạn cần đăng nhập");
            // Hiển thị nút đăng nhập khi chưa login
            btnLoginHome.setVisibility(android.view.View.VISIBLE);
            btnLoginHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void setupRecyclerView() {
        // 1. Tạo dữ liệu mẫu
        // createSampleData(); // Commented out - using Firebase data

        // 2. Khởi tạo productList nếu chưa có
        if (productList == null) {
            productList = new ArrayList<>();
        }

        // 3. Tạo Adapter với click listener
        productAdapter = new ProductAdapter(this, productList, this);

        // 3. Thiết lập Layout Manager (dạng lưới 2 cột)
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // 4. Gán Adapter cho RecyclerView
        recyclerViewProducts.setAdapter(productAdapter);
    }

    // private void createSampleData() {
    //     productList = new ArrayList<>();
    //     // Sử dụng dữ liệu thực phẩm chức năng từ global.java
    //     global globalData = new global();
    //     productList = globalData.getDefaultData();
    // }
    
    // private void loadProductList() {

    // }

    private void updateProductCache(boolean loadNextPage) {
        if (productRepo == null) {
            productRepo = new ProductRepo();
        }

        if (dbhelper == null) dbhelper = new DBHelper(this);

        // danh sách local hiện tại
        List<Product> loadedProducts = dbhelper.getAllProducts();
        Log.d(">>> Home Activity", "Danh sách sản phẩm local hiện tại: " + loadedProducts.size());

        // nếu đây không phải là load trang tiếp theo và danh sách local hiện tại đang KHÔNG trống
        // thì không load từ firestore mà lấy lại từ sqlite
        if (!loadNextPage && loadedProducts != null && !loadedProducts.isEmpty()) {
            productList.clear();
            productList.addAll(loadedProducts);

            if (productAdapter == null) {
                productAdapter = new ProductAdapter(this, productList, this);
                recyclerViewProducts.setAdapter(productAdapter);
            } else {
                productAdapter.notifyDataSetChanged();
            }
            Log.d(">>> HomeActivity", "Load lại sản phẩm từ cache SQLite, số lượng: " + productList.size());
            return;
        }

        // nếu là load tiếp tục, hoặc load lần đầu khi mới mở app
        // thì lấy từ firestore
        productRepo.getProductsBatch(PAGE_SIZE, object -> {
            renderImage = new RenderImage();
            if (object != null) {
                for (Product product : (List<Product>) object) {
                    renderImage.downloadAndSaveImage(this, product, () -> {
                        // tải ảnh và lưu product vào SQLite
                        dbhelper.insertProducts(Collections.singletonList(product));

                        // Thêm product vào productList và cập nhật RecyclerView
                        runOnUiThread(() -> {
                            productList.add(product);
                            if (productAdapter == null) {
                                productAdapter = new ProductAdapter(this, productList, this);
                                recyclerViewProducts.setAdapter(productAdapter);
                            } else {
                                productAdapter.notifyDataSetChanged();
                            }
                        });
                    });
                }
            }
        });
    }

    private void setupSearchListener() {
        // Khi click vào card search, mở dialog tìm kiếm
        if (cardSearch != null) {
            cardSearch.setOnClickListener(v -> showSearchDialog());
        }
        
        // Cũng cho phép click vào EditText
        if (editSearch != null) {
            editSearch.setOnClickListener(v -> showSearchDialog());
        }
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
                // Go to shop with search filter
                Intent intent = new Intent(this, ShopActivity.class);
                intent.putExtra("filter_type", "search");
                intent.putExtra("filter_value", currentQuery[0]);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        
        // Setup search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // When user submits search, go to shop with filter
                Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                intent.putExtra("filter_type", "search");
                intent.putExtra("filter_value", query);
                startActivity(intent);
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
                    for (Product product : productList) {
                        if (product.getName() != null && 
                            product.getName().toLowerCase().contains(searchQuery)) {
                            suggestions.add(product);
                            if (suggestions.size() >= 3) break; // Max 3 suggestions
                        }
                    }
                    
                    // Second pass: If still need more, find by CATEGORY
                    if (suggestions.size() < 3) {
                        for (Product product : productList) {
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
                SharedPreferencesManager.getInstance(this).resetNotificationCount();
                updateNotificationBadge();
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
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
            intent.setClassName(this, "com.example.bt1.activities.ProductDetailActivity");
            intent.putExtra("product", product);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: Không thể mở trang chi tiết", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển sang ShopActivity với query tìm kiếm
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra("filter_type", "search");
        intent.putExtra("filter_value", query);
        startActivity(intent);
        
        // Ẩn bàn phím sau khi tìm kiếm
        android.view.inputmethod.InputMethodManager imm = 
            (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    private void updateNotificationBadge() {
        int newNotificationCount = SharedPreferencesManager.getInstance(this).getNewNotificationCount();
        if (newNotificationCount > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(newNotificationCount));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    private void startCountdown() {
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, 23);
        targetTime.set(Calendar.MINUTE, 59);
        targetTime.set(Calendar.SECOND, 59);
        targetTime.set(Calendar.MILLISECOND, 0);

        // Nếu đã qua 23:59:59 hôm nay, đặt mục tiêu là 23:59:59 ngày mai
        if (System.currentTimeMillis() > targetTime.getTimeInMillis()) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        long diffInMillis = targetTime.getTimeInMillis() - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(diffInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                textTimerHours.setText(String.format(Locale.getDefault(), "%02d", hours));
                textTimerMinutes.setText(String.format(Locale.getDefault(), "%02d", minutes));
                textTimerSeconds.setText(String.format(Locale.getDefault(), "%02d", seconds));
            }

            @Override
            public void onFinish() {
                textTimerHours.setText("00");
                textTimerMinutes.setText("00");
                textTimerSeconds.setText("00");
                // Có thể thêm xử lý khi hết giờ
            }
        }.start();
    }
}
