package com.example.bt1.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.CartAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemClickListener {

    private ImageView btnBack;
    private CheckBox checkboxSelectAll;
    private Button btnDeleteSelected;
    private TextView textCartCount, textSubtotal, textShipping, textTotal;
    private RecyclerView recyclerViewCart;
    private Button btnCheckout;
    private android.widget.LinearLayout layoutEmptyCart;
    
    private CartAdapter cartAdapter;
    private List<Product> cartProducts;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        // Initialize SharedPreferences and Gson
        // Lấy userId từ SharedPreferencesManager
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        String userId = prefManager.getUserId();
        
        // Sử dụng key riêng cho mỗi user
        String cartKey = userId != null ? "cart_" + userId : "cart_guest";
        sharedPreferences = getSharedPreferences(cartKey, MODE_PRIVATE);
        gson = new Gson();

        cartProducts = new ArrayList<>();
        
        initViews();
        setupRecyclerView();
        loadCartProducts();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        btnDeleteSelected = findViewById(R.id.btn_delete_selected);
        textCartCount = findViewById(R.id.text_cart_count);
        textSubtotal = findViewById(R.id.text_subtotal);
        textShipping = findViewById(R.id.text_shipping);
        textTotal = findViewById(R.id.text_total);
        recyclerViewCart = findViewById(R.id.recycler_view_cart);
        btnCheckout = findViewById(R.id.btn_checkout);
        layoutEmptyCart = findViewById(R.id.layout_empty_cart);
        
        // Nút tiếp tục mua sắm trong empty state
        com.google.android.material.button.MaterialButton btnContinueShopping = 
            findViewById(R.id.btn_continue_shopping);
        if (btnContinueShopping != null) {
            btnContinueShopping.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this, cartProducts, this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCartProducts() {
        cartProducts.clear();
        
        String json = sharedPreferences.getString("cart_products", "[]");
        Type type = new TypeToken<List<Product>>(){}.getType();
        List<Product> cart = gson.fromJson(json, type);
        
        if (cart != null && !cart.isEmpty()) {
            // Gộp các sản phẩm trùng nhau (dựa trên tên)
            List<Product> mergedCart = new ArrayList<>();
            for (Product product : cart) {
                boolean found = false;
                for (Product merged : mergedCart) {
                    if (merged.getName().equals(product.getName())) {
                        // Nếu trùng, cộng dồn quantity
                        merged.setQuantity(merged.getQuantity() + product.getQuantity());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // Nếu chưa có, thêm mới
                    mergedCart.add(product);
                }
            }
            
            cartProducts.addAll(mergedCart);
            
            // Lưu lại giỏ hàng đã gộp
            saveCartProducts();
        }

        updateUI();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
        
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartAdapter != null) {
                cartAdapter.selectAll(isChecked);
            }
        });
        
        btnDeleteSelected.setOnClickListener(v -> {
            List<Product> selected = cartAdapter.getSelectedProducts();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
            } else {
                cartAdapter.removeSelectedItems();
                saveCartProducts();
                updateUI();
                checkboxSelectAll.setChecked(false);
                Toast.makeText(this, "Đã xóa " + selected.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCheckout.setOnClickListener(v -> {
            // Kiểm tra đăng nhập trước khi thanh toán
            com.example.bt1.utils.SharedPreferencesManager prefManager = 
                com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
            
            if (!prefManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            
            List<Product> selectedProducts = cartAdapter != null ? cartAdapter.getSelectedProducts() : new ArrayList<>();
            
            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            } else {
                android.content.Intent intent = new android.content.Intent(this, PaymentActivity.class);
                
                // Chỉ tính tiền cho các sản phẩm được tick chọn (sử dụng giá sau giảm)
                double subtotal = 0;
                for (Product product : selectedProducts) {
                    double finalPrice = (product.getOnDeal() != null && product.getOnDeal()) ? product.getDiscountedPrice() : product.getPrice();
                    subtotal += finalPrice * product.getQuantity();
                }
                double shipping = 30000;
                double total = subtotal + shipping;
                
                intent.putExtra("subtotal", subtotal);
                intent.putExtra("shipping", shipping);
                intent.putExtra("total", total);
                intent.putExtra("cart_size", selectedProducts.size());
                
                // Gửi danh sách sản phẩm đã chọn dưới dạng JSON
                String selectedProductsJson = gson.toJson(selectedProducts);
                intent.putExtra("selected_products", selectedProductsJson);
                
                startActivity(intent);
            }
        });
    }
    
    private void saveCartProducts() {
        String json = gson.toJson(cartProducts);
        sharedPreferences.edit().putString("cart_products", json).apply();
    }

    private void updateUI() {
        textCartCount.setText(cartProducts.size() + " sản phẩm");
        
        if (cartProducts.isEmpty()) {
            recyclerViewCart.setVisibility(android.view.View.GONE);
            layoutEmptyCart.setVisibility(android.view.View.VISIBLE);
        } else {
            recyclerViewCart.setVisibility(android.view.View.VISIBLE);
            layoutEmptyCart.setVisibility(android.view.View.GONE);
        }
        
        // Chỉ tính tiền cho các sản phẩm được tick chọn (sử dụng giá sau giảm)
        double subtotal = 0;
        List<Product> selectedProducts = cartAdapter != null ? cartAdapter.getSelectedProducts() : new ArrayList<>();
        for (Product product : selectedProducts) {
            double finalPrice = (product.getOnDeal() != null && product.getOnDeal()) ? product.getDiscountedPrice() : product.getPrice();
            subtotal += finalPrice * product.getQuantity();
        }
        
        double shipping = selectedProducts.isEmpty() ? 0 : 30000;
        double total = subtotal + shipping;
        
        textSubtotal.setText(String.format("%,.0f₫", subtotal));
        textShipping.setText(String.format("%,.0f₫", shipping));
        textTotal.setText(String.format("%,.0f₫", total));
        
        if (cartAdapter != null) {
            cartAdapter.updateData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartProducts();
    }

    @Override
    public void onRemoveFromCart(Product product) {
        cartProducts.remove(product);
        saveCartProducts();
        updateUI();
        checkboxSelectAll.setChecked(false);
        Toast.makeText(this, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuantityChanged(Product product, int newQuantity) {
        saveCartProducts();
        updateUI();
    }
    
    @Override
    public void onSelectionChanged() {
        if (cartAdapter != null) {
            checkboxSelectAll.setChecked(cartAdapter.areAllSelected());
        }
        updateUI();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Quay về trang chủ khi bấm nút back của hệ thống
        android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
