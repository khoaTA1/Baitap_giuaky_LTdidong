package com.example.bt1.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.example.bt1.models.Comment;
import com.example.bt1.models.Product;
import com.example.bt1.models.ProductDetailsAbstract;
import com.example.bt1.models.User;
import com.example.bt1.repositories.CommentRepo;
import com.example.bt1.repositories.UserRepo;
import com.example.bt1.utils.RenderImage;
import com.example.bt1.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        productImageView = findViewById(R.id.product_image);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(">>>", "Vào product detail activity");

        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences("cart", MODE_PRIVATE);
        gson = new Gson();

        // ánh xạ các thành phần từ layout
        btnFavorite = findViewById(R.id.btn_favorite);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        // Thiết lập toolbar
        setupToolbar();

        // Lấy thông tin sản phẩm từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            Log.d(">>> ProductDetailActivity", "Đã nhận sản phẩm: " + product.getName());
            ///  render ảnh ra image view
            renderImage = new RenderImage();
            renderImage.renderProductImage(this, product, productImageView);

            /// chuyển đổi product thành product details abstract
            convertObj(product);

            adapter = new ProductDetailsAdapter(productViewItems, this);
            recyclerView.setAdapter(adapter);

            commentRepo = new CommentRepo();
            commentRepo.getCommentByPid(product.getId(), obj -> {
                commentsList = (List<Comment>) obj;
                Log.d(">>> Product Detail Activity", "Danh sách comment đã load: " + commentsList.size());

                runOnUiThread(() -> {
                    loadCommentsView(commentsList, adapter);
                });
            });
        } else {
            Log.e("!!!", "Không có product");
            Toast.makeText(this, "Lỗi: Không thể lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có dữ liệu
        }

        adapter.setOnCommentSendListener((content, rating) -> {
            Comment newCmt = new Comment(sharedPreferences.getLong("userId", -1),
                    product.getId(),
                    rating,
                    new Date(),
                    content);

            commentRepo.createComment(newCmt);

            // Chèn comment view vào RecyclerView
            sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            productViewItems.add(new ProductDetailsAbstract.Comment(
                    sharedPreferences.getString("fullname", "<Người dùng>"),
                    newCmt.getCreatedDate(),
                    newCmt.getRate(),
                    newCmt.getContent()
            ));

            adapter.notifyItemInserted(productViewItems.size() - 1);
        });

        // Thiết lập các sự kiện click
        setupClickListeners(product);

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
                Toast.makeText(this, "Đã thêm vào yêu thích: " + product.getName(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Chuyển đến trang thanh toán cho: " + product.getName(), Toast.LENGTH_SHORT).show();
                // TODO: Chuyển đến PaymentActivity
            });
        }
    }

    private void addToCart(Product product) {
        String json = sharedPreferences.getString("cart_products", "[]");
        Type type = new TypeToken<List<Product>>() {
        }.getType();
        List<Product> cartProducts = gson.fromJson(json, type);

        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        cartProducts.add(product);
        String newJson = gson.toJson(cartProducts);
        sharedPreferences.edit().putString("cart_products", newJson).commit();
    }

    private void convertObj(Product product) {
        productViewItems = new ArrayList<>();

        //productViewItems.add(new ProductDetailsAbstract.ProductImage(product));
        productViewItems.add(new ProductDetailsAbstract.ProductPreview(product.getName(), product.getPriceFormatted(), product.getDescription()));
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
}

