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

import java.util.List;

public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductActionListener listener;
    private RenderImage renderImage;

    public interface OnProductActionListener {
        void onProductMenuClick(Product product, View view);
    }

    public ManageProductAdapter(Context context, List<Product> productList, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.renderImage = new RenderImage();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Product name
        holder.tvProductName.setText(product.getName() != null ? product.getName() : "N/A");

        // Category
        holder.tvProductCategory.setText(product.getCategory() != null ? product.getCategory() : "Chưa phân loại");

        // Price
        if (product.getOnDeal() != null && product.getOnDeal() && product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
            double discountedPrice = product.getDiscountedPrice();
            holder.tvProductPrice.setText(String.format("%,.0f₫", discountedPrice));
        } else {
            holder.tvProductPrice.setText(String.format("%,.0f₫", product.getPrice()));
        }

        // Stock
        Integer stock = product.getStock();
        if (stock != null && stock > 0) {
            holder.tvProductStock.setText("Còn " + stock);
            holder.tvProductStock.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvProductStock.setText("Hết hàng");
            holder.tvProductStock.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

//        // Sold
//        Integer sold = product.getSold();
//        holder.tvProductSold.setText("Đã bán: " + (sold != null ? sold : 0));
//
//        // Rating
//        if (product.getRating() != null && product.getRating() > 0) {
//            holder.tvProductRating.setVisibility(View.VISIBLE);
//            holder.tvProductRating.setText(String.format("%.1f", product.getRating()));
//        } else {
//            holder.tvProductRating.setVisibility(View.GONE);
//        }

        // Image
        renderImage.renderProductImage(context, product, holder.ivProductImage);

        // Menu click
        holder.btnProductMenu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductMenuClick(product, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage, btnProductMenu;
        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductStock, tvProductSold, tvProductRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            tvProductSold = itemView.findViewById(R.id.tv_product_sold);
            tvProductRating = itemView.findViewById(R.id.tv_product_rating);
            btnProductMenu = itemView.findViewById(R.id.btn_product_menu);
        }
    }
}
