package com.example.bt1.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Voucher;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private List<Voucher> voucherList;
    private Context context;

    public VoucherAdapter(Context context, List<Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        holder.voucherCode.setText(voucher.getCode());
        holder.voucherDescription.setText(voucher.getDescription());
        holder.voucherExpiry.setText("Hết hạn: " + voucher.getExpiryDate());

        holder.copyButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Voucher Code", voucher.getCode());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Đã sao chép mã: " + voucher.getCode(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView voucherCode, voucherDescription, voucherExpiry;
        Button copyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherCode = itemView.findViewById(R.id.textViewVoucherCode);
            voucherDescription = itemView.findViewById(R.id.textViewVoucherDescription);
            voucherExpiry = itemView.findViewById(R.id.textViewVoucherExpiry);
            copyButton = itemView.findViewById(R.id.buttonCopyVoucher);
        }
    }
}
