package com.example.bt1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.DeliveryAddress;

import java.util.List;

public class PaymentAddressAdapter extends RecyclerView.Adapter<PaymentAddressAdapter.ViewHolder> {

    private Context context;
    private List<DeliveryAddress> addresses;
    private int selectedPosition = -1;
    private OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onAddressSelected(DeliveryAddress address, int position);
    }

    public PaymentAddressAdapter(Context context, List<DeliveryAddress> addresses, OnAddressSelectedListener listener) {
        this.context = context;
        this.addresses = addresses;
        this.listener = listener;
        
        // Select default address if available
        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).getIsDefault()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryAddress address = addresses.get(position);

        // Set label
        String label = address.getLabel();
        holder.textLabel.setText(label != null && !label.isEmpty() ? label : "Địa chỉ");

        // Set recipient info (handle null values)
        String recipientInfo = address.getRecipientInfo();
        if (recipientInfo != null && !recipientInfo.isEmpty()) {
            holder.textRecipient.setText(recipientInfo);
            holder.textRecipient.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            holder.textRecipient.setText("[Chưa cập nhật tên và SĐT - Vui lòng chỉnh sửa]");
            holder.textRecipient.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        // Set full address
        holder.textAddress.setText(address.getFullAddress());

        // Set radio button state
        holder.radioButton.setChecked(position == selectedPosition);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onAddressSelected(address, selectedPosition);
            }
        });

        // Handle radio button click
        holder.radioButton.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onAddressSelected(address, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public DeliveryAddress getSelectedAddress() {
        if (selectedPosition >= 0 && selectedPosition < addresses.size()) {
            return addresses.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView textLabel;
        TextView textRecipient;
        TextView textAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_select);
            textLabel = itemView.findViewById(R.id.text_label);
            textRecipient = itemView.findViewById(R.id.text_recipient);
            textAddress = itemView.findViewById(R.id.text_address);
        }
    }
}
