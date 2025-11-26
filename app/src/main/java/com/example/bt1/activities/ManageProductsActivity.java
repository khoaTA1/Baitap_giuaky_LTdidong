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
import com.example.bt1.adapters.ManageProductAdapter;
import com.example.bt1.models.Product;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import android.widget.Switch;
import android.widget.EditText;

public class ManageProductsActivity extends AppCompatActivity {

    private ImageView btnBack, btnAddProduct;
    private MaterialCardView btnFilter;
    private RecyclerView recyclerProducts;
    private LinearLayout emptyState;
    private TextView tvTotalProducts, tvInStock, tvOutOfStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        initViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnFilter = findViewById(R.id.btn_filter);
        recyclerProducts = findViewById(R.id.recycler_products);
        emptyState = findViewById(R.id.empty_state);
        tvTotalProducts = findViewById(R.id.tv_total_products);
        tvInStock = findViewById(R.id.tv_in_stock);
        tvOutOfStock = findViewById(R.id.tv_out_of_stock);

        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddProduct.setOnClickListener(v -> {
            showAddProductDialog();
        });
        
        btnFilter.setOnClickListener(v -> {
            // TODO: Hiển thị dialog filter
        });
    }

    private void loadData() {
        // Load products from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Product> allProducts = new ArrayList<>();
                int totalProducts = 0;
                int inStock = 0;
                int outOfStock = 0;
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Product product = document.toObject(Product.class);
                    product.setId(Long.parseLong(document.getId()));
                    
                    // Kiểm tra tồn kho
                    Integer stock = product.getStock();
                    if (stock != null && stock > 0) {
                        inStock++;
                    } else {
                        outOfStock++;
                    }
                    totalProducts++;
                    
                    allProducts.add(product);
                }
                
                // Cập nhật UI
                tvTotalProducts.setText(String.valueOf(totalProducts));
                tvInStock.setText(String.valueOf(inStock));
                tvOutOfStock.setText(String.valueOf(outOfStock));
                
                if (allProducts.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerProducts.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerProducts.setVisibility(View.VISIBLE);
                    
                    // Tạo và set adapter
                    ManageProductAdapter adapter = new ManageProductAdapter(this, allProducts, (product, view) -> {
                        showProductMenu(product);
                    });
                    recyclerProducts.setAdapter(adapter);
                }
                
                Log.d("ManageProducts", "Loaded " + totalProducts + " products");
            })
            .addOnFailureListener(e -> {
                Log.e("ManageProducts", "Error loading products", e);
                Toast.makeText(this, "Lỗi tải danh sách sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    private void showProductMenu(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_product_menu, null);
        builder.setView(dialogView);
        
        // Get views from dialog
        Switch switchOnDeal = dialogView.findViewById(R.id.switch_on_deal);
        EditText editDiscount = dialogView.findViewById(R.id.edit_discount);
        Switch switchIsActive = dialogView.findViewById(R.id.switch_is_active);
        
        // Set current values
        switchOnDeal.setChecked(product.getOnDeal() != null && product.getOnDeal());
        if (product.getDiscountPercent() != null) {
            editDiscount.setText(String.valueOf(product.getDiscountPercent()));
        }
        switchIsActive.setChecked(product.getIsActive());
        
        // Enable/disable discount field based on onDeal switch
        editDiscount.setEnabled(switchOnDeal.isChecked());
        switchOnDeal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editDiscount.setEnabled(isChecked);
            if (!isChecked) {
                editDiscount.setText("");
            }
        });
        
        builder.setTitle("Quản lý: " + product.getName());
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Update product in Firebase
            Map<String, Object> updates = new HashMap<>();
            updates.put("onDeal", switchOnDeal.isChecked());
            updates.put("isActive", switchIsActive.isChecked());
            
            if (switchOnDeal.isChecked() && !editDiscount.getText().toString().isEmpty()) {
                try {
                    int discount = Integer.parseInt(editDiscount.getText().toString());
                    if (discount >= 0 && discount <= 100) {
                        updates.put("dealPercentage", discount);
                    } else {
                        Toast.makeText(this, "Giảm giá phải từ 0-100%", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                updates.put("dealPercentage", 0);
            }
            
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                .document(String.valueOf(product.getId()))
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    loadData(); // Reload products
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    
    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        
        // Get views from dialog
        EditText editName = dialogView.findViewById(R.id.edit_product_name);
        EditText editBrand = dialogView.findViewById(R.id.edit_brand);
        EditText editCategory = dialogView.findViewById(R.id.edit_category);
        EditText editPrice = dialogView.findViewById(R.id.edit_price);
        EditText editStock = dialogView.findViewById(R.id.edit_stock);
        EditText editImageUrl = dialogView.findViewById(R.id.edit_image_url);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);
        EditText editDosageForm = dialogView.findViewById(R.id.edit_dosage_form);
        EditText editInclude = dialogView.findViewById(R.id.edit_include);
        EditText editOriginal = dialogView.findViewById(R.id.edit_original);
        
        builder.setTitle("Thêm sản phẩm mới");
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            // Validate required fields
            String name = editName.getText().toString().trim();
            String brand = editBrand.getText().toString().trim();
            String category = editCategory.getText().toString().trim();
            String priceStr = editPrice.getText().toString().trim();
            String stockStr = editStock.getText().toString().trim();
            
            if (name.isEmpty() || brand.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ các trường bắt buộc (*)", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);
                
                // Create product data map
                Map<String, Object> productData = new HashMap<>();
                productData.put("name", name);
                productData.put("brand", brand);
                productData.put("category", category);
                productData.put("price", price);
                productData.put("stock", stock);
                productData.put("imageUrl", editImageUrl.getText().toString().trim());
                productData.put("description", editDescription.getText().toString().trim());
                productData.put("dosageForm", editDosageForm.getText().toString().trim());
                productData.put("include", editInclude.getText().toString().trim());
                productData.put("original", editOriginal.getText().toString().trim());
                productData.put("onDeal", false);
                productData.put("dealPercentage", 0);
                productData.put("isActive", true);
                productData.put("rating", 0.0f);
                
                // Add to Firebase
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("products")
                    .add(productData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Đã thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        loadData(); // Reload products
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá hoặc tồn kho không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
