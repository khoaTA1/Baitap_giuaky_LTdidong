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
import com.example.bt1.models.User;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserMenuClick(User user, View view);
    }

    public ManageUserAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(user.getFullName() != null ? user.getFullName() : "N/A");
        holder.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        
        // Address
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            holder.tvUserAddress.setVisibility(View.VISIBLE);
            holder.tvUserAddress.setText(user.getAddress());
        } else {
            holder.tvUserAddress.setVisibility(View.GONE);
        }

        // Role chip
        String role = user.getRole();
        if ("admin".equals(role)) {
            holder.chipUserRole.setText("Admin");
            holder.chipUserRole.setChipBackgroundColorResource(android.R.color.holo_red_light);
        } else {
            holder.chipUserRole.setText("User");
            holder.chipUserRole.setChipBackgroundColorResource(android.R.color.holo_blue_light);
        }

        // Created date
        if (user.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(user.getCreatedAt()));
            holder.tvUserCreated.setText("Tham gia: " + dateStr);
        } else {
            holder.tvUserCreated.setVisibility(View.GONE);
        }

        // Menu click
        holder.btnUserMenu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserMenuClick(user, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserAddress, tvUserCreated;
        Chip chipUserRole;
        ImageView btnUserMenu;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserAddress = itemView.findViewById(R.id.tv_user_address);
            tvUserCreated = itemView.findViewById(R.id.tv_user_created);
            chipUserRole = itemView.findViewById(R.id.chip_user_role);
            btnUserMenu = itemView.findViewById(R.id.btn_user_menu);
        }
    }
}
