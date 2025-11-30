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
import com.example.bt1.repositories.ProductRepo;
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
                    
                    // Lưu Firestore document ID gốc
                    String docId = document.getId();
                    product.setDocumentId(docId);
                    
                    // Xử lý ID: có thể là số hoặc string auto-generated
                    try {
                        product.setId(Long.parseLong(docId));
                    } catch (NumberFormatException e) {
                        // Nếu document ID là string, dùng hashCode làm ID số
                        product.setId((long) docId.hashCode());
                    }
                    
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
                
                // ⭐ KIỂM TRA SẢN PHẨM SẮP HẾT HÀNG
                checkLowStockProducts(allProducts);
                
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
    
    /**
     * ⭐ CẢNH BÁO SẢN PHẨM SẮP HẾT HÀNG (LOW STOCK)
     */
    private void checkLowStockProducts(List<Product> products) {
        List<Product> lowStockProducts = new ArrayList<>();
        final int LOW_STOCK_THRESHOLD = 10; // Ngưỡng cảnh báo: dưới 10 sản phẩm
        
        for (Product p : products) {
            Integer stock = p.getStock();
            if (stock != null && stock > 0 && stock < LOW_STOCK_THRESHOLD) {
                lowStockProducts.add(p);
            }
        }
        
        if (!lowStockProducts.isEmpty()) {
            StringBuilder message = new StringBuilder("⚠️ Cảnh báo tồn kho thấp:\n\n");
            for (Product p : lowStockProducts) {
                message.append("• ").append(p.getName())
                       .append(": ").append(p.getStock()).append(" sản phẩm\n");
            }
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("🔔 Sản phẩm sắp hết hàng")
                .setMessage(message.toString())
                .setPositiveButton("Đã hiểu", null)
                .setNegativeButton("Xem chi tiết", (dialog, which) -> {
                    // Scroll to first low stock product
                    if (!lowStockProducts.isEmpty()) {
                        recyclerProducts.smoothScrollToPosition(0);
                    }
                })
                .show();
            
            Log.w("ManageProducts", "Found " + lowStockProducts.size() + " low stock products");
        }
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
        EditText editIngredient = dialogView.findViewById(R.id.edit_ingredient);
        EditText editUse = dialogView.findViewById(R.id.edit_use);
        EditText editSideEffects = dialogView.findViewById(R.id.edit_side_effects);
        EditText editObject = dialogView.findViewById(R.id.edit_object);
        
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
                
                // Create Product object
                Product newProduct = new Product();
                newProduct.setName(name);
                newProduct.setBrand(brand);
                newProduct.setCategory(category);
                newProduct.setPrice(price);
                newProduct.setStock(stock);
                newProduct.setImageUrl(editImageUrl.getText().toString().trim());
                newProduct.setDescription(editDescription.getText().toString().trim());
                newProduct.setDosageForm(editDosageForm.getText().toString().trim());
                newProduct.setInclude(editInclude.getText().toString().trim());
                newProduct.setOriginal(editOriginal.getText().toString().trim());
                newProduct.setIngredient(editIngredient.getText().toString().trim());
                newProduct.setUse(editUse.getText().toString().trim());
                newProduct.setSideEffects(editSideEffects.getText().toString().trim());
                newProduct.setObject(editObject.getText().toString().trim());
                newProduct.setOnDeal(false);
                newProduct.setDealPercentage(0);
                newProduct.setIsActive(true);
                newProduct.setRating(0.0f);
                newProduct.setSoldCount(0);
                
                // Add to Firebase using ProductRepo (document ID will be sequential number)
                ProductRepo productRepo = new ProductRepo();
                productRepo.addProduct(newProduct);
                
                Toast.makeText(this, "Đã thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                
                // Reload products after a short delay to allow Firebase to update
                new android.os.Handler().postDelayed(() -> loadData(), 500);
                    
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá hoặc tồn kho không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
