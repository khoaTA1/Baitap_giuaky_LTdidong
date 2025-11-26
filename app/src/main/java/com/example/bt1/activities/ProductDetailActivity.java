package com.example.bt1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.google.android.material.button.MaterialButton;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textProductName;
    private TextView textProductPrice;
    private TextView textProductDescription;
    private TextView textSpecifications;
    private ImageButton btnFavorite;
    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        
        // Khởi tạo views
        initViews();
        
        // Thiết lập toolbar
        setupToolbar();
        
        // Lấy thông tin sản phẩm từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");
        
        if (product != null) {
            // Hiển thị thông tin sản phẩm
            displayProductInfo(product);
            
            // Thiết lập các sự kiện click
            setupClickListeners(product);
            
            Log.d("ProductDetailActivity", "Đã nhận sản phẩm: " + product.getName());
        } else {
            Toast.makeText(this, "❌ Lỗi: Không thể lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có dữ liệu
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imageProduct = findViewById(R.id.image_product);
        textProductName = findViewById(R.id.text_product_name);
        textProductPrice = findViewById(R.id.text_product_price);
        textProductDescription = findViewById(R.id.text_product_description);
        textSpecifications = findViewById(R.id.text_specifications);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
    }
    
    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }
    
    private void displayProductInfo(Product product) {
        // Hiển thị tên sản phẩm
        if (textProductName != null) {
            textProductName.setText(product.getName());
        }
        
        // Hiển thị giá sản phẩm - sử dụng getPriceString() thay vì getPrice()
        if (textProductPrice != null) {
            if (product.getPriceString() != null) {
                textProductPrice.setText(product.getPriceString());
            } else {
                textProductPrice.setText(product.getPriceFormatted());
            }
        }
        
        // Hiển thị hình ảnh (sử dụng resource ID từ product)
        if (imageProduct != null && product.getImageResId() != 0) {
            imageProduct.setImageResource(product.getImageResId());
        }
        
        // Hiển thị mô tả 
        if (textProductDescription != null) {
            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                textProductDescription.setText(product.getDescription());
            } else {
                textProductDescription.setText("Sản phẩm chính hãng với chất lượng tốt nhất. Bảo hành 12 tháng.");
            }
        }
        
        // Hiển thị thông số kỹ thuật - sử dụng getSpecifications()
        if (textSpecifications != null) {
            if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
                textSpecifications.setText(product.getSpecifications());
            } else {
                // Tạo thông số mặc định
                StringBuilder specs = new StringBuilder();
                specs.append("• Thương hiệu: ").append(product.getBrand() != null ? product.getBrand() : "N/A").append("\n");
                specs.append("• Tình trạng: Mới 100%").append("\n");
                specs.append("• Bảo hành: 12 tháng chính hãng");
                textSpecifications.setText(specs.toString());
            }
        }
    }
    
    private void setupClickListeners(Product product) {
        // Nút yêu thích
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                Toast.makeText(this, "Đã thêm vào yêu thích: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
        }
        
        // Nút thêm vào giỏ
        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> {
                Toast.makeText(this, "Đã thêm vào giỏ hàng: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
        }
        
        // Nút mua ngay
        if (btnBuyNow != null) {
            btnBuyNow.setOnClickListener(v -> {
                Toast.makeText(this, "Chuyển đến trang thanh toán cho: " + product.getName(), Toast.LENGTH_SHORT).show();
                // TODO: Chuyển đến PaymentActivity
            });
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}