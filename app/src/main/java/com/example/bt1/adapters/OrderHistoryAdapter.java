package com.example.bt1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Order;
import com.example.bt1.models.Product;

import java.util.List;
import java.util.stream.Collectors;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<Order> orderList;

    public OrderHistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderId.setText("Mã đơn hàng: #" + order.getOrderId());
        holder.orderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.orderStatus.setText(order.getStatus());
        holder.orderTotal.setText(String.format("Tổng tiền: %,.0f₫", order.getTotalAmount()));

        String items = order.getItems().stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));
        holder.orderItems.setText("Sản phẩm: " + items);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, orderStatus, orderTotal, orderItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.textViewOrderId);
            orderDate = itemView.findViewById(R.id.textViewOrderDate);
            orderStatus = itemView.findViewById(R.id.textViewOrderStatus);
            orderTotal = itemView.findViewById(R.id.textViewOrderTotal);
            orderItems = itemView.findViewById(R.id.textViewOrderItems);
        }
    }
}
