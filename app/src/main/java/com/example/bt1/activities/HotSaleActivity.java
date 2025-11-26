package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.HotSaleAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HotSaleActivity extends AppCompatActivity implements HotSaleAdapter.OnHotSaleItemClickListener {

    private ImageView btnBack, btnSearch;
    private TextView textCountdownHours, textCountdownMinutes, textCountdownSeconds;
    private Button btnFilterCategory, btnFilterPrice;
    private RecyclerView recyclerViewHotSale;

    private HotSaleAdapter hotSaleAdapter;
    private List<Product> hotSaleProducts;
    private List<Product> allHotSaleProducts;
    private CountDownTimer countDownTimer;
    private Gson gson;
    
    // Filter state
    private String filterType = "all"; // "all", "category"
    private String filterValue = "";
    private String sortType = "name"; // "name", "price_low", "price_high"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_sale);

        gson = new Gson();
        initViews();
        setupRecyclerView();
        loadHotSaleProducts();
        setupListeners();
        startCountdownTimer();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        textCountdownHours = findViewById(R.id.text_countdown_hours);
        textCountdownMinutes = findViewById(R.id.text_countdown_minutes);
        textCountdownSeconds = findViewById(R.id.text_countdown_seconds);
        btnFilterCategory = findViewById(R.id.btn_filter_category);
        btnFilterPrice = findViewById(R.id.btn_filter_price);
        recyclerViewHotSale = findViewById(R.id.recycler_view_hot_sale);
    }

    private void setupRecyclerView() {
        hotSaleProducts = new ArrayList<>();
        allHotSaleProducts = new ArrayList<>();
        hotSaleAdapter = new HotSaleAdapter(this, hotSaleProducts, this);
        recyclerViewHotSale.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewHotSale.setAdapter(hotSaleAdapter);
    }

    private void loadHotSaleProducts() {
        allHotSaleProducts.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
            .whereEqualTo("onDeal", true) 
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    allHotSaleProducts.addAll(queryDocumentSnapshots.toObjects(Product.class));
                }
                applyFilterAndSort();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(HotSaleActivity.this, "Lỗi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                applyFilterAndSort();
            });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng tìm kiếm", Toast.LENGTH_SHORT).show();
        });

        btnFilterCategory.setOnClickListener(v -> showCategoryFilter());

        btnFilterPrice.setOnClickListener(v -> showSortOptions());
    }

    private void showCategoryFilter() {
        String[] categories = {
            "Tất cả", 
            "Vitamin & Khoáng chất", 
            "Sinh lý - Nội tiết tố",
            "Cải thiện tăng cường chức năng",
            "Hỗ trợ điều trị",
            "Hỗ trợ tiêu hóa",
            "Thần kinh não"
        };
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn danh mục");
        builder.setItems(categories, (dialog, which) -> {
            if (which == 0) {
                filterType = "all";
                filterValue = "";
            } else {
                filterType = "category";
                filterValue = categories[which];
            }
            applyFilterAndSort();
        });
        builder.show();
    }

    private void showSortOptions() {
        String[] sortOptions = {"Tên sản phẩm", "Giá thấp đến cao", "Giá cao đến thấp"};
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sắp xếp theo");
        builder.setItems(sortOptions, (dialog, which) -> {
            switch (which) {
                case 0: sortType = "name"; break;
                case 1: sortType = "price_low"; break;
                case 2: sortType = "price_high"; break;
            }
            applyFilterAndSort(); 
        });
        builder.show();
    }

    private void applyFilterAndSort() {
        hotSaleProducts.clear();

        for (Product product : allHotSaleProducts) {
            boolean shouldInclude = true;

            if ("category".equals(filterType)) {
                shouldInclude = product.getCategory() != null && 
                               product.getCategory().equals(filterValue);
            }

            if (shouldInclude) {
                hotSaleProducts.add(product);
            }
        }

        switch (sortType) {
            case "name":
                hotSaleProducts.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case "price_low":
                hotSaleProducts.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "price_high":
                hotSaleProducts.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
        }

        hotSaleAdapter.notifyDataSetChanged();
    }

    private void startCountdownTimer() {
        long timeInMillis = (12 * 60 * 60 + 34 * 60 + 56) * 1000;

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

                textCountdownHours.setText(String.format("%02d", hours));
                textCountdownMinutes.setText(String.format("%02d", minutes));
                textCountdownSeconds.setText(String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                textCountdownHours.setText("00");
                textCountdownMinutes.setText("00");
                textCountdownSeconds.setText("00");
                Toast.makeText(HotSaleActivity.this, "Flash Sale đã kết thúc!", Toast.LENGTH_LONG).show();
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onAddToCart(Product product) {
        Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToFavorites(Product product) {
        Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBuyNowClick(Product product) {
        List<Product> productsToBuy = new ArrayList<>();
        productsToBuy.add(product);

        double subtotal = product.getDiscountedPrice();
        double shipping = 20000; 
        double total = subtotal + shipping;
        int cartSize = 1;

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra("subtotal", subtotal);
        intent.putExtra("shipping", shipping);
        intent.putExtra("total", total);
        intent.putExtra("cart_size", cartSize);

        String selectedProductsJson = gson.toJson(productsToBuy);
        intent.putExtra("selected_products", selectedProductsJson);

        startActivity(intent);
    }
}
