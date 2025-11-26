package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.bt1.R;
import com.example.bt1.models.Product;
import com.example.bt1.adapters.FavoriteAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements FavoriteAdapter.OnFavoriteItemClickListener {

    private ImageView btnBack;
    private TextView textFavoriteCount;
    private EditText editSearchFavorites;
    private Button btnFilter;
    private RecyclerView recyclerViewFavorites;
    private LinearLayout layoutEmptyState;
    private Button btnStartShopping;
    private LinearLayout layoutBottomActions;
    private CheckBox checkboxSelectAll;
    private Button btnAddToCart, btnRemoveSelected;

    private FavoriteAdapter favoriteAdapter;
    private List<Product> favoriteProducts;
    private List<Product> filteredProducts;
    private boolean isSelectionMode = false;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yeuthich);

        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupRecyclerView();
//        loadFavoriteProducts();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        textFavoriteCount = findViewById(R.id.text_favorite_count);
        
        // Lấy EditText trực tiếp bằng ID
        editSearchFavorites = findViewById(R.id.edit_search_favorites);
        
        btnFilter = findViewById(R.id.btn_filter);
        recyclerViewFavorites = findViewById(R.id.recycler_view_favorites);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        btnStartShopping = findViewById(R.id.btn_start_shopping);
        layoutBottomActions = findViewById(R.id.layout_bottom_actions);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnRemoveSelected = findViewById(R.id.btn_remove_selected);
    }

    private void setupRecyclerView() {
        favoriteProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(this, filteredProducts, this);
        recyclerViewFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewFavorites.setAdapter(favoriteAdapter);
    }
    /*
    private void loadFavoriteProducts() {
        // Simulate loading favorite products
        favoriteProducts.clear();
        
        // Sample data - in real app, load from SharedPreferences or Database
        favoriteProducts.add(new Product("iPhone 15 Pro Max 256GB", "30.590.000₫", R.drawable.iphone_15));
        favoriteProducts.add(new Product("Samsung Galaxy S24 Ultra", "28.990.000₫", R.drawable.samsung_s23));
        favoriteProducts.add(new Product("Xiaomi 14T Pro", "15.990.000₫", R.drawable.xiaomi_13t));
        favoriteProducts.add(new Product("OPPO Find X8 Pro", "22.990.000₫", R.drawable.iphone_14));
        favoriteProducts.add(new Product("Vivo V30 Pro 5G", "12.990.000₫", R.drawable.iphone_15));

        filteredProducts.clear();
        filteredProducts.addAll(favoriteProducts);
        
        updateUI();
    }
     */

    private void updateUI() {
        if (favoriteProducts.isEmpty()) {
            recyclerViewFavorites.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            textFavoriteCount.setText("0 sản phẩm");
        } else {
            recyclerViewFavorites.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            textFavoriteCount.setText(favoriteProducts.size() + " sản phẩm");
        }
        
        favoriteAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnStartShopping.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        btnFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng lọc sẽ được cập nhật", Toast.LENGTH_SHORT).show();
        });

        // Search functionality
        editSearchFavorites.setOnEditorActionListener((v, actionId, event) -> {
            performSearch(editSearchFavorites.getText().toString().trim());
            return true;
        });

        // Selection mode
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            favoriteAdapter.selectAll(isChecked);
        });

        btnAddToCart.setOnClickListener(v -> {
            List<Product> selectedProducts = favoriteAdapter.getSelectedProducts();
            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Toast.makeText(this, "Đã thêm " + selectedProducts.size() + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            exitSelectionMode();
        });

        btnRemoveSelected.setOnClickListener(v -> {
            List<Product> selectedProducts = favoriteAdapter.getSelectedProducts();
            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            favoriteProducts.removeAll(selectedProducts);
            filteredProducts.removeAll(selectedProducts);
            
            Toast.makeText(this, "Đã xóa " + selectedProducts.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
            exitSelectionMode();
            updateUI();
        });
    }

    private void performSearch(String query) {
        filteredProducts.clear();
        
        if (query.isEmpty()) {
            filteredProducts.addAll(favoriteProducts);
        } else {
            for (Product product : favoriteProducts) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
        }
        
        favoriteAdapter.notifyDataSetChanged();
    }

    private void enterSelectionMode() {
        isSelectionMode = true;
        layoutBottomActions.setVisibility(View.VISIBLE);
        favoriteAdapter.setSelectionMode(true);
    }

    private void exitSelectionMode() {
        isSelectionMode = false;
        layoutBottomActions.setVisibility(View.GONE);
        checkboxSelectAll.setChecked(false);
        favoriteAdapter.setSelectionMode(false);
        favoriteAdapter.clearSelections();
    }

    @Override
    public void onRemoveFromFavorites(Product product) {
        favoriteProducts.remove(product);
        filteredProducts.remove(product);
        updateUI();
        Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCart(Product product) {
        Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProductClick(Product product) {
        // Navigate to product detail
        Toast.makeText(this, "Xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongPress(Product product) {
        if (!isSelectionMode) {
            enterSelectionMode();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSelectionMode) {
            exitSelectionMode();
        } else {
            super.onBackPressed();
        }
    }
}