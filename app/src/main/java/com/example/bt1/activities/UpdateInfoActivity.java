package com.example.bt1.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt1.R;
import com.example.bt1.models.User;
import com.example.bt1.utils.PasswordHasher;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateInfoActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputLayout textInputFullName, textInputEmail, textInputPhone, textInputAddress;
    private TextInputLayout textInputCurrentPassword, textInputNewPassword, textInputConfirmPassword;
    private TextInputEditText editTextFullName, editTextEmail, editTextPhone, editTextAddress;
    private TextInputEditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button btnSaveChanges;
    private com.google.android.material.button.MaterialButton btnManageAddresses;
    private SharedPreferencesManager prefsManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        prefsManager = SharedPreferencesManager.getInstance(this);
        db = FirebaseFirestore.getInstance();

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        textInputFullName = findViewById(R.id.text_input_fullname);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPhone = findViewById(R.id.text_input_phone);
        textInputAddress = findViewById(R.id.text_input_address);
        textInputCurrentPassword = findViewById(R.id.text_input_current_password);
        textInputNewPassword = findViewById(R.id.text_input_new_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirm_password);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnManageAddresses = findViewById(R.id.btn_manage_addresses);

        if (textInputFullName.getEditText() != null) {
            editTextFullName = (TextInputEditText) textInputFullName.getEditText();
        }
        if (textInputEmail.getEditText() != null) {
            editTextEmail = (TextInputEditText) textInputEmail.getEditText();
        }
        if (textInputPhone.getEditText() != null) {
            editTextPhone = (TextInputEditText) textInputPhone.getEditText();
        }
        if (textInputAddress.getEditText() != null) {
            editTextAddress = (TextInputEditText) textInputAddress.getEditText();
        }
        if (textInputCurrentPassword.getEditText() != null) {
            editTextCurrentPassword = (TextInputEditText) textInputCurrentPassword.getEditText();
        }
        if (textInputNewPassword.getEditText() != null) {
            editTextNewPassword = (TextInputEditText) textInputNewPassword.getEditText();
        }
        if (textInputConfirmPassword.getEditText() != null) {
            editTextConfirmPassword = (TextInputEditText) textInputConfirmPassword.getEditText();
        }
    }

    private void loadUserData() {
        if (prefsManager.isLoggedIn()) {
            User user = prefsManager.getUser();
            if (user != null) {
                editTextFullName.setText(user.getFullName());
                editTextEmail.setText(user.getEmail());
                editTextPhone.setText(user.getPhone());
                editTextAddress.setText(user.getAddress());
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(v -> {
            if (validateInputs()) {
                saveUserData();
            }
        });
        
        btnManageAddresses.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, DeliveryAddressesActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            textInputFullName.setError("Họ tên không được để trống");
            return false;
        }
        textInputFullName.setError(null);

        if (!phone.isEmpty() && phone.length() < 10) {
            textInputPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }
        textInputPhone.setError(null);

        return true;
    }

    private void saveUserData() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Disable button để tránh click nhiều lần
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Đang lưu...");

        // Lấy email từ SharedPreferences để query Firebase
        String userEmail = prefsManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            btnSaveChanges.setEnabled(true);
            btnSaveChanges.setText("Lưu thay đổi");
            return;
        }

        // Kiểm tra nếu người dùng muốn đổi mật khẩu
        boolean changePassword = !TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirmPassword);
        
        if (changePassword) {
            // Validate password fields
            if (TextUtils.isEmpty(currentPassword)) {
                textInputCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Lưu thay đổi");
                return;
            }
            if (TextUtils.isEmpty(newPassword)) {
                textInputNewPassword.setError("Vui lòng nhập mật khẩu mới");
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Lưu thay đổi");
                return;
            }
            if (newPassword.length() < 6) {
                textInputNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Lưu thay đổi");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                textInputConfirmPassword.setError("Mật khẩu không khớp");
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Lưu thay đổi");
                return;
            }
            
            // Xác thực mật khẩu hiện tại và cập nhật
            verifyAndUpdatePassword(userEmail, currentPassword, newPassword, fullName, phone, address);
        } else {
            // Chỉ cập nhật thông tin không đổi mật khẩu
            updateUserInfo(userEmail, fullName, phone, address, null, null);
        }
    }
    
    private void verifyAndUpdatePassword(String userEmail, String currentPassword, String newPassword, String fullName, String phone, String address) {
        // Query Firebase bằng email để lấy document ID
        db.collection("users").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    String storedHashedPassword = queryDocumentSnapshots.getDocuments().get(0).getString("password");
                    String salt = queryDocumentSnapshots.getDocuments().get(0).getString("salt");
                    
                    // Verify current password
                    String hashedCurrentPassword = PasswordHasher.hashPassword(currentPassword, salt);
                    
                    if (storedHashedPassword != null && storedHashedPassword.equals(hashedCurrentPassword)) {
                        // Mật khẩu đúng, tạo mật khẩu mới
                        String newSalt = PasswordHasher.generateSalt();
                        String hashedNewPassword = PasswordHasher.hashPassword(newPassword, newSalt);
                        
                        // Cập nhật cả thông tin và mật khẩu
                        updateUserInfoByDocId(userId, fullName, phone, address, hashedNewPassword, newSalt);
                    } else {
                        // Mật khẩu sai
                        runOnUiThread(() -> {
                            textInputCurrentPassword.setError("Mật khẩu hiện tại không đúng");
                            btnSaveChanges.setEnabled(true);
                            btnSaveChanges.setText("Lưu thay đổi");
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        btnSaveChanges.setEnabled(true);
                        btnSaveChanges.setText("Lưu thay đổi");
                    });
                }
            })
            .addOnFailureListener(e -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Lưu thay đổi");
                });
                Log.e("UpdateInfo", "Error verifying password", e);
            });
    }
    
    private void updateUserInfo(String userEmail, String fullName, String phone, String address, String hashedPassword, String salt) {
        // Query Firebase bằng email để lấy document ID
        db.collection("users").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    updateUserInfoByDocId(userId, fullName, phone, address, hashedPassword, salt);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        btnSaveChanges.setEnabled(true);
                        btnSaveChanges.setText("Lưu thay đổi");
                    });
                }
            })
            .addOnFailureListener(e -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Lưu thay đổi");
                });
                Log.e("UpdateInfo", "Error querying user", e);
            });
    }
    
    private void updateUserInfoByDocId(String userId, String fullName, String phone, String address, String hashedPassword, String salt) {
        // Chuẩn bị dữ liệu cập nhật
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("fullname", fullName);
        if (phone != null && !phone.isEmpty()) {
            updates.put("phone", phone);
        }
        if (address != null && !address.isEmpty()) {
            updates.put("address", address);
        }
        
        // Nếu có mật khẩu mới, thêm vào updates
        if (hashedPassword != null && salt != null) {
            updates.put("password", hashedPassword);
            updates.put("salt", salt);
        }
        
        // Cập nhật lên Firebase
        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                // Cập nhật SharedPreferences
                User user = prefsManager.getUser();
                if (user == null) user = new User();
                
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setAddress(address);
                prefsManager.updateUser(user);
                
                runOnUiThread(() -> {
                    if (hashedPassword != null) {
                        Toast.makeText(this, "Cập nhật thông tin và mật khẩu thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
                
                Log.d("UpdateInfo", "User info updated successfully");
            })
            .addOnFailureListener(e -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Lưu thay đổi");
                });
                Log.e("UpdateInfo", "Error updating user info", e);
            });
    }
}
