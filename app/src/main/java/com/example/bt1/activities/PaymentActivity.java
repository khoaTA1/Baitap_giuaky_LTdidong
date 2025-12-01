package com.example.bt1.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.adapters.PaymentAddressAdapter;
import com.example.bt1.models.DeliveryAddress;
import com.example.bt1.models.Notification;
import com.example.bt1.models.Order;
import com.example.bt1.models.Product;
import com.example.bt1.models.Voucher;
import com.example.bt1.adapters.VoucherSelectAdapter;
import com.example.bt1.repositories.OrderRepo;
import com.example.bt1.repositories.VoucherRepo;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    private TextView textCartSize, textSubtotal, textShipping, textTotalAmount, textBankAmount, textVoucherDiscount;
    private RecyclerView recyclerSavedAddresses;
    private TextView textNoAddresses;
    private TextInputEditText editTextAddress;
    private MaterialButton btnManageAddresses;
    private MaterialCardView cardVoucher;
    private TextView textVoucherCode, textVoucherValue;

    private double subtotal, shipping, total, voucherDiscount = 0;
    private Voucher selectedVoucher = null;
    private VoucherRepo voucherRepo;
    private int cartSize;
    private List<Product> selectedProducts; // Danh sách sản phẩm đã chọn
    private List<DeliveryAddress> savedAddresses;
    private PaymentAddressAdapter addressAdapter;
    private DeliveryAddress selectedAddress;

    private static final String CHANNEL_ID = "payment_notification_channel";
    private OrderRepo orderRepo;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        
        // Initialize OrderRepo and Firebase
        orderRepo = new OrderRepo();
        voucherRepo = new VoucherRepo();
        db = FirebaseFirestore.getInstance();
        savedAddresses = new ArrayList<>();

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
        loadSavedAddresses();
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
        
        recyclerSavedAddresses = findViewById(R.id.recycler_saved_addresses);
        textNoAddresses = findViewById(R.id.text_no_addresses);
        editTextAddress = findViewById(R.id.edit_text_address);
        btnManageAddresses = findViewById(R.id.btn_manage_addresses_payment);
        cardVoucher = findViewById(R.id.card_voucher);
        textVoucherCode = findViewById(R.id.text_voucher_code);
        textVoucherValue = findViewById(R.id.text_voucher_value);
        textVoucherDiscount = findViewById(R.id.text_voucher_discount);
        
        // Setup RecyclerView for saved addresses
        recyclerSavedAddresses.setLayoutManager(new LinearLayoutManager(this));
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
        
        // Manage addresses button
        if (btnManageAddresses != null) {
            btnManageAddresses.setOnClickListener(v -> {
                Intent intent = new Intent(this, DeliveryAddressesActivity.class);
                startActivity(intent);
            });
        }
        
        // Voucher card click
        if (cardVoucher != null) {
            cardVoucher.setOnClickListener(v -> showVoucherBottomSheet());
        }
    }
    
    private void loadSavedAddresses() {
        String userId = SharedPreferencesManager.getInstance(this).getUserId();
        if (userId == null || userId.isEmpty()) {
            showNoAddressesState();
            return;
        }
        
        db.collection("delivery_addresses")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedAddresses.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DeliveryAddress address = document.toObject(DeliveryAddress.class);
                        address.setId(document.getId());
                        
                        // Debug log
                        Log.d("PaymentActivity", "Address loaded - Name: " + address.getRecipientName() + 
                              ", Phone: " + address.getPhoneNumber() + 
                              ", Label: " + address.getLabel());
                        
                        savedAddresses.add(address);
                    }
                    
                    if (savedAddresses.isEmpty()) {
                        showNoAddressesState();
                    } else {
                        showAddressesList();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PaymentActivity", "Error loading addresses", e);
                    showNoAddressesState();
                });
    }
    
    private void showAddressesList() {
        if (recyclerSavedAddresses != null) {
            recyclerSavedAddresses.setVisibility(View.VISIBLE);
        }
        if (textNoAddresses != null) {
            textNoAddresses.setVisibility(View.GONE);
        }
        
        addressAdapter = new PaymentAddressAdapter(this, savedAddresses, (address, position) -> {
            selectedAddress = address;
            // Clear custom address input when selecting a saved address
            if (editTextAddress != null) {
                editTextAddress.setText("");
            }
        });
        recyclerSavedAddresses.setAdapter(addressAdapter);
        
        // Auto-select default address
        selectedAddress = addressAdapter.getSelectedAddress();
    }
    
    private void showNoAddressesState() {
        if (recyclerSavedAddresses != null) {
            recyclerSavedAddresses.setVisibility(View.GONE);
        }
        if (textNoAddresses != null) {
            textNoAddresses.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload addresses when returning from address management
        loadSavedAddresses();
    }

    private void displayPaymentInfo() {
        // Calculate discount and shipping based on voucher
        double displayShipping = shipping;
        if (selectedVoucher != null) {
            // Tính giảm giá từ subtotal (không bao gồm ship)
            voucherDiscount = selectedVoucher.calculateDiscount(subtotal);
            
            // Nếu có free ship, shipping hiển thị = 0
            if (selectedVoucher.isFreeShip()) {
                displayShipping = 0;
            }
        } else {
            voucherDiscount = 0;
        }
        
        // Update total: subtotal + shipping hiển thị - voucher discount
        total = subtotal + displayShipping - voucherDiscount;
        
        // Update cart size
        if (textCartSize != null) {
            textCartSize.setText(cartSize + " sản phẩm");
        }

        // Update subtotal
        if (textSubtotal != null) {
            textSubtotal.setText(String.format("%,.0f₫", subtotal));
        }

        // Update shipping (hiển thị 0đ màu đỏ nếu free ship)
        if (textShipping != null) {
            textShipping.setText(String.format("%,.0f₫", displayShipping));
            if (selectedVoucher != null && selectedVoucher.isFreeShip() && displayShipping == 0) {
                textShipping.setTextColor(0xFFFF0000); // Màu đỏ
            } else {
                textShipping.setTextColor(0xFF000000); // Màu đen
            }
        }
        
        // Update voucher discount (hiển thị số tiền giảm thực tế)
        if (textVoucherDiscount != null) {
            if (voucherDiscount > 0) {
                textVoucherDiscount.setVisibility(View.VISIBLE);
                findViewById(R.id.layout_voucher_discount).setVisibility(View.VISIBLE);
                textVoucherDiscount.setText(String.format("-%,.0f₫", voucherDiscount));
            } else {
                findViewById(R.id.layout_voucher_discount).setVisibility(View.GONE);
            }
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
        
        // Update voucher UI
        updateVoucherUI();
    }

    private boolean validatePayment() {
        // Check if either a saved address is selected or custom address is entered
        String customAddress = editTextAddress != null ? editTextAddress.getText().toString().trim() : "";
        
        if (selectedAddress == null && customAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng hoặc nhập địa chỉ tùy chỉnh", Toast.LENGTH_SHORT).show();
            return false;
        }
        
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
        
        // Determine delivery address
        String deliveryAddress;
        if (selectedAddress != null) {
            // Use saved address
            deliveryAddress = selectedAddress.getFullAddress();
        } else {
            // Use custom address
            deliveryAddress = editTextAddress.getText().toString().trim();
        }

        Order newOrder = new Order(orderId, orderDate, total, "Đang xử lý", selectedProducts);
        newOrder.setDeliveryAddress(deliveryAddress);
        
        // Save voucher info if applied
        if (selectedVoucher != null) {
            newOrder.setVoucherCode(selectedVoucher.getCode());
            // voucherDiscount đã được tính đúng trong displayPaymentInfo (chỉ từ subtotal)
            newOrder.setVoucherDiscount(voucherDiscount);
        }
        
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
    
    private void showVoucherBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_vouchers, null);
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = 
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        
        TextView textOrderAmount = bottomSheetView.findViewById(R.id.text_order_amount);
        RecyclerView recyclerVouchers = bottomSheetView.findViewById(R.id.recycler_vouchers);
        TextView textNoVouchers = bottomSheetView.findViewById(R.id.text_no_vouchers);
        
        textOrderAmount.setText(String.format("%,.0fđ", subtotal));
        recyclerVouchers.setLayoutManager(new LinearLayoutManager(this));
        
        // Load available vouchers
        voucherRepo.getAvailableVouchers(new VoucherRepo.VoucherCallback() {
            @Override
            public void onSuccess(List<Voucher> vouchers) {
                Log.d("PaymentActivity", "Loaded vouchers: " + vouchers.size());
                if (vouchers.isEmpty()) {
                    textNoVouchers.setVisibility(View.VISIBLE);
                    recyclerVouchers.setVisibility(View.GONE);
                    Log.d("PaymentActivity", "No vouchers available - showing empty state");
                } else {
                    textNoVouchers.setVisibility(View.GONE);
                    recyclerVouchers.setVisibility(View.VISIBLE);
                    
                    Log.d("PaymentActivity", "Creating adapter with " + vouchers.size() + " vouchers for order: " + subtotal);
                    VoucherSelectAdapter adapter = new VoucherSelectAdapter(vouchers, subtotal, voucher -> {
                        selectedVoucher = voucher;
                        displayPaymentInfo();
                        bottomSheetDialog.dismiss();
                        Toast.makeText(PaymentActivity.this, 
                                "Đã áp dụng voucher " + voucher.getCode(), 
                                Toast.LENGTH_SHORT).show();
                    });
                    recyclerVouchers.setAdapter(adapter);
                }
            }
            
            @Override
            public void onFailure(String error) {
                Log.e("PaymentActivity", "Error loading vouchers: " + error);
                textNoVouchers.setVisibility(View.VISIBLE);
                recyclerVouchers.setVisibility(View.GONE);
                Toast.makeText(PaymentActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        
        bottomSheetDialog.show();
    }
    
    private void updateVoucherUI() {
        if (cardVoucher == null) return;
        
        if (selectedVoucher != null) {
            textVoucherCode.setText(selectedVoucher.getCode());
            String valueText = String.format("Giảm %d%%", selectedVoucher.getDiscountPercent());
            if (selectedVoucher.isFreeShip()) {
                valueText += " + Miễn phí ship";
            }
            textVoucherValue.setText(valueText);
        } else {
            textVoucherCode.setText("Chọn mã giảm giá");
            textVoucherValue.setText("Tiết kiệm thêm cho đơn hàng");
        }
    }
}
