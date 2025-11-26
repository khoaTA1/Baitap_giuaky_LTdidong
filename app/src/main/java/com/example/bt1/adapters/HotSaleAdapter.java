package com.example.bt1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.utils.RenderImage;

import java.util.List;
import java.util.Random;

public class HotSaleAdapter extends RecyclerView.Adapter<HotSaleAdapter.HotSaleViewHolder> {

    private Context context;
    private List<Product> hotSaleProducts;
    private OnHotSaleItemClickListener listener;
    private RenderImage renderImage;

    public interface OnHotSaleItemClickListener {
        void onProductClick(Product product);
        void onAddToCart(Product product);
        void onAddToFavorites(Product product);
    }

    public HotSaleAdapter(Context context, List<Product> hotSaleProducts, OnHotSaleItemClickListener listener) {
        this.context = context;
        this.hotSaleProducts = hotSaleProducts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hot_sale_product, parent, false);
        return new HotSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotSaleViewHolder holder, int position) {
        Product product = hotSaleProducts.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return hotSaleProducts.size();
    }

    class HotSaleViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProduct;
        private TextView textDiscountBadge;
        private ImageButton btnFavorite;
        private TextView textProductName;
        private RatingBar ratingBar;
        private TextView textRating;
        private TextView textSalePrice, textOriginalPrice;
        private Button btnAddToCart;

        public HotSaleViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageProduct = itemView.findViewById(R.id.image_product);
            textDiscountBadge = itemView.findViewById(R.id.text_discount_badge);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            textProductName = itemView.findViewById(R.id.text_product_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            textRating = itemView.findViewById(R.id.text_rating);
            textSalePrice = itemView.findViewById(R.id.text_sale_price);
            textOriginalPrice = itemView.findViewById(R.id.text_original_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }

        public void bind(Product product) {
            // Set product data
            textProductName.setText(product.getName());
            
            // Tính và hiển thị giá
            if (product.getOnDeal() != null && product.getOnDeal() && product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
                // Hiển thị giá sau giảm
                double discountedPrice = product.getDiscountedPrice();
                textSalePrice.setText(String.format("%,.0f₫", discountedPrice));
                
                // Hiển thị giá gốc gạch ngang
                textOriginalPrice.setText(String.format("%,.0f₫", product.getPrice()));
                textOriginalPrice.setVisibility(View.VISIBLE);
                textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                
                // Hiển thị % giảm giá
                textDiscountBadge.setText("-" + product.getDiscountPercent() + "%");
            } else {
                // Không có giảm giá
                textSalePrice.setText(String.format("%,.0f₫", product.getPrice()));
                textOriginalPrice.setVisibility(View.GONE);
                if (product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
                    textDiscountBadge.setText("-" + product.getDiscountPercent() + "%");
                }
            }
            
            // Set product image with validation
            try {
                renderImage = new RenderImage();
                renderImage.renderProductImage(context, product, imageProduct);
            } catch (Exception e) {
                imageProduct.setImageResource(R.drawable.leanaocavill);
            }
            
            // Set rating
            if (product.getRating() != null) {
                ratingBar.setRating(product.getRating());
                textRating.setText(String.valueOf(product.getRating()));
            }

            
            // Set random stock progress (simulation)
            Random random = new Random();
            int progress = random.nextInt(80) + 20; // 20-100%

            
            // Set click listeners
            itemView.setOnClickListener(v -> listener.onProductClick(product));
            
            btnFavorite.setOnClickListener(v -> {
                listener.onAddToFavorites(product);
                // Toggle favorite icon
                if (btnFavorite.getTag() == null || !((Boolean) btnFavorite.getTag())) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
                    btnFavorite.setTag(true);
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                    btnFavorite.setTag(false);
                }
            });
            
            btnAddToCart.setOnClickListener(v -> listener.onAddToCart(product));
        }
    }
}