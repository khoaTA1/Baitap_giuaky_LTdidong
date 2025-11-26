package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.bt1.R;

public class MainActivity extends AppCompatActivity {

    private static final int REGISTER_REQUEST_CODE = 1001;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private TextView textRegister;
    private TextView textForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

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

            if (isValidLogin(email, password)) {
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
                textInputPassword.setError("Tên đăng nhập hoặc mật khẩu không chính xác");
            }
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
     */
    private boolean isValidLogin(String email, String password) {
        // 1. Kiểm tra các trường không được để trống
        if (email.isEmpty()) {
            textInputEmail.setError("Email không được để trống");
            return false;
        }
        if (password.isEmpty()) {
            textInputPassword.setError("Mật khẩu không được để trống");
            return false;
        }

        // 2. KIỂM TRA TÀI KHOẢN MẶC ĐỊNH ĐỂ TEST
        if (email.equals("admin@gmail.com") && password.equals("123456")) {
            return true; // Đăng nhập thành công với tài khoản test
        }

        // 3. Nếu không phải tài khoản test, kiểm tra trong SharedPreferences (tài khoản đã đăng ký)
        SharedPreferences sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREFS, MODE_PRIVATE);
        String savedPassword = sharedPreferences.getString(email + "_password", null);

        // 4. So sánh mật khẩu người dùng nhập với mật khẩu đã lưu
        if (savedPassword != null && savedPassword.equals(password)) {
            return true; // Mật khẩu khớp -> Đăng nhập thành công
        }

        return false; // Mật khẩu không khớp hoặc người dùng không tồn tại
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
