# 📝 Development Checklist

## 🎯 Ưu Tiên Tiếp Theo

### 🔥 Độ ưu tiên CAO (Tuần này)

- [ ] **Tạo ProductViewModel**
  - [ ] Extend AndroidViewModel
  - [ ] Khai báo ProductRepository
  - [ ] Tạo LiveData cho product list
  - [ ] Method getProducts(), searchProducts()
  
- [ ] **Update HomeActivity dùng ViewModel**
  - [ ] Khởi tạo ProductViewModel
  - [ ] Observe LiveData
  - [ ] Update UI khi data thay đổi
  - [ ] Show loading state
  
- [ ] **Tạo ProductDetailActivity**
  - [ ] Layout: product_detail.xml
  - [ ] Show product info, images
  - [ ] Button: Add to Cart
  - [ ] Quantity selector
  - [ ] Product description, specs

- [ ] **Tạo CartManager (Singleton)**
  - [ ] Manage cart items in memory
  - [ ] Add/Remove/Update quantity
  - [ ] Calculate total price
  - [ ] Save to SharedPreferences

---

### ⚡ Độ ưu tiên TRUNG (Tuần sau)

- [ ] **Tạo CartActivity**
  - [ ] Layout: activity_cart.xml
  - [ ] RecyclerView với CartAdapter
  - [ ] Show cart items
  - [ ] Update quantity
  - [ ] Remove items
  - [ ] Show total price
  - [ ] Button: Checkout

- [ ] **Tạo OrderActivity (Checkout)**
  - [ ] Form: Địa chỉ, SĐT
  - [ ] Payment method selection
  - [ ] Order summary
  - [ ] Confirm button
  - [ ] Integration với OrderRepository

- [ ] **Implement Search**
  - [ ] SearchView trong HomeActivity
  - [ ] Filter products locally
  - [ ] Highlight search results
  - [ ] Recent searches

---

### 💡 Độ ưu tiên THẤP (Sau này)

- [ ] **OrderHistoryActivity**
  - [ ] List orders
  - [ ] Order status
  - [ ] Order details
  - [ ] Cancel order option

- [ ] **Implement Room Database**
  - [ ] Product entity
  - [ ] Cart entity
  - [ ] DAO interfaces
  - [ ] Database class
  - [ ] Migration strategy

- [ ] **Advanced Features**
  - [ ] Product filter by price
  - [ ] Sort products
  - [ ] Wishlist
  - [ ] Product reviews
  - [ ] User ratings
  - [ ] Image zoom
  - [ ] Share product

---

## 🔧 Technical Tasks

### API Integration
- [ ] Setup backend API (hoặc mock API)
- [ ] Test all endpoints với Postman
- [ ] Implement error handling
- [ ] Add retry logic
- [ ] Loading indicators
- [ ] Empty states

### UI/UX Improvements
- [ ] Add animations (transitions)
- [ ] Improve bottom navigation
- [ ] Add splash screen
- [ ] Implement pull-to-refresh
- [ ] Add placeholder images
- [ ] Error state designs
- [ ] Success messages

### Testing
- [ ] Unit tests cho ViewModels
- [ ] Unit tests cho Repositories
- [ ] Unit tests cho Utils
- [ ] UI tests cho main flows
- [ ] Integration tests

### Performance
- [ ] Image caching strategy
- [ ] Lazy loading for lists
- [ ] Pagination for products
- [ ] Memory leak checks
- [ ] ProGuard rules

---

## 📋 Code Templates

### 1. ViewModel Template
```java
public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private MutableLiveData<List<Product>> products;
    private MutableLiveData<Boolean> isLoading;
    
    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository();
        products = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
    }
    
    public LiveData<List<Product>> getProducts() {
        // Load products
        return products;
    }
}
```

### 2. Activity with ViewModel
```java
public class HomeActivity extends AppCompatActivity {
    private ProductViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        // Init ViewModel
        viewModel = new ViewModelProvider(this)
            .get(ProductViewModel.class);
        
        // Observe data
        viewModel.getProducts().observe(this, products -> {
            // Update UI
            adapter.setProducts(products);
        });
    }
}
```

### 3. CartManager Singleton
```java
public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    
    private CartManager() {
        cartItems = new ArrayList<>();
    }
    
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
    
    public void addToCart(Product product, int quantity) {
        // Implementation
    }
    
    public double getTotalPrice() {
        // Calculate total
    }
}
```

---

## 🎨 UI Components Cần Tạo

### Layouts
- [ ] `activity_product_detail.xml`
- [ ] `activity_cart.xml`
- [ ] `activity_order.xml`
- [ ] `activity_order_history.xml`
- [ ] `activity_search.xml`
- [ ] `item_cart.xml` (RecyclerView item)
- [ ] `item_order.xml` (RecyclerView item)
- [ ] `dialog_quantity_picker.xml`

### Drawables/Icons
- [ ] ic_cart.xml
- [ ] ic_favorite.xml
- [ ] ic_share.xml
- [ ] ic_filter.xml
- [ ] ic_sort.xml
- [ ] loading_animation.xml

### Strings
```xml
<string name="add_to_cart">Thêm vào giỏ</string>
<string name="buy_now">Mua ngay</string>
<string name="cart_empty">Giỏ hàng trống</string>
<string name="order_success">Đặt hàng thành công!</string>
```

---

## 🐛 Known Issues

- [ ] None yet (Build successful!)

---

## 📱 Testing Checklist

### Manual Testing
- [ ] Login flow
- [ ] Register flow
- [ ] Browse products
- [ ] View product detail
- [ ] Add to cart
- [ ] Update cart
- [ ] Checkout
- [ ] View order history
- [ ] Logout

### Edge Cases
- [ ] Empty states (no products, empty cart)
- [ ] Network errors
- [ ] Invalid input
- [ ] Session timeout
- [ ] Low memory situations

---

## 📊 Progress Tracking

**Current Sprint:** Foundation Setup
- [x] Project structure ✅
- [x] Models & Repositories ✅
- [x] Network layer ✅
- [x] Utils & Helpers ✅
- [x] Base Activities ✅
- [x] Build success ✅

**Next Sprint:** Core Features
- [ ] ViewModels
- [ ] Product Detail
- [ ] Cart Management
- [ ] Checkout Flow

**Future Sprints:**
- [ ] Order History
- [ ] Advanced Features
- [ ] Performance Optimization
- [ ] Polish & Testing

---

**Last Updated:** November 7, 2025  
**Status:** 🟢 Ready for Next Phase
