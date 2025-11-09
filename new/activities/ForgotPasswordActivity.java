package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt1.R;
import com.example.bt1.utils.Validator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout layoutEmail;
    private TextInputEditText textInputEmail;
    private MaterialButton buttonSendReset;
    private View textLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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

        // TODO: Implement actual password reset API call
        // For now, simulate success
        simulatePasswordReset(email);
    }

    private void simulatePasswordReset(String email) {
        // Simulate network delay
        buttonSendReset.postDelayed(() -> {
            // Show success message
            Toast.makeText(this, 
                "Đã gửi email khôi phục mật khẩu đến " + email, 
                Toast.LENGTH_LONG).show();

            // Re-enable button
            buttonSendReset.setEnabled(true);

            // Navigate back to login after 2 seconds
            buttonSendReset.postDelayed(() -> {
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 2000);

        }, 1500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
