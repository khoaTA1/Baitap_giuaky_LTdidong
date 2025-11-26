package com.example.bt1.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.ProductAdapter;
import com.example.bt1.global;
import com.example.bt1.models.Product;
import com.example.bt1.repositories.ProductRepo;
import com.example.bt1.utils.DBHelper;
import com.example.bt1.utils.RenderImage;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    // Khai báo các view
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private EditText editSearch;
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
    private SharedPreferences prefs;
    private ProductRepo productRepo;
    private static final int PAGE_SIZE = 20;
    private DBHelper dbhelper;
    private RenderImage renderImage;

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
        iconFavorites = findViewById(R.id.icon_favorites);
        iconNotification = findViewById(R.id.icon_notification);
        notificationBadge = findViewById(R.id.notification_badge);
        textUserGreeting = findViewById(R.id.text_user_greeting);
        textUserNameProfile = findViewById(R.id.text_user_name_profile);
        btnLoginHome = findViewById(R.id.btn_login_home);
        cardFlashSale = findViewById(R.id.card_flash_sale);
        cardHotSale = findViewById(R.id.card_hot_sale);
        textTimerHours = findViewById(R.id.text_timer_hours);
        textTimerMinutes = findViewById(R.id.text_timer_minutes);
        textTimerSeconds = findViewById(R.id.text_timer_seconds);

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

        // Cập nhật số thông báo
        updateNotificationBadge();

        // Hiển thị tên người dùng
        loadUserGreeting();

        // Bắt đầu đếm ngược
        startCountdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại số thông báo khi quay lại màn hình này
        updateNotificationBadge();
        // Cập nhật tên người dùng
        loadUserGreeting();
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
        updateProductCache(false);

        // tạo Adapter với click listener
        productAdapter = new ProductAdapter(this, productList, this);

        // thiết lập Layout Manager (dạng lưới 2 cột)
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // gán Adapter cho RecyclerView
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProductList() {

    }

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
