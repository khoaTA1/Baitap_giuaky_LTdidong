package com.example.bt1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Voucher;
import java.util.List;
import java.util.Locale;

public class VoucherSelectAdapter extends RecyclerView.Adapter<VoucherSelectAdapter.ViewHolder> {
    
    private List<Voucher> vouchers;
    private double orderAmount;
    private OnVoucherSelectListener listener;
    
    public interface OnVoucherSelectListener {
        void onVoucherSelected(Voucher voucher);
    }
    
    public VoucherSelectAdapter(List<Voucher> vouchers, double orderAmount, OnVoucherSelectListener listener) {
        this.vouchers = vouchers;
        this.orderAmount = orderAmount;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_selectable, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        boolean isEligible = voucher.isEligible(orderAmount);
        
        holder.textCode.setText(voucher.getCode());
        
        // Build discount description
        StringBuilder discountDesc = new StringBuilder();
        discountDesc.append(String.format(Locale.getDefault(), "Giảm %d%%", voucher.getDiscountPercent()));
        if (voucher.isFreeShip()) {
            discountDesc.append(" + Miễn phí ship");
        }
        holder.textDiscount.setText(discountDesc.toString());
        
        holder.textCondition.setText(String.format(Locale.getDefault(), 
                "Đơn tối thiểu %,.0fđ", voucher.getMinOrderAmount()));
        
        if (isEligible) {
            holder.textStatus.setText("✓ Đủ điều kiện");
            holder.textStatus.setTextColor(0xFF4CAF50);
            holder.btnApply.setEnabled(true);
            holder.btnApply.setAlpha(1.0f);
        } else {
            holder.textStatus.setText("✗ Chưa đủ điều kiện");
            holder.textStatus.setTextColor(0xFFF44336);
            holder.btnApply.setEnabled(false);
            holder.btnApply.setAlpha(0.5f);
        }
        
        holder.btnApply.setOnClickListener(v -> {
            if (listener != null && isEligible) {
                listener.onVoucherSelected(voucher);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return vouchers.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCode, textDiscount, textCondition, textStatus;
        Button btnApply;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.text_voucher_code);
            textDiscount = itemView.findViewById(R.id.text_voucher_discount);
            textCondition = itemView.findViewById(R.id.text_voucher_condition);
            textStatus = itemView.findViewById(R.id.text_eligibility_status);
            btnApply = itemView.findViewById(R.id.btn_apply);
        }
    }
}
