package com.example.bt1.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.utils.RenderImage;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartProducts;
    private List<Boolean> selectedItems;
    private OnCartItemClickListener listener;
    private RenderImage renderImage;

    public interface OnCartItemClickListener {
        void onRemoveFromCart(Product product);
        void onQuantityChanged(Product product, int newQuantity);
        void onSelectionChanged();
    }

    public CartAdapter(Context context, List<Product> cartProducts, OnCartItemClickListener listener) {
        this.context = context;
        this.cartProducts = cartProducts;
        this.listener = listener;
        this.selectedItems = new ArrayList<>();
        for (int i = 0; i < cartProducts.size(); i++) {
            selectedItems.add(false);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Sync selectedItems size with cartProducts
        while (selectedItems.size() < cartProducts.size()) {
            selectedItems.add(false);
        }
        while (selectedItems.size() > cartProducts.size()) {
            selectedItems.remove(selectedItems.size() - 1);
        }

        Product product = cartProducts.get(position);
        boolean isSelected = position < selectedItems.size() ? selectedItems.get(position) : false;
        holder.bind(product, isSelected, position);
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    public void updateData() {
        // Sync selectedItems size with cartProducts
        while (selectedItems.size() < cartProducts.size()) {
            selectedItems.add(false);
        }
        while (selectedItems.size() > cartProducts.size()) {
            selectedItems.remove(selectedItems.size() - 1);
        }
        notifyDataSetChanged();
    }

    public List<Product> getSelectedProducts() {
        List<Product> selected = new ArrayList<>();
        for (int i = 0; i < Math.min(cartProducts.size(), selectedItems.size()); i++) {
            if (selectedItems.get(i)) {
                selected.add(cartProducts.get(i));
            }
        }
        return selected;
    }

    public void selectAll(boolean select) {
        selectedItems.clear();
        for (int i = 0; i < cartProducts.size(); i++) {
            selectedItems.add(select);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged();
        }
    }

    public boolean areAllSelected() {
        if (selectedItems.isEmpty() || cartProducts.isEmpty()) return false;
        for (int i = 0; i < Math.min(selectedItems.size(), cartProducts.size()); i++) {
            if (!selectedItems.get(i)) return false;
        }
        return true;
    }

    public void removeSelectedItems() {
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            if (i < cartProducts.size() && selectedItems.get(i)) {
                cartProducts.remove(i);
                selectedItems.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxSelect;
        private ImageView imageProduct, btnRemove;
        private TextView textProductName, textProductPrice, textOriginalPrice, textQuantity;
        private Button btnDecrease, btnIncrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxSelect = itemView.findViewById(R.id.checkbox_select);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textOriginalPrice = itemView.findViewById(R.id.text_original_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(Product product, boolean isSelected, int position) {
            textProductName.setText(product.getName());
            
            // Update price display based on quantity
            updatePriceDisplay(product);

            checkboxSelect.setChecked(isSelected);

            // Load quantity from product
            textQuantity.setText(String.valueOf(product.getQuantity()));

            // Set image with validation
            try {
                renderImage = new RenderImage();
                renderImage.renderProductImage(context, product, imageProduct);
            } catch (Exception e) {
                imageProduct.setImageResource(R.drawable.leanaocavill);
            }

            // Checkbox listener
            checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selectedItems.set(position, isChecked);
                if (listener != null) {
                    listener.onSelectionChanged();
                }
            });

            // Remove button
            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveFromCart(product);
                }
            });

            // Quantity buttons
            btnDecrease.setOnClickListener(v -> {
                int currentQty = Integer.parseInt(textQuantity.getText().toString());
                if (currentQty > 1) {
                    currentQty--;
                    textQuantity.setText(String.valueOf(currentQty));
                    product.setQuantity(currentQty);
                    updatePriceDisplay(product);
                    if (listener != null) {
                        listener.onQuantityChanged(product, currentQty);
                    }
                }
            });

            btnIncrease.setOnClickListener(v -> {
                int currentQty = Integer.parseInt(textQuantity.getText().toString());
                currentQty++;
                textQuantity.setText(String.valueOf(currentQty));
                product.setQuantity(currentQty);
                updatePriceDisplay(product);
                if (listener != null) {
                    listener.onQuantityChanged(product, currentQty);
                }
            });
        }
        
        private void updatePriceDisplay(Product product) {
            int quantity = product.getQuantity();
            double unitPrice = (product.getOnDeal() != null && product.getOnDeal()) 
                ? product.getDiscountedPrice() 
                : product.getPrice();
            double totalPrice = unitPrice * quantity;
            
            if (product.getOnDeal() != null && product.getOnDeal()) {
                textProductPrice.setText(String.format("%,.0f₫", totalPrice));
                double originalTotal = product.getPrice() * quantity;
                textOriginalPrice.setText(String.format("%,.0f₫", originalTotal));
                textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textOriginalPrice.setVisibility(View.VISIBLE);
            } else {
                textProductPrice.setText(String.format("%,.0f₫", totalPrice));
                textOriginalPrice.setVisibility(View.GONE);
            }
        }
    }
}
