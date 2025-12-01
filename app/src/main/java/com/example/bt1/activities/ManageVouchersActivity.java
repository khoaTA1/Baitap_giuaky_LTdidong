package com.example.bt1.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.VoucherAdapter;
import com.example.bt1.models.Voucher;
import com.example.bt1.repositories.VoucherRepo;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManageVouchersActivity extends AppCompatActivity implements VoucherAdapter.OnVoucherActionListener {
    
    private ImageView btnBack, btnAddVoucher;
    private RecyclerView recyclerVouchers;
    private LinearLayout emptyState;
    private TextView textTotalVouchers, textActiveVouchers;
    
    private VoucherAdapter adapter;
    private List<Voucher> voucherList;
    private VoucherRepo voucherRepo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vouchers);
        
        initViews();
        setupListeners();
        loadVouchers();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnAddVoucher = findViewById(R.id.btn_add_voucher);
        recyclerVouchers = findViewById(R.id.recycler_vouchers);
        emptyState = findViewById(R.id.empty_state);
        textTotalVouchers = findViewById(R.id.text_total_vouchers);
        textActiveVouchers = findViewById(R.id.text_active_vouchers);
        
        voucherList = new ArrayList<>();
        voucherRepo = new VoucherRepo();
        
        adapter = new VoucherAdapter(voucherList, this);
        recyclerVouchers.setLayoutManager(new LinearLayoutManager(this));
        recyclerVouchers.setAdapter(adapter);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnAddVoucher.setOnClickListener(v -> showAddVoucherDialog(null, -1));
    }
    
    private void loadVouchers() {
        voucherRepo.getAllVouchers(new VoucherRepo.VoucherCallback() {
            @Override
            public void onSuccess(List<Voucher> vouchers) {
                voucherList.clear();
                voucherList.addAll(vouchers);
                adapter.notifyDataSetChanged();
                
                updateStatistics();
                
                if (vouchers.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerVouchers.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerVouchers.setVisibility(View.VISIBLE);
                }
            }
            
            @Override
            public void onFailure(String error) {
                Toast.makeText(ManageVouchersActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateStatistics() {
        int total = voucherList.size();
        int active = 0;
        for (Voucher v : voucherList) {
            if (v.isActive()) active++;
        }
        
        textTotalVouchers.setText(String.valueOf(total));
        textActiveVouchers.setText(String.valueOf(active));
    }
    
    private void showAddVoucherDialog(Voucher existingVoucher, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_voucher, null);
        
        EditText editCode = dialogView.findViewById(R.id.edit_voucher_code);
        EditText editPercent = dialogView.findViewById(R.id.edit_discount_percent);
        EditText editMinOrder = dialogView.findViewById(R.id.edit_min_order);
        EditText editQuantity = dialogView.findViewById(R.id.edit_quantity);
        CheckBox checkFreeShip = dialogView.findViewById(R.id.checkbox_free_ship);
        TextInputLayout layoutFreeShipAmount = dialogView.findViewById(R.id.layout_free_ship_amount);
        EditText editFreeShipAmount = dialogView.findViewById(R.id.edit_free_ship_amount);
        EditText editExpiryDate = dialogView.findViewById(R.id.edit_expiry_date);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);
        
        // Free ship checkbox listener
        checkFreeShip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutFreeShipAmount.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        
        // Date picker
        editExpiryDate.setOnClickListener(v -> showDatePicker(editExpiryDate));
        
        // Fill existing data if editing
        if (existingVoucher != null) {
            editCode.setText(existingVoucher.getCode());
            editCode.setEnabled(false); // Don't allow changing code
            editPercent.setText(String.valueOf(existingVoucher.getDiscountPercent()));
            editMinOrder.setText(String.valueOf((int) existingVoucher.getMinOrderAmount()));
            editQuantity.setText(String.valueOf(existingVoucher.getTotalQuantity()));
            checkFreeShip.setChecked(existingVoucher.isFreeShip());
            if (existingVoucher.isFreeShip()) {
                editFreeShipAmount.setText(String.valueOf((int) existingVoucher.getFreeShipAmount()));
            }
            editExpiryDate.setText(existingVoucher.getExpiryDate());
            editDescription.setText(existingVoucher.getDescription());
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingVoucher == null ? "Thêm Voucher Mới" : "Chỉnh Sửa Voucher");
        builder.setView(dialogView);
        builder.setPositiveButton(existingVoucher == null ? "Thêm" : "Cập nhật", (dialog, which) -> {
            saveVoucher(existingVoucher, position, editCode, editPercent, editMinOrder, 
                    editQuantity, checkFreeShip, editFreeShipAmount, 
                    editExpiryDate, editDescription);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    
    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            editText.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void saveVoucher(Voucher existing, int position, EditText editCode, EditText editPercent,
                            EditText editMinOrder, EditText editQuantity,
                            CheckBox checkFreeShip, EditText editFreeShipAmount, 
                            EditText editExpiryDate, EditText editDescription) {
        
        // Validation
        String code = editCode.getText().toString().trim().toUpperCase();
        String percentStr = editPercent.getText().toString().trim();
        String minOrderStr = editMinOrder.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        String expiryDate = editExpiryDate.getText().toString().trim();
        
        if (code.isEmpty() || percentStr.isEmpty() || minOrderStr.isEmpty() || 
                quantityStr.isEmpty() || expiryDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int percent = Integer.parseInt(percentStr);
            double minOrder = Double.parseDouble(minOrderStr);
            int quantity = Integer.parseInt(quantityStr);
            
            if (percent <= 0 || percent > 100) {
                Toast.makeText(this, "Phần trăm giảm giá phải từ 1-100", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (minOrder < 0) {
                Toast.makeText(this, "Giá trị đơn tối thiểu không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (quantity <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double freeShipAmount = 0;
            if (checkFreeShip.isChecked()) {
                String freeShipStr = editFreeShipAmount.getText().toString().trim();
                if (!freeShipStr.isEmpty()) {
                    freeShipAmount = Double.parseDouble(freeShipStr);
                    if (freeShipAmount < 0) {
                        Toast.makeText(this, "Giá trị miễn phí ship không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            
            String description = editDescription.getText().toString().trim();
            
            Voucher voucher;
            if (existing != null) {
                voucher = existing;
                voucher.setDiscountPercent(percent);
                voucher.setMinOrderAmount(minOrder);
                voucher.setTotalQuantity(quantity);
                voucher.setFreeShip(checkFreeShip.isChecked());
                voucher.setFreeShipAmount(freeShipAmount);
                voucher.setExpiryDate(expiryDate);
                voucher.setDescription(description);
            } else {
                voucher = new Voucher(code, percent, minOrder, quantity,
                        checkFreeShip.isChecked(), freeShipAmount, expiryDate, description);
            }
            
            if (existing != null) {
                voucherRepo.updateVoucher(voucher, new VoucherRepo.OperationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ManageVouchersActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadVouchers();
                    }
                    
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(ManageVouchersActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                voucherRepo.addVoucher(voucher, new VoucherRepo.OperationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ManageVouchersActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadVouchers();
                    }
                    
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(ManageVouchersActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onEdit(Voucher voucher, int position) {
        showAddVoucherDialog(voucher, position);
    }
    
    @Override
    public void onDelete(Voucher voucher, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa Voucher")
            .setMessage("Bạn có chắc muốn xóa voucher " + voucher.getCode() + "?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                voucherRepo.deleteVoucher(voucher.getId(), new VoucherRepo.OperationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ManageVouchersActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadVouchers();
                    }
                    
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(ManageVouchersActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    @Override
    public void onToggleActive(Voucher voucher, int position, boolean isActive) {
        voucherRepo.toggleVoucherStatus(voucher.getId(), isActive, new VoucherRepo.OperationCallback() {
            @Override
            public void onSuccess(String message) {
                voucher.setActive(isActive);
                updateStatistics();
            }
            
            @Override
            public void onFailure(String error) {
                Toast.makeText(ManageVouchersActivity.this, error, Toast.LENGTH_SHORT).show();
                loadVouchers();
            }
        });
    }
}
