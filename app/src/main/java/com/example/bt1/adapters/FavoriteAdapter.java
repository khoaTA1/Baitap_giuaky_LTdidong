package com.example.bt1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<Product> favoriteProducts;
    private OnFavoriteItemClickListener listener;
    private boolean isSelectionMode = false;
    private List<Product> selectedProducts = new ArrayList<>();

    public interface OnFavoriteItemClickListener {
        void onRemoveFromFavorites(Product product);
        void onAddToCart(Product product);
        void onProductClick(Product product);
        void onLongPress(Product product);
    }

    public FavoriteAdapter(Context context, List<Product> favoriteProducts, OnFavoriteItemClickListener listener) {
        this.context = context;
        this.favoriteProducts = favoriteProducts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_product, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = favoriteProducts.get(position);
        holder.bind(product, position);
    }

    @Override
    public int getItemCount() {
        return favoriteProducts.size();
    }

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }

    public void selectAll(boolean selectAll) {
        selectedProducts.clear();
        if (selectAll) {
            selectedProducts.addAll(favoriteProducts);
        }
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selectedProducts.clear();
        notifyDataSetChanged();
    }

    public List<Product> getSelectedProducts() {
        return new ArrayList<>(selectedProducts);
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxSelect;
        private ImageView imageProduct;
        private TextView textProductName, textProductPrice, textOriginalPrice, textStockStatus;
        private ImageView btnRemoveFavorite;
        private Button btnAddToCart;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            
            checkboxSelect = itemView.findViewById(R.id.checkbox_select);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textOriginalPrice = itemView.findViewById(R.id.text_original_price);
            textStockStatus = itemView.findViewById(R.id.text_stock_status);
            btnRemoveFavorite = itemView.findViewById(R.id.btn_remove_favorite);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }

        public void bind(Product product, int position) {
            // Set product data
            textProductName.setText(product.getName());
            textProductPrice.setText(product.getPriceString());

            // Set image
            imageProduct.setImageResource(product.getImageResId());
            
            // Set stock status
            textStockStatus.setText("Còn hàng");
            
            // Handle selection mode
            if (isSelectionMode) {
                checkboxSelect.setVisibility(View.VISIBLE);
                checkboxSelect.setChecked(selectedProducts.contains(product));
                btnRemoveFavorite.setVisibility(View.GONE);
                btnAddToCart.setVisibility(View.GONE);
            } else {
                checkboxSelect.setVisibility(View.GONE);
                btnRemoveFavorite.setVisibility(View.VISIBLE);
                btnAddToCart.setVisibility(View.VISIBLE);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (isSelectionMode) {
                    toggleSelection(product);
                } else {
                    listener.onProductClick(product);
                }
            });

            itemView.setOnLongClickListener(v -> {
                listener.onLongPress(product);
                return true;
            });

            checkboxSelect.setOnClickListener(v -> toggleSelection(product));

            btnRemoveFavorite.setOnClickListener(v -> listener.onRemoveFromFavorites(product));

            btnAddToCart.setOnClickListener(v -> listener.onAddToCart(product));
        }

        private void toggleSelection(Product product) {
            if (selectedProducts.contains(product)) {
                selectedProducts.remove(product);
            } else {
                selectedProducts.add(product);
            }
            checkboxSelect.setChecked(selectedProducts.contains(product));
        }
    }
}