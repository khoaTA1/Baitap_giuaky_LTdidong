package com.example.bt1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Voucher;

import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    
    private List<Voucher> vouchers;
    private OnVoucherActionListener listener;
    
    public interface OnVoucherActionListener {
        void onEdit(Voucher voucher, int position);
        void onDelete(Voucher voucher, int position);
        void onToggleActive(Voucher voucher, int position, boolean isActive);
    }
    
    public VoucherAdapter(List<Voucher> vouchers, OnVoucherActionListener listener) {
        this.vouchers = vouchers;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        
        holder.textCode.setText(voucher.getCode());
        
        // Discount description
        String desc = String.format(Locale.getDefault(), "Giảm %d%%", voucher.getDiscountPercent());
        if (voucher.getMaxDiscount() > 0) {
            desc += String.format(Locale.getDefault(), " - Tối đa %,.0fđ", voucher.getMaxDiscount());
        }
        if (voucher.isFreeShip()) {
            desc += " + FREE SHIP";
        }
        holder.textDesc.setText(desc);
        
        // Condition
        holder.textCondition.setText(String.format(Locale.getDefault(), 
                "📌 Đơn tối thiểu %,.0fđ", voucher.getMinOrderAmount()));
        
        // Quantity
        int remaining = voucher.getRemainingQuantity();
        holder.textQuantity.setText(String.format(Locale.getDefault(),
                "Còn %d/%d", remaining, voucher.getTotalQuantity()));
        
        // Expiry date
        holder.textExpiryDate.setText("HSD: " + voucher.getExpiryDate());
        
        // Active status
        holder.switchActive.setChecked(voucher.isActive());
        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggleActive(voucher, position, isChecked);
            }
        });
        
        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(voucher, position);
            }
        });
        
        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(voucher, position);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return vouchers.size();
    }
    
    public void updateData(List<Voucher> newVouchers) {
        this.vouchers = newVouchers;
        notifyDataSetChanged();
    }
    
    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView textCode, textDesc, textCondition, textQuantity, textExpiryDate;
        SwitchCompat switchActive;
        Button btnEdit, btnDelete;
        
        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.text_voucher_code);
            textDesc = itemView.findViewById(R.id.text_voucher_desc);
            textCondition = itemView.findViewById(R.id.text_voucher_condition);
            textQuantity = itemView.findViewById(R.id.text_voucher_quantity);
            textExpiryDate = itemView.findViewById(R.id.text_expiry_date);
            switchActive = itemView.findViewById(R.id.switch_active);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
