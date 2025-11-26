package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bt1.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView textUsername, textUserEmail, textPhone, textAddress;
    private TextView menuUpdateInfo, menuMyOrders, menuSettings, menuHelp;
    private TextView menuManageProducts, menuManageOrders, menuManageUsers, menuStatistics;
    private ShapeableImageView imageAvatar;
    private Button buttonLogout;
    private ImageView backButton;
    private android.widget.LinearLayout infoContainer;
    private android.widget.LinearLayout guestMessageContainer;
    private android.widget.LinearLayout menuItemsContainer;
    private android.widget.LinearLayout adminMenuContainer;

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
        textPhone = findViewById(R.id.text_phone);
        textAddress = findViewById(R.id.text_address);
        imageAvatar = findViewById(R.id.image_avatar);
        buttonLogout = findViewById(R.id.button_logout);
        backButton = findViewById(R.id.back_button);
        infoContainer = findViewById(R.id.info_container);
        guestMessageContainer = findViewById(R.id.guest_message_container);
        menuItemsContainer = findViewById(R.id.menu_items_container);
        adminMenuContainer = findViewById(R.id.admin_menu_container);
        
        // User menu items
        menuUpdateInfo = findViewById(R.id.menu_update_info);
        menuMyOrders = findViewById(R.id.menu_my_orders);
        menuSettings = findViewById(R.id.menu_settings);
        menuHelp = findViewById(R.id.menu_help);
        
        // Admin menu items
        menuManageProducts = findViewById(R.id.menu_manage_products);
        menuManageOrders = findViewById(R.id.menu_manage_orders);
        menuManageUsers = findViewById(R.id.menu_manage_users);
        menuStatistics = findViewById(R.id.menu_statistics);
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
    }

    private void loadProfileData() {
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        
        if (prefManager.isLoggedIn()) {
            // Hiển thị thông tin đăng nhập
            infoContainer.setVisibility(android.view.View.VISIBLE);
            guestMessageContainer.setVisibility(android.view.View.GONE);
            
            String fullName = prefManager.getUserName();
            String email = prefManager.getUserEmail();
            String phone = prefManager.getUserPhone();
            String address = prefManager.getUserAddress();
            String role = prefManager.getUserRole(); // Lấy role từ SharedPreferences
            
            textUsername.setText(fullName != null && !fullName.isEmpty() ? fullName : "Khách");
            textUserEmail.setText(email != null && !email.isEmpty() ? email : "Chưa có email");
            textPhone.setText("Điện thoại: " + (phone != null && !phone.isEmpty() ? phone : "Chưa có số điện thoại"));
            textAddress.setText("Địa chỉ: " + (address != null && !address.isEmpty() ? address : "Chưa có địa chỉ"));
            
            // Kiểm tra role và hiển thị menu tương ứng
            if ("admin".equals(role)) {
                // Hiển thị menu admin, ẩn menu user
                menuItemsContainer.setVisibility(android.view.View.GONE);
                adminMenuContainer.setVisibility(android.view.View.VISIBLE);
                setupAdminMenuListeners();
            } else {
                // Hiển thị menu user, ẩn menu admin
                menuItemsContainer.setVisibility(android.view.View.VISIBLE);
                adminMenuContainer.setVisibility(android.view.View.GONE);
                setupUserMenuListeners();
            }
        } else {
            // Hiển thị giao diện Guest
            infoContainer.setVisibility(android.view.View.GONE);
            guestMessageContainer.setVisibility(android.view.View.VISIBLE);
            menuItemsContainer.setVisibility(android.view.View.GONE);
            adminMenuContainer.setVisibility(android.view.View.GONE);
            
            textUsername.setText("Khách");
            textUserEmail.setText("Chưa đăng nhập");
        }
    }
    
    private void setupUserMenuListeners() {
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        
        // Setup menu item click listeners
        menuUpdateInfo.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                Intent intent = new Intent(ProfileActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập để cập nhật thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        
        menuMyOrders.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                Intent orderIntent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
                startActivity(orderIntent);
            } else {
                Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
        
        menuSettings.setOnClickListener(v -> {
            Intent settingRed = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(settingRed);
        });
        
        menuHelp.setOnClickListener(v -> {
            Intent helpIntent = new Intent(ProfileActivity.this, HelpSupportActivity.class);
            startActivity(helpIntent);
        });
    }
    
    private void setupAdminMenuListeners() {
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
        
        // Thống kê
        menuStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });
    }

    private void logout() {
        // Xóa session đăng nhập
        com.example.bt1.utils.SharedPreferencesManager.getInstance(this).clearUserData();
        
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
