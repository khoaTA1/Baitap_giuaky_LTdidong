package com.example.bt1.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bt1.R;
import com.example.bt1.utils.global;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView textUsername, textUserEmail;
    private LinearLayout menuUpdateInfo, menuAddresses, menuMyOrders, menuSettings, menuHelp;
    private LinearLayout menuManageProducts, menuManageOrders, menuManageMessages, menuManageUsers, menuStatistics, menuManageVouchers;
    private TextView textOrdersCount, textFavoritesCount;
    private ShapeableImageView imageAvatar;
    private Button buttonLogout;
    private ImageView backButton;
    private MaterialCardView btnEditAvatar;
    private com.google.android.material.card.MaterialCardView cardOrders, cardFavorites;
    private android.widget.LinearLayout guestMessageContainer;
    private android.widget.LinearLayout menuItemsContainer;
    private android.widget.LinearLayout adminMenuContainer;
    private android.widget.LinearLayout statsContainer;
    private global global = new global();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initViews();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        textUsername = findViewById(R.id.text_username);
        textUserEmail = findViewById(R.id.text_user_email);
        imageAvatar = findViewById(R.id.image_avatar);
        btnEditAvatar = findViewById(R.id.btn_edit_avatar);
        buttonLogout = findViewById(R.id.button_logout);
        backButton = findViewById(R.id.back_button);
        
        // Stats cards
        cardOrders = findViewById(R.id.card_orders);
        cardFavorites = findViewById(R.id.card_favorites);
        textOrdersCount = findViewById(R.id.text_orders_count);
        textFavoritesCount = findViewById(R.id.text_favorites_count);
        
        statsContainer = findViewById(R.id.stats_container);
        guestMessageContainer = findViewById(R.id.guest_message_container);
        menuItemsContainer = findViewById(R.id.menu_items_container);
        adminMenuContainer = findViewById(R.id.admin_menu_container);
        
        // User menu items
        menuUpdateInfo = findViewById(R.id.menu_update_info);
        menuAddresses = findViewById(R.id.menu_addresses);
        menuMyOrders = findViewById(R.id.menu_my_orders);
        menuSettings = findViewById(R.id.menu_settings);
        menuHelp = findViewById(R.id.menu_help);
        
        // Admin menu items
        menuManageProducts = findViewById(R.id.menu_manage_products);
        menuManageOrders = findViewById(R.id.menu_manage_orders);
        menuManageUsers = findViewById(R.id.menu_manage_users);
        menuManageMessages = findViewById(R.id.menu_manage_messages);
        menuStatistics = findViewById(R.id.menu_statistics);
        menuManageVouchers = findViewById(R.id.menu_manage_vouchers);
    }

    private void setupListeners() {
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        
        if (prefManager.isLoggedIn()) {
            // Nếu đã đăng nhập, hiển thị nút Logout
            buttonLogout.setText("Đăng xuất");
            buttonLogout.setOnClickListener(v -> logout());
        } else {
            // Nếu chưa đăng nhập (Guest), hiển thị nút Login
            buttonLogout.setText("Đăng nhập");
            buttonLogout.setOnClickListener(v -> goToLogin());
        }
        
        backButton.setOnClickListener(v -> goToHome());
        
        // Setup bottom navigation
        setupBottomNavigation();
    }
    
    private void setupBottomNavigation() {
        // Đánh dấu mục "Profile" là đang được chọn
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            
            // Gán sự kiện khi một mục được chọn
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
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
                    return true;
                }

                return false;
            });
        }
    }

    private void loadProfileData() {
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        
        if (prefManager.isLoggedIn()) {
            // Hiển thị stats cards và menu
            cardOrders.setVisibility(View.VISIBLE);
            cardFavorites.setVisibility(View.VISIBLE);
            guestMessageContainer.setVisibility(View.GONE);
            
            String fullName = prefManager.getUserName();
            String email = prefManager.getUserEmail();
            String role = prefManager.getUserRole();
            
            textUsername.setText(fullName != null && !fullName.isEmpty() ? fullName : "Khách");
            textUserEmail.setText(email != null && !email.isEmpty() ? email : "Chưa có email");
            
            // Load stats data
            loadStatsData();
            
            // Setup stats cards click listeners
            cardOrders.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            });
            
            cardFavorites.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
                startActivity(intent);
            });
            
            // Kiểm tra role và hiển thị menu tương ứng
            if ("admin".equals(role)) {
                // Ẩn stats cards cho admin
                statsContainer.setVisibility(View.GONE);
                menuItemsContainer.setVisibility(View.GONE);
                adminMenuContainer.setVisibility(View.VISIBLE);
                setupAdminMenuListeners();
            } else {
                menuItemsContainer.setVisibility(View.VISIBLE);
                adminMenuContainer.setVisibility(View.GONE);
                setupUserMenuListeners();
            }
        } else {
            // Hiển thị giao diện Guest
            cardOrders.setVisibility(View.GONE);
            cardFavorites.setVisibility(View.GONE);
            guestMessageContainer.setVisibility(View.VISIBLE);
            menuItemsContainer.setVisibility(View.GONE);
            adminMenuContainer.setVisibility(View.GONE);
            
            textUsername.setText("Khách");
            textUserEmail.setText("Chưa đăng nhập");
        }
    }
    
    private void loadStatsData() {
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        
        if (!prefManager.isLoggedIn()) {
            textOrdersCount.setText("0");
            textFavoritesCount.setText("0");
            return;
        }
        
        String userId = prefManager.getUserId();
        
        // Load số lượng đơn hàng từ Firestore
        com.google.firebase.firestore.FirebaseFirestore db = 
            com.google.firebase.firestore.FirebaseFirestore.getInstance();
        
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int ordersCount = queryDocumentSnapshots.size();
                textOrdersCount.setText(String.valueOf(ordersCount));
            })
            .addOnFailureListener(e -> {
                textOrdersCount.setText("0");
            });
        
        // Load số lượng yêu thích từ SharedPreferences
        try {
            String favKey = userId != null ? "favorites_" + userId : "favorites_guest";
            SharedPreferences favPrefs = getSharedPreferences(favKey, MODE_PRIVATE);
            String json = favPrefs.getString("favorite_products", "[]");
            
            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<Long>>(){}.getType();
            java.util.List<Long> favoriteIds = gson.fromJson(json, type);
            
            if (favoriteIds != null) {
                textFavoritesCount.setText(String.valueOf(favoriteIds.size()));
            } else {
                textFavoritesCount.setText("0");
            }
        } catch (Exception e) {
            textFavoritesCount.setText("0");
        }
    }
    
    private void setupUserMenuListeners() {
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        
        // Cập nhật thông tin
        menuUpdateInfo.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                Intent intent = new Intent(ProfileActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập để cập nhật thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Địa chỉ
        menuAddresses.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                Intent intent = new Intent(ProfileActivity.this, DeliveryAddressesActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập để xem địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Đơn hàng
        menuMyOrders.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                Intent orderIntent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
                startActivity(orderIntent);
            } else {
                Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Cài đặt
        menuSettings.setOnClickListener(v -> {
            Intent settingRed = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(settingRed);
        });
        
        // Trợ giúp
        menuHelp.setOnClickListener(v -> {
            Intent helpIntent = new Intent(ProfileActivity.this, HelpSupportActivity.class);
            startActivity(helpIntent);
        });
    }
    
    private void setupAdminMenuListeners() {
        // Ẩn Cài đặt và Trợ giúp cho admin
        menuSettings.setVisibility(View.GONE);
        menuHelp.setVisibility(View.GONE);
        
        // Quản lý sản phẩm
        menuManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageProductsActivity.class);
            startActivity(intent);
        });
        
        // Quản lý đơn hàng
        menuManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageOrdersActivity.class);
            startActivity(intent);
        });
        
        // Quản lý người dùng
        menuManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUsersActivity.class);
            startActivity(intent);
        });
        
        // Quản lý tin nhắn
        menuManageMessages.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageChatActivity.class);
            startActivity(intent);
        });
        
        // Thống kê
        menuStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });
        
        // Quản lý mã giảm giá
        menuManageVouchers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageVouchersActivity.class);
            startActivity(intent);
        });
    }

    private void logout() {
        // Xóa session đăng nhập
        com.example.bt1.utils.SharedPreferencesManager.getInstance(this).clearUserData();

        // đặt load lần đầu để reset phân trang
        global.isFirstLoad = true;
        
        // Quay về trang Home với vai trò Guest
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void goToLogin() {
        // Chuyển đến trang đăng nhập
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Quay về trang chủ khi bấm nút back của hệ thống
        goToHome();
    }
}
