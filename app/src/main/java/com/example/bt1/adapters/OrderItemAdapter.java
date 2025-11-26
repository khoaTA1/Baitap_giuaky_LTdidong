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

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private final Context context;
    private final List<Product> products;

    public OrderItemAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderItemViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView imageProduct;
        private final TextView textProductName;
        private final TextView textProductPrice;
        private final TextView textProductQuantity;

        OrderItemViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textProductQuantity = itemView.findViewById(R.id.text_product_quantity);
        }

        void bind(Product product) {
            textProductName.setText(product.getName());
            
            // Hiển thị giá
            double price = product.getPrice();
            if (product.getOnDeal() != null && product.getOnDeal()) {
                price = product.getDiscountedPrice();
            }
            textProductPrice.setText(String.format("%,.0f₫", price));
            
            // Hiển thị số lượng
            int quantity = product.getQuantity() > 0 ? product.getQuantity() : 1;
            textProductQuantity.setText("x" + quantity);
            
            // Hiển thị hình ảnh từ Firebase
            RenderImage renderImage = new RenderImage();
            renderImage.renderProductImage(context, product, imageProduct);
        }
    }
}
