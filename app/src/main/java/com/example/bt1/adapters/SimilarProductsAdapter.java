package com.example.bt1.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.utils.RenderImage;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimilarProductsAdapter extends RecyclerView.Adapter<SimilarProductsAdapter.SimilarProductViewHolder> {
    
    private Context context;
    private List<Product> products;
    private OnProductClickListener onProductClickListener;
    
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public SimilarProductsAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.onProductClickListener = listener;
    }
    
    @NonNull
    @Override
    public SimilarProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_similar_product_card, parent, false);
        return new SimilarProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SimilarProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }
    
    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }
    
    class SimilarProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName;
        TextView textProductPrice;
        TextView textProductInfo;
        MaterialButton btnAddToCart;
        
        SimilarProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textProductInfo = itemView.findViewById(R.id.text_product_info);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
        
        void bind(Product product) {
            // Set product name
            textProductName.setText(product.getName());
            
            // Set product price
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String priceText = formatter.format(product.getPrice()) + "đ / Hộp";
            textProductPrice.setText(priceText);
            
            // Set product info (include/quy cách)
            if (product.getInclude() != null && !product.getInclude().isEmpty()) {
                textProductInfo.setText(product.getInclude());
                textProductInfo.setVisibility(View.VISIBLE);
            } else {
                textProductInfo.setVisibility(View.GONE);
            }
            
            // Load product image
            RenderImage renderImage = new RenderImage();
            renderImage.renderProductImage(context, product, imageProduct);
            
            // Click listener for the whole card
            itemView.setOnClickListener(v -> {
                if (onProductClickListener != null) {
                    onProductClickListener.onProductClick(product);
                }
            });
            
            // Click listener for add to cart button
            btnAddToCart.setOnClickListener(v -> {
                addToCart(product);
                Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void addToCart(Product product) {
        // Lấy userId từ SharedPreferencesManager
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(context);
        String userId = prefManager.getUserId();
        String cartKey = userId != null ? "cart_" + userId : "cart_guest";
        
        SharedPreferences cartPrefs = context.getSharedPreferences(cartKey, Context.MODE_PRIVATE);
        String json = cartPrefs.getString("cart_products", "[]");
        
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>(){}.getType();
        List<Product> cartProducts = gson.fromJson(json, type);
        
        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }
        
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        boolean found = false;
        for (Product p : cartProducts) {
            if (p.getId().equals(product.getId())) {
                // Nếu đã có, tăng số lượng
                int currentQty = p.getQuantity();
                if (currentQty == 0) currentQty = 1;
                p.setQuantity(currentQty + 1);
                found = true;
                break;
            }
        }
        
        // Nếu chưa có trong giỏ, thêm mới với số lượng = 1
        if (!found) {
            product.setQuantity(1);
            cartProducts.add(product);
        }
        
        String newJson = gson.toJson(cartProducts);
        cartPrefs.edit().putString("cart_products", newJson).apply();
    }
}
