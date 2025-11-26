package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bt1.R;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    // === SỬA ĐỔI 1: Khai báo lại các biến cho khớp với layout mới ===
    private TextView textUsername, textUserEmail, textPhone, textAddress;
    private ShapeableImageView imageAvatar; // Dùng ShapeableImageView cho ảnh tròn
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Tạm thời vô hiệu hóa để layout phức tạp hiển thị đúng
        setContentView(R.layout.profile);

        // === SỬA ĐỔI 2: Ánh xạ lại các View cho đúng ID trong layout mới ===
        textUsername = findViewById(R.id.text_username);
        textUserEmail = findViewById(R.id.text_user_email);
        textPhone = findViewById(R.id.text_phone);
        textAddress = findViewById(R.id.text_address);
        imageAvatar = findViewById(R.id.image_avatar);
        buttonLogout = findViewById(R.id.button_logout);

        // Xử lý Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, v.getPaddingTop(), systemBars.right, v.getPaddingBottom());
            return insets;
        });

        // Xử lý sự kiện khi nhấn nút "Đăng xuất"
        buttonLogout.setOnClickListener(v -> {
            logout();
        });

        // === SỬA ĐỔI 3: Cập nhật logic hiển thị thông tin ===
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_EMAIL")) {
            String userEmail = intent.getStringExtra("USER_EMAIL");
            String username = userEmail.split("@")[0]; // Lấy tên từ email

            // Hiển thị thông tin lên các TextView mới
            textUsername.setText(username);
            textUserEmail.setText(userEmail);
        } else {
            textUsername.setText("Khách");
            textUserEmail.setText("Không tìm thấy thông tin");
        }
    }

    /**
     * Hàm xử lý đăng xuất: quay về màn hình đăng nhập (MainActivity)
     */
    private void logout() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng Activity hiện tại
    }
}
