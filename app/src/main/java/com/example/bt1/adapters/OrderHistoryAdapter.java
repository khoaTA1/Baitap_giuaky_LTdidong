package com.example.bt1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.activities.PaymentActivity;
import com.example.bt1.models.Order;
import com.example.bt1.models.Product;
import com.example.bt1.repositories.OrderRepo;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final Gson gson = new Gson();
    private final boolean isAdminView;
    private OnOrderActionListener actionListener;
    private OrderRepo orderRepo;
    
    public interface OnOrderActionListener {
        void onConfirmOrder(Order order, int position);
    }

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.isAdminView = false;
        this.orderRepo = new OrderRepo();
    }
    
    public OrderHistoryAdapter(Context context, List<Order> orderList, boolean isAdminView) {
        this.context = context;
        this.orderList = orderList;
        this.isAdminView = isAdminView;
        this.orderRepo = new OrderRepo();
    }
    
    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView textOrderId, textOrderDate, textOrderStatus, textOrderTotal;
        private final RecyclerView recyclerProducts;
        private final Button btnCancelOrder, btnReorder, btnConfirmOrder;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            textOrderTotal = itemView.findViewById(R.id.text_order_total);
            recyclerProducts = itemView.findViewById(R.id.recycler_view_products);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
            btnReorder = itemView.findViewById(R.id.btn_reorder);
            btnConfirmOrder = itemView.findViewById(R.id.btn_confirm_order);
        }

        void bind(Order order) {
            textOrderId.setText("Mã đơn hàng: #" + order.getOrderId());
            textOrderDate.setText("Ngày đặt: " + order.getOrderDate());
            textOrderStatus.setText(order.getStatus());
            textOrderTotal.setText(String.format("%,.0f₫", order.getTotalAmount()));

            // Setup nested RecyclerView for products
            List<Product> products = order.getItems();
            if (products != null && !products.isEmpty()) {
                OrderItemAdapter adapter = new OrderItemAdapter(context, products);
                recyclerProducts.setLayoutManager(new LinearLayoutManager(context));
                recyclerProducts.setAdapter(adapter);
                recyclerProducts.setVisibility(View.VISIBLE);
            } else {
                recyclerProducts.setVisibility(View.GONE);
            }

            // Control button visibility based on order status and view type
            String status = order.getStatus();
            
            if (isAdminView) {
                // Admin view: Show confirm button for pending orders
                if ("Chờ xác nhận".equals(status) || "Đang xử lý".equals(status)) {
                    btnConfirmOrder.setVisibility(View.VISIBLE);
                    btnCancelOrder.setVisibility(View.GONE);
                    btnReorder.setVisibility(View.GONE);
                } else {
                    btnConfirmOrder.setVisibility(View.GONE);
                    btnCancelOrder.setVisibility(View.GONE);
                    btnReorder.setVisibility(View.GONE);
                }
            } else {
                // User view: Control cancel button based on order status
                btnConfirmOrder.setVisibility(View.GONE);
                
                if ("Hoàn thành".equals(status) || "Đã giao".equals(status)) {
                    // Đơn hàng đã hoàn thành → chỉ hiển thị nút Mua lại
                    btnCancelOrder.setVisibility(View.GONE);
                    btnReorder.setVisibility(View.VISIBLE);
                } else if ("Đã hủy".equals(status)) {
                    // Đơn hàng đã hủy → hiển thị nút Mua lại
                    btnCancelOrder.setVisibility(View.GONE);
                    btnReorder.setVisibility(View.VISIBLE);
                } else if ("Đã xác nhận".equals(status) || "Đang giao".equals(status)) {
                    // Đơn hàng đã xác nhận/đang giao → không thể hủy
                    btnCancelOrder.setVisibility(View.GONE);
                    btnReorder.setVisibility(View.GONE);
                } else {
                    // Đơn hàng chờ xác nhận → cho phép hủy
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    btnReorder.setVisibility(View.GONE);
                }
            }

            // Handle cancel button click
            btnCancelOrder.setOnClickListener(v -> showCancelDialog(order));
            
            // Handle confirm button click (admin only)
            btnConfirmOrder.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onConfirmOrder(order, getAdapterPosition());
                }
            });
            
            // Handle reorder button click
            btnReorder.setOnClickListener(v -> reorder(order));
        }

        private void showCancelDialog(Order order) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Hủy đơn hàng");

            final EditText input = new EditText(context);
            input.setHint("Nhập lý do hủy");
            builder.setView(input);

            builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                String reason = input.getText().toString();
                if (reason.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show();
                } else {
                    cancelOrder(order);
                }
            });
            builder.setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.cancel());

            builder.show();
        }

        private void cancelOrder(Order order) {
            // Update status in Firebase
            orderRepo.updateOrderStatus(order.getOrderId(), "Đã hủy", new OrderRepo.OnCompleteListener() {
                @Override
                public void onComplete(boolean success, String message) {
                    if (success) {
                        // Update local data
                        order.setStatus("Đã hủy");
                        notifyItemChanged(getAdapterPosition());
                        Toast.makeText(context, "Đã hủy đơn hàng #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void reorder(Order order) {
            List<Product> productsToReorder = order.getItems();
            if (productsToReorder == null || productsToReorder.isEmpty()) {
                Toast.makeText(context, "Không có sản phẩm để mua lại.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create intent for PaymentActivity
            Intent intent = new Intent(context, PaymentActivity.class);

            // Pass data to PaymentActivity
            intent.putExtra("subtotal", order.getTotalAmount()); // Assuming total is the subtotal for reorder
            intent.putExtra("shipping", 0.0); // Or calculate shipping again
            intent.putExtra("total", order.getTotalAmount());
            intent.putExtra("cart_size", productsToReorder.size());

            // Pass the selected product list as a JSON string
            String selectedProductsJson = gson.toJson(productsToReorder);
            intent.putExtra("selected_products", selectedProductsJson);

            context.startActivity(intent);
        }
    }
}
