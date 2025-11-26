package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bt1.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView textUsername, textUserEmail, textPhone, textAddress, menuUpdateInfo, menuMyOrders, menuSettings, menuHelp;
    private ShapeableImageView imageAvatar;
    private Button buttonLogout;
    private ImageView backButton;
    private android.widget.LinearLayout infoContainer;
    private android.widget.LinearLayout guestMessageContainer;
    private android.widget.LinearLayout menuItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initViews();
        setupListeners();
        loadProfileData();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToHome();
            }
        });
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
        menuUpdateInfo = findViewById(R.id.menu_update_info);
        menuMyOrders = findViewById(R.id.menu_my_orders);
        menuSettings = findViewById(R.id.menu_settings);
        menuHelp = findViewById(R.id.menu_help);
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
            menuItemsContainer.setVisibility(android.view.View.VISIBLE);
            
            String fullName = prefManager.getUserName();
            String email = prefManager.getUserEmail();
            String phone = prefManager.getUserPhone();
            String address = prefManager.getUserAddress();
            
            textUsername.setText(fullName != null && !fullName.isEmpty() ? fullName : "Khách");
            textUserEmail.setText(email != null && !email.isEmpty() ? email : "Chưa có email");
            textPhone.setText("Điện thoại: " + (phone != null && !phone.isEmpty() ? phone : "Chưa có số điện thoại"));
            textAddress.setText("Địa chỉ: " + (address != null && !address.isEmpty() ? address : "Chưa có địa chỉ"));
        } else {
            // Hiển thị giao diện Guest
            infoContainer.setVisibility(android.view.View.GONE);
            guestMessageContainer.setVisibility(android.view.View.VISIBLE);
            menuItemsContainer.setVisibility(android.view.View.GONE);
            
            textUsername.setText("Khách");
            textUserEmail.setText("Chưa đăng nhập");
        }
        
        // Setup menu item click listeners
        menuSettings.setOnClickListener(v -> {
            Intent settingRed = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(settingRed);
        });
        
        menuMyOrders.setOnClickListener(v -> {
            Intent orderIntent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
            startActivity(orderIntent);
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
}
