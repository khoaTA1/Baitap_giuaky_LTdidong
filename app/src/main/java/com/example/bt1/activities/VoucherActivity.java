package com.example.bt1.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.VoucherAdapter;
import com.example.bt1.models.Voucher;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewVouchers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        List<Voucher> voucherList = new ArrayList<>();
        voucherList.add(new Voucher("GIAM10K", "Giảm 10.000đ cho đơn hàng từ 100.000đ", "31/12/2024"));
        voucherList.add(new Voucher("FREESHIP", "Miễn phí vận chuyển cho đơn hàng từ 200.000đ", "31/12/2024"));
        voucherList.add(new Voucher("SALE50", "Giảm 50% tối đa 50.000đ", "30/06/2024"));

        VoucherAdapter adapter = new VoucherAdapter(this, voucherList);
        recyclerView.setAdapter(adapter);
    }
}
