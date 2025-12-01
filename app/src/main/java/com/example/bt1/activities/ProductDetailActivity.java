package com.example.bt1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.ProductDetailsAdapter;
import com.example.bt1.adapters.ProductAdapter;
import com.example.bt1.models.Comment;
import com.example.bt1.models.Product;
import com.example.bt1.models.ProductDetailsAbstract;
import com.example.bt1.models.User;
import com.example.bt1.repositories.CommentRepo;
import com.example.bt1.repositories.ProductRepo;
import com.example.bt1.repositories.UserRepo;
import com.example.bt1.utils.RenderImage;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageButton btnFavorite;
    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;
    private ImageView productImageView;

    private EditText editComment;
    private RatingBar ratingBar;
    private Button btnSendComment;

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesManager sharedPrefsManager = SharedPreferencesManager.getInstance(this);
    private Gson gson;

    private RecyclerView recyclerView;
    private ProductDetailsAdapter adapter;
    private List<ProductDetailsAbstract> productViewItems;
    private List<Comment> commentsList;
    private RenderImage renderImage;
    private UserRepo userRepo;
    private CommentRepo commentRepo;
    
    private ProductRepo productRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        productRepo = new ProductRepo();

        Log.d(">>>", "Vào product detail activity");

        // Initialize SharedPreferences and Gson
        // Lấy userId từ SharedPreferencesManager
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        String userId = prefManager.getUserId();
        
        // Sử dụng key riêng cho mỗi user
        String cartKey = userId != null ? "cart_" + userId : "cart_guest";
        sharedPreferences = getSharedPreferences(cartKey, MODE_PRIVATE);
        gson = new Gson();

        // ánh xạ các thành phần từ layout
        btnFavorite = findViewById(R.id.btn_favorite);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        productImageView = findViewById(R.id.product_image);

        // Thiết lập toolbar
        setupToolbar();

        // Lấy thông tin sản phẩm từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");
        Long productId = null;
        
        if (getIntent().hasExtra("product_id")) {
            productId = getIntent().getLongExtra("product_id", -1);
        }

        // Nếu có product_id, load từ Firebase
        final Long finalProductId = productId;
        if (productId != null && productId != -1) {
            Log.d(">>> ProductDetailActivity", "Loading product with ID: " + productId);
            productRepo.getProductById(productId, result -> {
                if (result != null) {
                    Product loadedProduct = (Product) result;
                    runOnUiThread(() -> {
                        displayProduct(loadedProduct);
                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e("!!!", "Không tìm thấy sản phẩm với ID: " + finalProductId);
                        Toast.makeText(this, "Lỗi: Không thể lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        } else if (product != null) {
            // Nếu có product object trực tiếp
            displayProduct(product);
        } else {
            Log.e("!!!", "Không có sản phẩm");
            Toast.makeText(this, "Lỗi: Không thể lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Đăng ký callback khi nhấn nút quay lại
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
    
    private void displayProduct(Product product) {
        Log.d(">>> ProductDetailActivity", "Đã nhận sản phẩm: " + product.getName());

        // Render ảnh sản phẩm
        renderImage = new RenderImage();
        renderImage.renderProductImage(this, product, productImageView);

        // Thiết lập các nút click listeners
        setupClickListeners(product);
        
        // Cập nhật trạng thái yêu thích
        updateFavoriteButton(product);

        /// chuyển đổi product thành product details abstract
        convertObj(product, new ArrayList<>());

        adapter = new ProductDetailsAdapter(productViewItems, this);
        recyclerView.setAdapter(adapter);

        commentRepo = new CommentRepo();
        commentsList = new ArrayList<>();
        
        adapter.setOnCommentSendListener((content, rating) -> {
            String userIdStr = sharedPrefsManager.getUserId();
            long userIdForComment = userIdStr != null ? Long.parseLong(userIdStr) : -1;
            Comment newCmt = new Comment(userIdForComment,
                    product.getId(),
                    rating,
                    new java.util.Date(),
                    content);

            commentRepo.createComment(newCmt);
            commentsList.add(newCmt);
            loadCommentsView(commentsList, adapter);
        });
        
        // Kiểm tra ID trước khi load comment
        if (product.getId() != null) {
            commentRepo.getCommentByPid(product.getId(), obj -> {
                commentsList = (List<Comment>) obj;
                Log.d(">>> Product Detail Activity", "Danh sách comment đã load: " + commentsList.size());

                runOnUiThread(() -> {
                    loadCommentsView(commentsList, adapter);
                    // Load similar products after comments
                    loadSimilarProducts(product);
                });
            });
        } else {
            Log.w(">>> Product Detail Activity", "Product ID is null, không thể load comment");
            // Hiển thị empty comment
            loadCommentsView(commentsList, adapter);
        }
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void setupClickListeners(Product product) {
        // Nút yêu thích
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                toggleFavorite(product);
            });
        }

        // Nút thêm vào giỏ
        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> {
                addToCart(product);
                Toast.makeText(this, "Đã thêm vào giỏ hàng: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        // Nút mua ngay
        if (btnBuyNow != null) {
            btnBuyNow.setOnClickListener(v -> {
                // Create a list with only the current product
                List<Product> productsToBuy = new ArrayList<>();
                productsToBuy.add(product);

                // Calculate prices
                double subtotal = product.getDiscountedPrice();
                double shipping = 20000; // Assuming a fixed shipping cost for now
                double total = subtotal + shipping;
                int cartSize = 1;

                // Create intent for PaymentActivity
                Intent intent = new Intent(ProductDetailActivity.this, PaymentActivity.class);

                // Pass data to PaymentActivity
                intent.putExtra("subtotal", subtotal);
                intent.putExtra("shipping", shipping);
                intent.putExtra("total", total);
                intent.putExtra("cart_size", cartSize);

                // Pass the selected product list as a JSON string
                String selectedProductsJson = gson.toJson(productsToBuy);
                intent.putExtra("selected_products", selectedProductsJson);

                startActivity(intent);
            });
        }
    }

    private void addToCart(Product product) {
        String json = sharedPreferences.getString("cart_products", "[]");
        Type type = new TypeToken<List<Product>>(){}.getType();
        List<Product> cartProducts = gson.fromJson(json, type);

        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        boolean found = false;
        for (Product p : cartProducts) {
            if (p.getId() == product.getId()) {
                // Nếu đã có, tăng số lượng
                int currentQty = p.getQuantity();
                if (currentQty == 0) currentQty = 1;
                p.setQuantity(currentQty + 1);
                found = true;
                break;
            }
        }
        
        // Nếu chưa có trong giỏ, thêm mới với số lượng = 1
        if (!found) {
            product.setQuantity(1);
            cartProducts.add(product);
        }

        String newJson = gson.toJson(cartProducts);
        sharedPreferences.edit().putString("cart_products", newJson).apply();
    }
    
    private void toggleFavorite(Product product) {
        // Lấy userId
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        String userId = prefManager.getUserId();
        String favKey = userId != null ? "favorites_" + userId : "favorites_guest";
        
        SharedPreferences favPrefs = getSharedPreferences(favKey, MODE_PRIVATE);
        String json = favPrefs.getString("favorite_products", "[]");
        Type type = new TypeToken<List<Long>>(){}.getType();
        List<Long> favoriteIds;
        try {
            favoriteIds = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            favoriteIds = new ArrayList<>();
        }
        
        if (favoriteIds == null) {
            favoriteIds = new ArrayList<>();
        }
        
        boolean isFavorite = favoriteIds.contains(product.getId());
        
        if (isFavorite) {
            // Xóa khỏi yêu thích
            favoriteIds.remove(product.getId());
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
        } else {
            // Thêm vào yêu thích
            favoriteIds.add(product.getId());
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            Toast.makeText(this, "Đã thêm vào yêu thích: " + product.getName(), Toast.LENGTH_SHORT).show();
        }
        
        String newJson = gson.toJson(favoriteIds);
        favPrefs.edit().putString("favorite_products", newJson).apply();
    }
    
    private void updateFavoriteButton(Product product) {
        // Lấy userId
        com.example.bt1.utils.SharedPreferencesManager prefManager = 
            com.example.bt1.utils.SharedPreferencesManager.getInstance(this);
        String userId = prefManager.getUserId();
        String favKey = userId != null ? "favorites_" + userId : "favorites_guest";
        
        SharedPreferences favPrefs = getSharedPreferences(favKey, MODE_PRIVATE);
        String json = favPrefs.getString("favorite_products", "[]");
        Type type = new TypeToken<List<Long>>(){}.getType();
        List<Long> favoriteIds;
        try {
            favoriteIds = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            // Handle error, maybe the stored JSON is malformed or from an old version
            favoriteIds = new ArrayList<>();
            // Optionally, clear the bad data
            favPrefs.edit().remove("favorite_products").apply();
        }
        
        if (favoriteIds != null && favoriteIds.contains(product.getId())) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void convertObj(Product product, List<Comment> commentList) {
        productViewItems = new ArrayList<>();

        productViewItems.add(new ProductDetailsAbstract.ProductImage(product.getImageResId()));
        
        // Tạo ProductPreview với giá gốc và giá giảm
        if (product.getOnDeal() != null && product.getOnDeal() && product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
            // Có giảm giá: hiển thị giá giảm và giá gốc
            String discountedPrice = String.format("%,.0f₫", product.getDiscountedPrice());
            String originalPrice = String.format("%,.0f₫", product.getPrice());
            productViewItems.add(new ProductDetailsAbstract.ProductPreview(product.getName(), discountedPrice, originalPrice, product.getDescription()));
        } else {
            // Không giảm giá: chỉ hiển thị giá thường
            productViewItems.add(new ProductDetailsAbstract.ProductPreview(product.getName(), product.getPriceFormatted(), product.getDescription()));
        }
        productViewItems.add(new ProductDetailsAbstract.SpecTitle(getString(R.string.product_spec_title)));

        // thêm riêng lẽ từng thông tin sản phẩm vào 1 danh sách
        List<ProductDetailsAbstract.ProductSpec> specs = new ArrayList<>();
        specs.add(new ProductDetailsAbstract.ProductSpec("Danh mục", product.getCategory()));
        specs.add(new ProductDetailsAbstract.ProductSpec("Dạng bào chế", product.getDosageForm()));
        specs.add(new ProductDetailsAbstract.ProductSpec("Gồm", product.getInclude()));
        specs.add(new ProductDetailsAbstract.ProductSpec("Thành phần", product.getIngredient()));
        specs.add(new ProductDetailsAbstract.ProductSpec("cách dùng", product.getUse()));
        specs.add(new ProductDetailsAbstract.ProductSpec("Tác dụng phụ", product.getSideEffects()));
        specs.add(new ProductDetailsAbstract.ProductSpec("Đối tượng sử dụng", product.getObject()));
        specs.add(new ProductDetailsAbstract.ProductSpec("Nơi sản xuất", product.getOriginal()));

        // thêm danh sách phía trên vào đối tượng view Product Spec Group
        // giải quyết vấn đề các thông tin được render rời rạc không có sự liên kết
        productViewItems.add(new ProductDetailsAbstract.ProductSpecGroup(specs));

        productViewItems.add(new ProductDetailsAbstract.CommentTitle(getString(R.string.product_comment_title)));

        productViewItems.add(new ProductDetailsAbstract.CommentInput());
    }

    private void loadCommentsView(List<Comment> commentList, ProductDetailsAdapter adapter) {
        userRepo = new UserRepo();

        int totalComments = commentList.size();
        final AtomicInteger count = new AtomicInteger();

        for (Comment c : commentList) {
            long uid = c.getUserId();
            userRepo.getUserById(uid, obj -> {
                User currentUser = (User) obj;

                Log.d(">>> Load comment view", "User có fname: " + currentUser.getFullName());
                if (currentUser != null) {
                    productViewItems.add(
                            new ProductDetailsAbstract.Comment(
                                    currentUser.getFullName(),
                                    c.getCreatedDate(),
                                    c.getRate(),
                                    c.getContent()
                            )
                    );
                } else {
                    Log.e("!!! Load comment view", "Không tìm thấy user");
                }

                if (count.incrementAndGet() == totalComments) {
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                }
            });

        }

    }
    
    private void loadSimilarProducts(Product currentProduct) {
        if (currentProduct.getCategory() == null || currentProduct.getCategory().isEmpty()) {
            return;
        }
        
        productRepo.getAllProducts(result -> {
            if (result != null) {
                List<Product> allProducts = (List<Product>) result;
                List<Product> similarProducts = new ArrayList<>();
                
                // Filter products with same category, exclude current product
                for (Product product : allProducts) {
                    if (product.getCategory() != null && 
                        product.getCategory().equals(currentProduct.getCategory()) &&
                        !product.getId().equals(currentProduct.getId())) {
                        similarProducts.add(product);
                        if (similarProducts.size() >= 5) break; // Limit to 5 products
                    }
                }
                
                runOnUiThread(() -> {
                    if (!similarProducts.isEmpty()) {
                        // Add similar products section to adapter
                        productViewItems.add(new ProductDetailsAbstract.SimilarProductsSection(similarProducts, currentProduct.getId()));
                        adapter.notifyItemInserted(productViewItems.size() - 1);
                        Log.d(">>> ProductDetail", "Loaded " + similarProducts.size() + " similar products");
                    }
                });
            }
        });
    }
}
