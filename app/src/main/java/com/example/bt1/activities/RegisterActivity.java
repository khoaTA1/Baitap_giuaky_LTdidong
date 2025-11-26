package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bt1.R;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Dòng import này không cần thiết, có thể xóa đi
// import com.example.bt1.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputUsername, textInputEmail, textInputPassword, textInputConfirmPassword;
    private TextInputEditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textLogin;
    public static final String USER_PREFS = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        textInputUsername = findViewById(R.id.text_input_username);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirm_password);
        buttonRegister = findViewById(R.id.button_register);
        textLogin = findViewById(R.id.text_login);

        // Lấy EditText từ bên trong TextInputLayout
        // Dùng if để tránh lỗi nếu getEditText() trả về null
        if (textInputUsername.getEditText() != null)
            editTextUsername = (TextInputEditText) textInputUsername.getEditText();
        if (textInputEmail.getEditText() != null)
            editTextEmail = (TextInputEditText) textInputEmail.getEditText();
        if (textInputPassword.getEditText() != null)
            editTextPassword = (TextInputEditText) textInputPassword.getEditText();
        if (textInputConfirmPassword.getEditText() != null)
            editTextConfirmPassword = (TextInputEditText) textInputConfirmPassword.getEditText();

        // Sửa lỗi ID cho khớp với register.xml
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonRegister.setOnClickListener(v -> {
            // Gọi hàm đăng ký khi nhấn nút
            registerUser();
        });

        textLogin.setOnClickListener(v -> {
            // Quay về màn hình đăng nhập
            finish();
        });
    }

    /**
     * Hàm chính để xử lý logic đăng ký
     */
    private void registerUser() {
        // 1. Lấy dữ liệu người dùng nhập
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // 2. Kiểm tra dữ liệu có hợp lệ không
        boolean isUsernameValid = validateUsername(username);
        boolean isEmailValid = validateEmail(email);
        boolean isPasswordValid = validatePassword(password, confirmPassword);

        // 3. Nếu tất cả đều hợp lệ, tiến hành lưu và kết thúc
        if (isUsernameValid && isEmailValid && isPasswordValid) {
            // Lưu tài khoản vào bộ nhớ tạm
            saveUserCredentials(email, password);

            // Thông báo đăng ký thành công
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

            // Tạo Intent để trả dữ liệu (email, password) về cho MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("email", email);
            resultIntent.putExtra("password", password);
            setResult(RESULT_OK, resultIntent);

            // Đóng màn hình đăng ký và quay về màn hình đăng nhập
            finish();
        }
    }

    /**
     * Hàm lưu tài khoản vào SharedPreferences
     */
    private void saveUserCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Key để lưu mật khẩu sẽ là "abc@email.com_password"
        // Điều này đảm bảo mỗi email chỉ có một mật khẩu
        editor.putString(email + "_password", password);

        // Áp dụng thay đổi
        editor.apply();
    }

    /**
     * Hàm kiểm tra tên người dùng
     */
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            textInputUsername.setError("Tên người dùng không được để trống");
            return false;
        }
        // Nếu hợp lệ, xóa thông báo lỗi
        textInputUsername.setError(null);
        return true;
    }

    /**
     * Hàm kiểm tra Email
     */
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            textInputEmail.setError("Email không được để trống");
            return false;
        }
        // Sử dụng hàm có sẵn của Android để kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.setError("Vui lòng nhập địa chỉ email hợp lệ");
            return false;
        }
        textInputEmail.setError(null);
        return true;
    }

    /**
     * Hàm kiểm tra Mật khẩu và Xác nhận mật khẩu
     */
    private boolean validatePassword(String password, String confirmPassword) {
        boolean isValid = true;

        // Kiểm tra ô mật khẩu
        if (password.isEmpty()) {
            textInputPassword.setError("Mật khẩu không được để trống");
            isValid = false;
        } else if (password.length() < 6) {
            textInputPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            textInputPassword.setError(null);
        }

        // Kiểm tra ô xác nhận mật khẩu
        if (confirmPassword.isEmpty()) {
            textInputConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            // Chỉ kiểm tra khớp nếu ô mật khẩu đã hợp lệ
            if (isValid) {
                textInputConfirmPassword.setError("Mật khẩu không khớp");
                isValid = false;
            }
        } else {
            textInputConfirmPassword.setError(null);
        }

        return isValid;
    }
}
