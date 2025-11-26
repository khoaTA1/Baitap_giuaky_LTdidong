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
import com.example.bt1.models.Product;
import com.google.gson.Gson;
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

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
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
        holder.productPrice.setText(product.getPriceFormatted());
        holder.productImage.setImageResource(product.getImageResId());

        // Set favorite state
        boolean isFavorite = isProductFavorite(product);
        updateFavoriteIcon(holder.iconFavorite, isFavorite);

        // Set rating if available
        if (product.getRating() > 0) {
            holder.textRating.setText(String.valueOf(product.getRating()));
        }

        // Show/hide discount
        if (product.getDiscountPercent() > 0) {
            holder.textDiscount.setVisibility(View.VISIBLE);
            holder.textDiscount.setText("Giảm " + product.getDiscountPercent() + "%");
            
            if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
                holder.textOriginalPrice.setVisibility(View.VISIBLE);
                holder.textOriginalPrice.setText(product.getOriginalPrice());
                holder.textOriginalPrice.setPaintFlags(holder.textOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
        } else {
            holder.textDiscount.setVisibility(View.GONE);
            holder.textOriginalPrice.setVisibility(View.GONE);
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
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Favorite management methods
    private boolean isProductFavorite(Product product) {
        List<Product> favorites = getFavoriteProducts();
        for (Product fav : favorites) {
            if (fav.getName().equals(product.getName())) {
                return true;
            }
        }
        return false;
    }

    private void addToFavorites(Product product) {
        List<Product> favorites = getFavoriteProducts();
        
        // Kiểm tra nếu đã tồn tại
        for (Product fav : favorites) {
            if (fav.getName().equals(product.getName())) {
                return; // Đã tồn tại
            }
        }
        
        favorites.add(product);
        saveFavoriteProducts(favorites);
    }

    private void removeFromFavorites(Product product) {
        List<Product> favorites = getFavoriteProducts();
        favorites.removeIf(fav -> fav.getName().equals(product.getName()));
        saveFavoriteProducts(favorites);
    }

    private List<Product> getFavoriteProducts() {
        String json = sharedPreferences.getString("favorite_products", "[]");
        Type type = new TypeToken<List<Product>>(){}.getType();
        List<Product> favorites = gson.fromJson(json, type);
        return favorites != null ? favorites : new ArrayList<>();
    }

    private void saveFavoriteProducts(List<Product> favorites) {
        String json = gson.toJson(favorites);
        sharedPreferences.edit().putString("favorite_products", json).apply();
    }

    private void updateFavoriteIcon(ImageView iconFavorite, boolean isFavorite) {
        if (isFavorite) {
            iconFavorite.setImageResource(R.drawable.ic_favorite_border); // Sử dụng icon có sẵn tạm thời
            iconFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_light));
        } else {
            iconFavorite.setImageResource(R.drawable.ic_favorite_border);
            iconFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
    }

    // Lớp ViewHolder để giữ các view của một item
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView textOriginalPrice;
        TextView textDiscount;
        TextView textRating;
        ImageView iconFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.image_product);
            productName = itemView.findViewById(R.id.text_product_name);
            productPrice = itemView.findViewById(R.id.text_product_price);
            textOriginalPrice = itemView.findViewById(R.id.text_original_price);
            textDiscount = itemView.findViewById(R.id.text_discount);
            textRating = itemView.findViewById(R.id.text_rating);
            iconFavorite = itemView.findViewById(R.id.icon_favorite);
        }
    }
}
