package com.example.bt1.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt1.R;
import com.example.bt1.models.DeliveryAddress;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditAddressActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView textTitle;
    private TextInputLayout textInputRecipientName, textInputPhone, textInputProvince;
    private TextInputLayout textInputDistrict, textInputWard, textInputDetailAddress, textInputLabel;
    private TextInputEditText editRecipientName, editPhone, editProvince;
    private TextInputEditText editDistrict, editWard, editDetailAddress, editLabel;
    private CheckBox checkboxDefault;
    private MaterialButton btnSave;

    private FirebaseFirestore db;
    private String userId;
    private boolean isEditMode = false;
    private DeliveryAddress currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        db = FirebaseFirestore.getInstance();
        userId = SharedPreferencesManager.getInstance(this).getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();

        // Check if editing existing address
        if (getIntent().hasExtra("address") && getIntent().getBooleanExtra("is_edit", false)) {
            isEditMode = true;
            currentAddress = (DeliveryAddress) getIntent().getSerializableExtra("address");
            loadAddressData();
        } else {
            // Pre-fill recipient name and phone from user profile for new address
            prefillUserInfo();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        textTitle = findViewById(R.id.text_title);
        textInputRecipientName = findViewById(R.id.text_input_recipient_name);
        textInputPhone = findViewById(R.id.text_input_phone);
        textInputProvince = findViewById(R.id.text_input_province);
        textInputDistrict = findViewById(R.id.text_input_district);
        textInputWard = findViewById(R.id.text_input_ward);
        textInputDetailAddress = findViewById(R.id.text_input_detail_address);
        textInputLabel = findViewById(R.id.text_input_label);
        checkboxDefault = findViewById(R.id.checkbox_default);
        btnSave = findViewById(R.id.btn_save);

        editRecipientName = (TextInputEditText) textInputRecipientName.getEditText();
        editPhone = (TextInputEditText) textInputPhone.getEditText();
        editProvince = (TextInputEditText) textInputProvince.getEditText();
        editDistrict = (TextInputEditText) textInputDistrict.getEditText();
        editWard = (TextInputEditText) textInputWard.getEditText();
        editDetailAddress = (TextInputEditText) textInputDetailAddress.getEditText();
        editLabel = (TextInputEditText) textInputLabel.getEditText();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveAddress();
            }
        });
    }

    private void loadAddressData() {
        if (currentAddress == null) return;

        textTitle.setText("Sửa địa chỉ");
        btnSave.setText("Cập nhật");

        // Load recipient name - auto-fill from profile if missing
        String recipientName = currentAddress.getRecipientName();
        if (recipientName != null && !recipientName.isEmpty()) {
            editRecipientName.setText(recipientName);
        } else {
            // Auto-fill from user profile if missing
            SharedPreferencesManager spManager = SharedPreferencesManager.getInstance(this);
            String userName = spManager.getUserName();
            if (userName != null && !userName.isEmpty()) {
                editRecipientName.setText(userName);
            }
        }
        
        // Load phone number - auto-fill from profile if missing
        String phoneNumber = currentAddress.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            editPhone.setText(phoneNumber);
        } else {
            // Auto-fill from user profile if missing
            SharedPreferencesManager spManager = SharedPreferencesManager.getInstance(this);
            String userPhone = spManager.getUserPhone();
            if (userPhone != null && !userPhone.isEmpty()) {
                editPhone.setText(userPhone);
            }
        }
        
        editProvince.setText(currentAddress.getProvince());
        editDistrict.setText(currentAddress.getDistrict());
        editWard.setText(currentAddress.getWard());
        editDetailAddress.setText(currentAddress.getDetailAddress());
        editLabel.setText(currentAddress.getLabel());
        checkboxDefault.setChecked(currentAddress.getIsDefault());
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String recipientName = editRecipientName.getText().toString().trim();
        if (recipientName.isEmpty()) {
            textInputRecipientName.setError("Vui lòng nhập tên người nhận");
            isValid = false;
        } else {
            textInputRecipientName.setError(null);
        }

        String phone = editPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            textInputPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else if (phone.length() < 10) {
            textInputPhone.setError("Số điện thoại không hợp lệ");
            isValid = false;
        } else {
            textInputPhone.setError(null);
        }

        String province = editProvince.getText().toString().trim();
        if (province.isEmpty()) {
            textInputProvince.setError("Vui lòng nhập tỉnh/thành phố");
            isValid = false;
        } else {
            textInputProvince.setError(null);
        }

        String district = editDistrict.getText().toString().trim();
        if (district.isEmpty()) {
            textInputDistrict.setError("Vui lòng nhập quận/huyện");
            isValid = false;
        } else {
            textInputDistrict.setError(null);
        }

        String ward = editWard.getText().toString().trim();
        if (ward.isEmpty()) {
            textInputWard.setError("Vui lòng nhập phường/xã");
            isValid = false;
        } else {
            textInputWard.setError(null);
        }

        String detailAddress = editDetailAddress.getText().toString().trim();
        if (detailAddress.isEmpty()) {
            textInputDetailAddress.setError("Vui lòng nhập số nhà, tên đường");
            isValid = false;
        } else {
            textInputDetailAddress.setError(null);
        }

        return isValid;
    }
    
    private void prefillUserInfo() {
        // Auto-fill recipient name and phone from user profile
        SharedPreferencesManager spManager = SharedPreferencesManager.getInstance(this);
        
        String userName = spManager.getUserName();
        if (userName != null && !userName.isEmpty() && editRecipientName != null) {
            editRecipientName.setText(userName);
        }
        
        String userPhone = spManager.getUserPhone();
        if (userPhone != null && !userPhone.isEmpty() && editPhone != null) {
            editPhone.setText(userPhone);
        }
    }

    private void saveAddress() {
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        String recipientName = editRecipientName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String province = editProvince.getText().toString().trim();
        String district = editDistrict.getText().toString().trim();
        String ward = editWard.getText().toString().trim();
        String detailAddress = editDetailAddress.getText().toString().trim();
        String label = editLabel.getText().toString().trim();
        if (label.isEmpty()) label = "Địa chỉ";
        boolean isDefault = checkboxDefault.isChecked();

        // If setting as default, first remove default from other addresses
        if (isDefault) {
            String finalLabel = label;
            removeOtherDefaults(() -> {
                saveAddressToFirebase(recipientName, phone, province, district, ward, detailAddress, finalLabel, isDefault);
            });
        } else {
            saveAddressToFirebase(recipientName, phone, province, district, ward, detailAddress, label, isDefault);
        }
    }

    private void removeOtherDefaults(Runnable onComplete) {
        db.collection("delivery_addresses")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("is_default", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Skip current address if editing
                        if (isEditMode && currentAddress != null && document.getId().equals(currentAddress.getId())) {
                            continue;
                        }
                        db.collection("delivery_addresses")
                                .document(document.getId())
                                .update("is_default", false);
                    }
                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddEditAddress", "Error removing other defaults", e);
                    onComplete.run();
                });
    }

    private void saveAddressToFirebase(String recipientName, String phone, String province,
                                       String district, String ward, String detailAddress,
                                       String label, boolean isDefault) {
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("user_id", userId);
        addressData.put("recipient_name", recipientName);
        addressData.put("phone_number", phone);
        addressData.put("province", province);
        addressData.put("district", district);
        addressData.put("ward", ward);
        addressData.put("detail_address", detailAddress);
        addressData.put("label", label);
        addressData.put("is_default", isDefault);
        addressData.put("created_at", System.currentTimeMillis());
        
        Log.d("AddEditAddress", "Saving address - Name: " + recipientName + 
              ", Phone: " + phone + ", Label: " + label + ", Default: " + isDefault);

        if (isEditMode && currentAddress != null && currentAddress.getId() != null) {
            // Update existing address
            Log.d("AddEditAddress", "Updating existing address ID: " + currentAddress.getId());
            db.collection("delivery_addresses")
                    .document(currentAddress.getId())
                    .update(addressData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AddEditAddress", "Address updated successfully");
                        Toast.makeText(this, "Đã cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AddEditAddress", "Error updating address", e);
                        Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("Cập nhật");
                    });
        } else {
            // Add new address
            Log.d("AddEditAddress", "Adding new address");
            db.collection("delivery_addresses")
                    .add(addressData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("AddEditAddress", "Address added successfully with ID: " + documentReference.getId());
                        Toast.makeText(this, "Đã thêm địa chỉ mới", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi thêm địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("Lưu địa chỉ");
                    });
        }
    }
}
