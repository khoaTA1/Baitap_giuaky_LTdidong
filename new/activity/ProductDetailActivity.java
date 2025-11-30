package com.example.bt1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.utils.SoldCountCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductDetailsAdapter adapter;
    private List<ProductDetailsAbstract> productViewItems;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Product product = (Product) getIntent().getSerializableExtra("product");

        // chủyển ổi từ đối tượng product sang đối tượng trừa tượng để hiển thị ra recycler view
        productViewItems = new ArrayList<>();

        if (product != null) {
            // Lấy sold count từ cache
            SoldCountCache soldCountCache = new SoldCountCache(this);
            int soldCount = soldCountCache.getSoldCount(product.getId());
            
            productViewItems.add(new ProductDetailsAbstract.ProductImage(product.getImageResId()));
            productViewItems.add(new ProductDetailsAbstract.ProductPreview(product.getName(), product.getPrice(), soldCount));
            productViewItems.add(new ProductDetailsAbstract.SpecTitle(getString(R.string.product_spec_title)));
            productViewItems.add(new ProductDetailsAbstract.ProductSpec("Màn hình", product.getScreenSize()));
            productViewItems.add(new ProductDetailsAbstract.ProductSpec("RAM", product.getMemory()));
            productViewItems.add(new ProductDetailsAbstract.ProductSpec("Bộ nhớ", product.getStorage()));
            productViewItems.add(new ProductDetailsAbstract.CommentTitle(getString(R.string.product_comment_title)));
        } else {
            // debug truyền đối tượng product
            Log.e("Từ ProductDetailActivity: ", "Không thể lấy product từ Intent");
        }

        adapter = new ProductDetailsAdapter(productViewItems, this);
        recyclerView.setAdapter(adapter);
    }
}
