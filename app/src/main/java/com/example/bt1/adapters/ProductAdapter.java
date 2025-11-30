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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.activities.HomeActivity;
import com.example.bt1.activities.ProductDetailActivity;
import com.example.bt1.models.Product;
import com.example.bt1.utils.RenderImage;
import com.example.bt1.utils.SoldCountCache;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private OnProductClickListener listener;
    private RenderImage renderImage;
    private SoldCountCache soldCountCache;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.soldCountCache = new SoldCountCache(context);
        
        // Lấy userId từ SharedPreferencesManager
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(context);
        String userId = prefManager.getUserId();
        String favKey = userId != null ? "favorites_" + userId : "favorites_guest";
        
        this.sharedPreferences = context.getSharedPreferences(favKey, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.soldCountCache = new SoldCountCache(context);
        
        // Lấy userId từ SharedPreferencesManager
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(context);
        String userId = prefManager.getUserId();
        String favKey = userId != null ? "favorites_" + userId : "favorites_guest";
        
        this.sharedPreferences = context.getSharedPreferences(favKey, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_product.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Gán dữ liệu vào các view
        holder.productName.setText(product.getName());
        
        // Hiển thị giá: nếu có giảm giá thì hiển thị giá sau giảm, giá gốc gạch ngang
        if (product.getOnDeal() != null && product.getOnDeal() && product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
            // Hiển thị giá sau giảm
            double discountedPrice = product.getDiscountedPrice();
            holder.productPrice.setText(String.format("%,.0f₫", discountedPrice));
            
            // Hiển thị giá gốc gạch ngang
            holder.textOriginalPrice.setVisibility(View.VISIBLE);
            holder.textOriginalPrice.setText(String.format("%,.0f₫", product.getPrice()));
            holder.textOriginalPrice.setPaintFlags(holder.textOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            
            // Hiển thị % giảm giá
            holder.textDiscount.setVisibility(View.VISIBLE);
            holder.textDiscount.setText("-" + product.getDiscountPercent() + "%");
        } else {
            // Không có giảm giá, hiển thị giá gốc
            holder.productPrice.setText(String.format("%,.0f₫", product.getPrice()));
            holder.textOriginalPrice.setVisibility(View.GONE);
            holder.textDiscount.setVisibility(View.GONE);
        }
        
        //holder.productImage.setImageResource(product.getImageResId());
        renderImage = new RenderImage();
        renderImage.renderProductImage(context, product, holder.productImage);

        // Set favorite state
        boolean isFavorite = isProductFavorite(product);
        updateFavoriteIcon(holder.iconFavorite, isFavorite);

        // Set rating if available
        if (product.getRating() != null && product.getRating() > 0) {
            holder.textRating.setText(String.valueOf(product.getRating()));
        }
        
        // Set sold count từ cache (local) với null safety
        if (soldCountCache != null) {
            int soldCount = soldCountCache.getSoldCount(product.getId());
            if (soldCount > 0) {
                holder.textSoldCount.setText("Đã bán: " + soldCount);
            } else {
                holder.textSoldCount.setText("Đã bán: 0");
            }
        } else {
            holder.textSoldCount.setText("Đã bán: 0");
        }
        
        // ⭐ HIỂN THỊ BADGE "SẮP HẾT" NẾU TỒN KHO < 10
        Integer stock = product.getStock();
        if (stock != null && stock > 0 && stock < 10) {
            holder.textLowStock.setVisibility(View.VISIBLE);
        } else {
            holder.textLowStock.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click vào sản phẩm
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            } else {
                Toast.makeText(context, "Bạn đã chọn: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện click vào icon favorite
        holder.iconFavorite.setOnClickListener(v -> {
            boolean currentIsFavorite = isProductFavorite(product);
            if (currentIsFavorite) {
                removeFromFavorites(product);
                updateFavoriteIcon(holder.iconFavorite, false);
                Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                addToFavorites(product);
                updateFavoriteIcon(holder.iconFavorite, true);
                Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện click vào nút Add to Cart
        holder.btnAddToCart.setOnClickListener(v -> {
            addToCart(product);
            Toast.makeText(context, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update product list for search functionality
    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    // Favorite management methods
    private boolean isProductFavorite(Product product) {
        List<Long> favoriteIds = getFavoriteProductIds();
        return favoriteIds.contains(product.getId());
    }

    private void addToFavorites(Product product) {
        List<Long> favoriteIds = getFavoriteProductIds();
        if (!favoriteIds.contains(product.getId())) {
            favoriteIds.add(product.getId());
            saveFavoriteProductIds(favoriteIds);
        }
    }

    private void removeFromFavorites(Product product) {
        List<Long> favoriteIds = getFavoriteProductIds();
        favoriteIds.remove(product.getId());
        saveFavoriteProductIds(favoriteIds);
    }

    private List<Long> getFavoriteProductIds() {
        String json = sharedPreferences.getString("favorite_products", "[]");
        Type type = new TypeToken<List<Long>>(){}.getType();
        List<Long> favorites = null;
        try {
            favorites = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            // If parsing fails, assume it's the old format (List<Product>)
            Type oldType = new TypeToken<List<Product>>(){}.getType();
            try {
                List<Product> oldFavorites = gson.fromJson(json, oldType);
                if (oldFavorites != null) {
                    favorites = new ArrayList<>();
                    for (Product p : oldFavorites) {
                        favorites.add(p.getId());
                    }
                    // Save in the new format
                    saveFavoriteProductIds(favorites);
                }
            } catch (JsonSyntaxException ignored) {
                 // Could not parse old format either, just start fresh
            }
        }

        return favorites != null ? favorites : new ArrayList<>();
    }

    private void saveFavoriteProductIds(List<Long> favorites) {
        String json = gson.toJson(favorites);
        sharedPreferences.edit().putString("favorite_products", json).apply();
    }
    
    private void updateFavoriteIcon(ImageView iconFavorite, boolean isFavorite) {
        if (isFavorite) {
            iconFavorite.setImageResource(R.drawable.ic_favorite_filled);
            iconFavorite.setImageTintList(null); // Clear tint để hiển thị màu đỏ
        } else {
            iconFavorite.setImageResource(R.drawable.ic_favorite_border);
            iconFavorite.setImageTintList(null); // Clear tint
        }
    }

    // Cart management methods
    private void addToCart(Product product) {
        // Lấy userId từ SharedPreferencesManager
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(context);
        String userId = prefManager.getUserId();
        String cartKey = userId != null ? "cart_" + userId : "cart_guest";
        
        android.content.SharedPreferences cartPrefs = context.getSharedPreferences(cartKey, android.content.Context.MODE_PRIVATE);
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
            if (p.getId() == product.getId()) {
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

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, textOriginalPrice, textDiscount, textRating, textSoldCount, textLowStock;
        ImageView productImage, iconFavorite;
        MaterialButton btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.text_product_name);
            productPrice = itemView.findViewById(R.id.text_product_price);
            textOriginalPrice = itemView.findViewById(R.id.text_original_price);
            textDiscount = itemView.findViewById(R.id.text_discount);
            textRating = itemView.findViewById(R.id.text_rating);
            textSoldCount = itemView.findViewById(R.id.text_sold_count);
            textLowStock = itemView.findViewById(R.id.text_low_stock); // ⭐ LOW STOCK BADGE
            productImage = itemView.findViewById(R.id.image_product);
            iconFavorite = itemView.findViewById(R.id.icon_favorite);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
