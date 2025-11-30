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
import com.example.bt1.models.DeliveryAddress;
import com.google.android.material.chip.Chip;

import java.util.List;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder> {

    private Context context;
    private List<DeliveryAddress> addresses;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onEditClick(DeliveryAddress address);
        void onDeleteClick(DeliveryAddress address);
        void onAddressClick(DeliveryAddress address); // For selection mode
    }

    public DeliveryAddressAdapter(Context context, List<DeliveryAddress> addresses, OnAddressActionListener listener) {
        this.context = context;
        this.addresses = addresses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryAddress address = addresses.get(position);

        // Set label
        String label = address.getLabel();
        holder.textLabel.setText(label != null && !label.isEmpty() ? label : "Địa chỉ");

        // Set recipient info - handle null values
        String recipientName = address.getRecipientName();
        String phoneNumber = address.getPhoneNumber();
        
        boolean hasRecipientInfo = false;
        
        if (recipientName != null && !recipientName.isEmpty()) {
            holder.textRecipientName.setText(recipientName);
            holder.textRecipientName.setTextColor(context.getResources().getColor(android.R.color.black));
            hasRecipientInfo = true;
        } else {
            holder.textRecipientName.setText("[Chưa có tên người nhận]");
            holder.textRecipientName.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        holder.textRecipientName.setVisibility(View.VISIBLE);
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            holder.textPhone.setText(phoneNumber);
            holder.textPhone.setTextColor(context.getResources().getColor(android.R.color.black));
            hasRecipientInfo = true;
        } else {
            holder.textPhone.setText("[Chưa có số điện thoại]");
            holder.textPhone.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        holder.textPhone.setVisibility(View.VISIBLE);
        
        // Show warning icon if missing recipient info
        if (!hasRecipientInfo) {
            holder.itemView.setAlpha(0.8f);
        }

        // Set full address
        holder.textAddress.setText(address.getFullAddress());

        // Show default badge if this is default address
        if (address.getIsDefault()) {
            holder.chipDefault.setVisibility(View.VISIBLE);
            // Highlight the card for default address
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.chipDefault.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.9f);
        }

        // Set click listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(address);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(address);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void updateData(List<DeliveryAddress> newAddresses) {
        this.addresses = newAddresses;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textLabel;
        TextView textRecipientName;
        TextView textPhone;
        TextView textAddress;
        Chip chipDefault;
        ImageView btnEdit;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textLabel = itemView.findViewById(R.id.text_label);
            textRecipientName = itemView.findViewById(R.id.text_recipient_name);
            textPhone = itemView.findViewById(R.id.text_phone);
            textAddress = itemView.findViewById(R.id.text_address);
            chipDefault = itemView.findViewById(R.id.chip_default);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
