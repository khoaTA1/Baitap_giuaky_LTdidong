package com.example.bt1.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.ManageUserAdapter;
import com.example.bt1.models.User;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private ImageView btnBack, btnSearch;
    private RecyclerView recyclerUsers;
    private LinearLayout emptyState;
    private TextView tvTotalUsers, tvTotalAdmins;
    private ChipGroup chipGroupFilter;
    
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private ManageUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        initViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        recyclerUsers = findViewById(R.id.recycler_users);
        emptyState = findViewById(R.id.empty_state);
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalAdmins = findViewById(R.id.tv_total_admins);
        chipGroupFilter = findViewById(R.id.chip_group_filter);

        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnSearch.setOnClickListener(v -> {
            // TODO: Mở search dialog hoặc activity
        });
        
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                filterUsers(checkedId);
            }
        });
    }

    private void loadData() {
        // Load users from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allUsers.clear();
                int totalUsers = 0;
                int totalAdmins = 0;
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    User user = new User();
                    user.setId(Integer.parseInt(document.getId()));
                    user.setFullName(document.getString("fullname"));
                    user.setEmail(document.getString("email"));
                    user.setAddress(document.getString("address"));
                    user.setPhone(document.getString("phone"));
                    
                    // Load role
                    String role = document.getString("role");
                    user.setRole(role != null ? role : "user");
                    
                    // Load isActive status
                    Boolean isActive = document.getBoolean("is_active");
                    user.setIsActive(isActive != null ? isActive : true);
                    
                    // Count by role
                    if ("admin".equals(user.getRole())) {
                        totalAdmins++;
                    }
                    totalUsers++;
                    
                    allUsers.add(user);
                }
                
                // Update UI
                tvTotalUsers.setText(String.valueOf(totalUsers));
                tvTotalAdmins.setText(String.valueOf(totalAdmins));
                
                // Show all users by default
                filterUsers(R.id.chip_all);
                
                Log.d("ManageUsers", "Loaded " + totalUsers + " users, " + totalAdmins + " admins");
            })
            .addOnFailureListener(e -> {
                Log.e("ManageUsers", "Error loading users", e);
                Toast.makeText(this, "Lỗi tải danh sách người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void filterUsers(int chipId) {
        filteredUsers.clear();
        
        if (chipId == R.id.chip_all) {
            // Show all users
            filteredUsers.addAll(allUsers);
        } else if (chipId == R.id.chip_users) {
            // Show only regular users (non-admin)
            for (User user : allUsers) {
                if (!"admin".equals(user.getRole())) {
                    filteredUsers.add(user);
                }
            }
        } else if (chipId == R.id.chip_admins) {
            // Show only admins
            for (User user : allUsers) {
                if ("admin".equals(user.getRole())) {
                    filteredUsers.add(user);
                }
            }
        } else if (chipId == R.id.chip_active) {
            // Show only active users
            for (User user : allUsers) {
                if (user.getIsActive() != null && user.getIsActive()) {
                    filteredUsers.add(user);
                }
            }
        } else if (chipId == R.id.chip_blocked) {
            // Show only blocked users
            for (User user : allUsers) {
                if (user.getIsActive() != null && !user.getIsActive()) {
                    filteredUsers.add(user);
                }
            }
        }
        
        // Update UI
        if (filteredUsers.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerUsers.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerUsers.setVisibility(View.VISIBLE);
            
            if (adapter == null) {
                adapter = new ManageUserAdapter(this, filteredUsers, (user, view) -> {
                    // TODO: Hiển thị menu actions (edit, delete, block, etc.)
                    Toast.makeText(this, "Menu for: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                });
                recyclerUsers.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
        
        Log.d("ManageUsers", "Filtered: " + filteredUsers.size() + " users");
    }
}
