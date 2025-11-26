package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bt1.utils.FireStoreCallBack;
import com.example.bt1.utils.Notify;
import com.example.bt1.utils.PasswordHasher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.bt1.R;
import com.example.bt1.models.User;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final int REGISTER_REQUEST_CODE = 1001;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private TextView textRegister;
    private TextView textForgotPassword;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        Notify.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // ánh xạ một số thành phần layout
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        buttonLogin = findViewById(R.id.button_login);
        textRegister = findViewById(R.id.text_register);
        textForgotPassword = findViewById(R.id.text_forgot_password);

        if (textInputEmail.getEditText() != null) {
            editTextEmail = (TextInputEditText) textInputEmail.getEditText();
        }
        if (textInputPassword.getEditText() != null) {
            editTextPassword = (TextInputEditText) textInputPassword.getEditText();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogin.setOnClickListener(v -> {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    textInputEmail.setError(null);
                    textInputPassword.setError(null);

                    validateLogin(email, password, user -> {
                        if (user != null) {
                            // đăng nhập thành công
                            Log.d(">> Login", "success");

                            // Lưu thông tin user vào SharedPreferencesManager
                            SharedPreferencesManager.getInstance(MainActivity.this).saveUserData((User) user);

                            Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            // Tạo Intent để mở HomeActivity
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            // Gửi email của người dùng sang HomeActivity
                            intent.putExtra("USER_EMAIL", email);
                            // Bắt đầu HomeActivity
                            startActivity(intent);
                            // Đóng MainActivity để người dùng không thể nhấn back quay lại
                            finish();
                        } else {
                            // đăng nhập thất bại
                            // Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            textInputPassword.setError("Tên đăng nhập hoặc mật khẩu không chính xác");
                        }
                    });
        });

        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivityForResult(intent, REGISTER_REQUEST_CODE);
        });

        textForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Hàm kiểm tra thông tin đăng nhập.
     * Hỗ trợ cả tài khoản mặc định để test và tài khoản đã đăng ký.
     * @return User nếu đăng nhập thành công, null nếu thất bại
     */
    private void validateLogin(String email, String password, FireStoreCallBack callback) {
        // 1. Kiểm tra các trường không được để trống
        if (email.isEmpty()) {
            textInputEmail.setError("Email không được để trống");
            //callback.onLoginResult(null);
            callback.returnResult(null);
            return;
        }
        if (password.isEmpty()) {
            textInputPassword.setError("Mật khẩu không được để trống");
            callback.returnResult(null);
            return;
        }

        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        // lấy mật khẩu băm đã lưu trong firestore
                        String savedPassword = document.getString("password");

                        // lấy salt để băm mật khẩu
                        String salt = document.getString("salt");

                        // băm lại mật khẩu mà người dùng đã nhập với salt
                        String hashedInputPassword = PasswordHasher.hashPassword(password, salt);

                        // kiểm tra 2 mật khẩu đã băm
                        if (savedPassword.equals(hashedInputPassword)) {
                            User user = new User();
                            user.setEmail(email);
                            user.setPassword(password);

                            sharedpref = getSharedPreferences("user", MODE_PRIVATE);
                            sharedpref.edit().putLong("userId", Long.valueOf(userId));

                            if (user.getFullName() != null) {
                                sharedpref.edit().putString("fullname", user.getFullName());
                            } else {
                                sharedpref.edit().putString("fullname", "<Người dùng>");
                            }

                            Log.d(">>> Login Activity", "Đã đăng nhập: " + userId);
                            callback.returnResult(user);
                            return;
                        }
                    }
                    callback.returnResult(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("!!! Firestore", "Không tìm thấy người dùng");
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String email = data.getStringExtra("email");
            String password = data.getStringExtra("password");

            editTextEmail.setText(email);
            editTextPassword.setText(password);

            Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
        }
    }
}