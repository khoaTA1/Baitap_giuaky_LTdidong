package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt1.R;
import com.example.bt1.utils.Notify;
import com.example.bt1.utils.PasswordHasher;
import com.example.bt1.utils.Validator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.security.SecureRandom;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout layoutEmail;
    private TextInputEditText textInputEmail;
    private MaterialButton buttonSendReset;
    private View textLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Tạo notification channel
        Notify.createNotificationChannel(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        layoutEmail = findViewById(R.id.layout_email);
        textInputEmail = findViewById(R.id.text_input_email);
        buttonSendReset = findViewById(R.id.button_send_reset);
        textLoginLink = findViewById(R.id.text_login_link);
    }

    private void setupListeners() {
        buttonSendReset.setOnClickListener(v -> handleSendReset());

        textLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Clear error when user starts typing
        textInputEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                layoutEmail.setError(null);
            }
        });
    }

    private void handleSendReset() {
        // Clear previous errors
        layoutEmail.setError(null);

        // Get input values
        String email = textInputEmail.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            layoutEmail.setError("Vui lòng nhập email");
            textInputEmail.requestFocus();
            return;
        }

        if (!Validator.isValidEmail(email)) {
            layoutEmail.setError("Email không hợp lệ");
            textInputEmail.requestFocus();
            return;
        }

        // Disable button to prevent multiple clicks
        buttonSendReset.setEnabled(false);
        buttonSendReset.setText("Đang xử lý...");

        // Kiểm tra email trong Firebase và gửi mật khẩu mới
        resetPassword(email);
    }

    private void resetPassword(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Tìm user với email này
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()) {
                    // Không tìm thấy email
                    layoutEmail.setError("Email không tồn tại trong hệ thống");
                    buttonSendReset.setEnabled(true);
                    buttonSendReset.setText("Gửi mã khôi phục");
                    return;
                }
                
                // Tìm thấy user, tạo mật khẩu mới
                QueryDocumentSnapshot userDoc = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                String userId = userDoc.getId();
                String fullName = userDoc.getString("fullname");
                
                // Tạo mật khẩu mới ngẫu nhiên (6 ký tự)
                String newPassword = generateRandomPassword(8);
                
                // Hash mật khẩu mới
                String salt = PasswordHasher.generateSalt();
                String hashedPassword = PasswordHasher.hashPassword(newPassword, salt);
                
                // Cập nhật mật khẩu trong Firebase
                db.collection("users").document(userId)
                    .update(
                        "password", hashedPassword,
                        "salt", salt
                    )
                    .addOnSuccessListener(aVoid -> {
                        // Gửi thông báo với mật khẩu mới
                        String notificationTitle = "Khôi phục mật khẩu";
                        String notificationMessage = "Xin chào " + (fullName != null ? fullName : "bạn") + 
                            ",\nMật khẩu mới của bạn là: " + newPassword + 
                            "\nVui lòng đăng nhập và đổi mật khẩu ngay.";
                        
                        Notify.sendNotification(getApplicationContext(), notificationTitle, notificationMessage);
                        
                        // Re-enable button
                        buttonSendReset.setEnabled(true);
                        buttonSendReset.setText("Gửi mã khôi phục");
                        
                        // Navigate back to login after 3 seconds
                        buttonSendReset.postDelayed(() -> {
                            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }, 3000);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        buttonSendReset.setEnabled(true);
                        buttonSendReset.setText("Gửi mã khôi phục");
                    });
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                buttonSendReset.setEnabled(true);
                buttonSendReset.setText("Gửi mã khôi phục");
            });
    }
    
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
