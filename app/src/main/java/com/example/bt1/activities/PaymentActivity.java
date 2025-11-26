package com.example.bt1.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.example.bt1.R;
import com.example.bt1.models.Notification;
import com.example.bt1.models.Order;
import com.example.bt1.models.Product;
import com.example.bt1.repositories.OrderRepo;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RadioGroup radioGroupPayment;
    private MaterialCardView cardCreditDetails;
    private CheckBox checkboxTerms;
    private Button buttonPayNow;
    private TextView textCartSize, textSubtotal, textShipping, textTotalAmount, textBankAmount;

    private double subtotal, shipping, total;
    private int cartSize;
    private List<Product> selectedProducts; // Danh sách sản phẩm đã chọn

    private static final String CHANNEL_ID = "payment_notification_channel";
    private OrderRepo orderRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        
        // Initialize OrderRepo
        orderRepo = new OrderRepo();

        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            subtotal = intent.getDoubleExtra("subtotal", 0);
            shipping = intent.getDoubleExtra("shipping", 0);
            total = intent.getDoubleExtra("total", 0);
            cartSize = intent.getIntExtra("cart_size", 0);
            
            // Lấy danh sách sản phẩm đã chọn từ Intent
            String selectedProductsJson = intent.getStringExtra("selected_products");
            if (selectedProductsJson != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Product>>() {}.getType();
                selectedProducts = gson.fromJson(selectedProductsJson, type);
            }
        }
        
        if (selectedProducts == null) {
            selectedProducts = new ArrayList<>();
        }

        createNotificationChannel();
        initViews();
        setupListeners();
        displayPaymentInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        radioGroupPayment = findViewById(R.id.radio_group_payment);
        cardCreditDetails = findViewById(R.id.card_credit_details);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        buttonPayNow = findViewById(R.id.button_pay_now);

        textCartSize = findViewById(R.id.text_cart_size);
        textSubtotal = findViewById(R.id.text_subtotal);
        textShipping = findViewById(R.id.text_shipping);
        textTotalAmount = findViewById(R.id.text_total_amount);
        textBankAmount = findViewById(R.id.text_bank_amount);
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
    }

    private void displayPaymentInfo() {
        // Update cart size
        if (textCartSize != null) {
            textCartSize.setText(cartSize + " sản phẩm");
        }

        // Update subtotal
        if (textSubtotal != null) {
            textSubtotal.setText(String.format("%,.0f₫", subtotal));
        }

        // Update shipping
        if (textShipping != null) {
            textShipping.setText(String.format("%,.0f₫", shipping));
        }

        // Update total
        if (textTotalAmount != null) {
            textTotalAmount.setText(String.format("%,.0f₫", total));
        }

        // Update bank transfer amount
        if (textBankAmount != null) {
            textBankAmount.setText(String.format("💰 Số tiền: %,.0f₫", total));
        }

        // Update payment button text
        if (buttonPayNow != null) {
            buttonPayNow.setText(String.format("Thanh Toán %,.0f₫", total));
        }
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

            // Save order to history
            saveOrder();

            // Create and save notification
            saveAndSendNotification();

            // Xóa CHỈ các sản phẩm đã thanh toán khỏi giỏ hàng (không xóa toàn bộ)
            removeSelectedProductsFromCart();

            // Navigate back to home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void saveOrder() {
        // Sử dụng danh sách sản phẩm đã chọn thay vì toàn bộ giỏ hàng
        if (selectedProducts == null || selectedProducts.isEmpty()) return;

        String orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Order newOrder = new Order(orderId, orderDate, total, "Đang xử lý", selectedProducts);
        
        // Lưu vào Firebase Firestore (nguồn chính thực)
        String userId = SharedPreferencesManager.getInstance(this).getUserId();
        if (userId != null && orderRepo != null) {
            orderRepo.createOrder(userId, newOrder, (success, message) -> {
                if (success) {
                    Log.d(">>> PaymentActivity", "Đơn hàng đã lưu vào Firebase: " + orderId);
                } else {
                    Log.e(">>> PaymentActivity", "Lỗi lưu đơn hàng vào Firebase: " + message);
                    // Nếu lỗi Firebase, thông báo cho user
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(PaymentActivity.this, 
                            "Không thể lưu đơn hàng. Vui lòng kiểm tra kết nối mạng.", 
                            android.widget.Toast.LENGTH_LONG).show();
                    });
                }
            });
        } else {
            Log.w(">>> PaymentActivity", "Không thể lưu order: userId null hoặc orderRepo null");
        }
    }

    private void saveAndSendNotification() {
        String title = "Thanh toán thành công";
        String message = String.format(Locale.getDefault(), "Đơn hàng của bạn với tổng giá trị %,.0f₫ đã được đặt thành công.", total);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Notification newNotification = new Notification(title, message, timestamp);

        // Save notification to SharedPreferences
        SharedPreferencesManager.getInstance(this).addNotification(newNotification);

        // Create an intent to open NotificationActivity
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create and show system notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Payment Notifications";
            String description = "Channel for payment status notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Xóa chỉ các sản phẩm đã thanh toán khỏi giỏ hàng
     */
    private void removeSelectedProductsFromCart() {
        // Lấy userId từ SharedPreferencesManager
        SharedPreferencesManager prefManager = SharedPreferencesManager.getInstance(this);
        String userId = prefManager.getUserId();
        String cartKey = userId != null ? "cart_" + userId : "cart_guest";
        
        // Lấy toàn bộ giỏ hàng
        android.content.SharedPreferences cartPrefs = getSharedPreferences(cartKey, MODE_PRIVATE);
        String json = cartPrefs.getString("cart_products", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        List<Product> allCartProducts = gson.fromJson(json, type);
        
        if (allCartProducts == null || selectedProducts == null) return;
        
        // Xóa các sản phẩm đã thanh toán
        for (Product selectedProduct : selectedProducts) {
            allCartProducts.removeIf(cartProduct -> 
                cartProduct.getName().equals(selectedProduct.getName()));
        }
        
        // Lưu lại giỏ hàng sau khi xóa
        String updatedJson = gson.toJson(allCartProducts);
        cartPrefs.edit().putString("cart_products", updatedJson).apply();
    }
}
