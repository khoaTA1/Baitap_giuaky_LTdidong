package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.example.bt1.R;

public class PaymentActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RadioGroup radioGroupPayment;
    private MaterialCardView cardCreditDetails;
    private CheckBox checkboxTerms;
    private Button buttonPayNow, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        radioGroupPayment = findViewById(R.id.radio_group_payment);
        cardCreditDetails = findViewById(R.id.card_credit_details);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        buttonPayNow = findViewById(R.id.button_pay_now);
        buttonCancel = findViewById(R.id.button_cancel);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Payment method selection
        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_banking) {
                cardCreditDetails.setVisibility(android.view.View.VISIBLE);
            } else {
                cardCreditDetails.setVisibility(android.view.View.GONE);
            }
        });

        // Payment button
        buttonPayNow.setOnClickListener(v -> {
            if (validatePayment()) {
                processPayment();
            }
        });

        // Cancel button
        buttonCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean validatePayment() {
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản sử dụng", Toast.LENGTH_SHORT).show();
            return false;
        }

        int selectedPaymentMethod = radioGroupPayment.getCheckedRadioButtonId();
        
        if (selectedPaymentMethod == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void processPayment() {
        // Show loading or processing state
        buttonPayNow.setEnabled(false);
        buttonPayNow.setText("Đang xử lý...");

        int selectedPaymentMethod = radioGroupPayment.getCheckedRadioButtonId();
        final String paymentMethodName;
        
        if (selectedPaymentMethod == R.id.radio_banking) {
            paymentMethodName = "Chuyển khoản ngân hàng";
        } else if (selectedPaymentMethod == R.id.radio_cod) {
            paymentMethodName = "Thanh toán khi nhận hàng";
        } else {
            paymentMethodName = "Không xác định";
        }

        // Simulate payment processing
        new android.os.Handler().postDelayed(() -> {
            // Payment successful
            Toast.makeText(this, "Đặt hàng thành công! Phương thức: " + paymentMethodName, Toast.LENGTH_LONG).show();
            
            // Navigate back to home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }, 2000);
    }
}