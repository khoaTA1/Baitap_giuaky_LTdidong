package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.DeliveryAddressAdapter;
import com.example.bt1.models.DeliveryAddress;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveryAddressesActivity extends AppCompatActivity implements DeliveryAddressAdapter.OnAddressActionListener {

    private RecyclerView recyclerAddresses;
    private LinearLayout emptyState;
    private MaterialButton btnAddAddress;
    private ImageView btnBack;

    private DeliveryAddressAdapter adapter;
    private List<DeliveryAddress> addressList;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_addresses);

        db = FirebaseFirestore.getInstance();
        userId = SharedPreferencesManager.getInstance(this).getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadAddresses();
    }

    private void initViews() {
        recyclerAddresses = findViewById(R.id.recycler_addresses);
        emptyState = findViewById(R.id.empty_state);
        btnAddAddress = findViewById(R.id.btn_add_address);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupRecyclerView() {
        addressList = new ArrayList<>();
        adapter = new DeliveryAddressAdapter(this, addressList, this);
        recyclerAddresses.setLayoutManager(new LinearLayoutManager(this));
        recyclerAddresses.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditAddressActivity.class);
            startActivity(intent);
        });
    }

    private void loadAddresses() {
        android.util.Log.d("DeliveryAddresses", "Loading addresses for userId: " + userId);
        
        db.collection("delivery_addresses")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    android.util.Log.d("DeliveryAddresses", "Query successful, documents: " + queryDocumentSnapshots.size());
                    
                    addressList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DeliveryAddress address = document.toObject(DeliveryAddress.class);
                        address.setId(document.getId());
                        
                        android.util.Log.d("DeliveryAddresses", "Address loaded - ID: " + address.getId() + 
                              ", Name: " + address.getRecipientName() + 
                              ", Phone: " + address.getPhoneNumber() +
                              ", Label: " + address.getLabel());
                        
                        addressList.add(address);
                    }

                    if (addressList.isEmpty()) {
                        android.util.Log.d("DeliveryAddresses", "No addresses found, showing empty state");
                        recyclerAddresses.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        android.util.Log.d("DeliveryAddresses", "Found " + addressList.size() + " addresses");
                        recyclerAddresses.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                    }

                    adapter.updateData(addressList);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("DeliveryAddresses", "Error loading addresses", e);
                    Toast.makeText(this, "Lỗi tải địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    @Override
    public void onEditClick(DeliveryAddress address) {
        Intent intent = new Intent(this, AddEditAddressActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("is_edit", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(DeliveryAddress address) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteAddress(address);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onAddressClick(DeliveryAddress address) {
        // Can be used for selection mode in checkout
    }

    private void deleteAddress(DeliveryAddress address) {
        if (address.getId() == null) {
            Toast.makeText(this, "Không thể xóa địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("delivery_addresses")
                .document(address.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi xóa địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
