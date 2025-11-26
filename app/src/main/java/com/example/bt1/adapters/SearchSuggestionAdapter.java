package com.example.bt1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.utils.RenderImage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.ViewHolder> {

    private Context context;
    private List<Product> suggestions;
    private OnSuggestionClickListener listener;
    private RenderImage renderImage;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(Product product);
    }

    public SearchSuggestionAdapter(Context context, List<Product> suggestions, OnSuggestionClickListener listener) {
        this.context = context;
        this.suggestions = suggestions;
        this.listener = listener;
        this.renderImage = new RenderImage();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = suggestions.get(position);
        
        // Set product name
        holder.textProductName.setText(product.getName());
        
        // Set category
        if (product.getCategory() != null && !product.getCategory().isEmpty()) {
            holder.textCategory.setText(product.getCategory());
            holder.textCategory.setVisibility(View.VISIBLE);
        } else {
            holder.textCategory.setVisibility(View.GONE);
        }
        
        // Set price
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String priceText = formatter.format(product.getPrice()) + "₫";
        holder.textPrice.setText(priceText);
        
        // Set product image - Try Firebase image first, then local resource
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Load from Firebase using RenderImage
            renderImage.renderProductImage(context, product, holder.imageProduct);
        } else {
            // Fallback to local drawable resource
            int imageResId = product.getImageResId();
            if (imageResId != 0) {
                holder.imageProduct.setImageResource(imageResId);
            } else {
                holder.imageProduct.setImageResource(R.mipmap.avatar);
            }
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuggestionClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void updateSuggestions(List<Product> newSuggestions) {
        this.suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName;
        TextView textCategory;
        TextView textPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textCategory = itemView.findViewById(R.id.text_category);
            textPrice = itemView.findViewById(R.id.text_price);
        }
    }
}
