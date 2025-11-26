# 📱 TaoStore - App Bán Điện Thoại Di Động

## 🏗️ Cấu Trúc Dự Án Hoàn Chỉnh

Dự án được tổ chức theo mô hình **Clean Architecture** và **MVVM Pattern** để dễ bảo trì và mở rộng.

```
com.example.bt1/
├── 📁 activities/          # Các màn hình Activity
│   ├── MainActivity.java           ✅ Màn hình đăng nhập
│   ├── RegisterActivity.java       ✅ Màn hình đăng ký
│   ├── HomeActivity.java           ✅ Trang chủ với danh sách sản phẩm
│   ├── ProfileActivity.java        ✅ Hồ sơ người dùng
│   ├── ProductDetailActivity.java  📝 Chi tiết sản phẩm (cần tạo)
│   ├── CartActivity.java           📝 Giỏ hàng (cần tạo)
│   ├── OrderActivity.java          📝 Đặt hàng (cần tạo)
│   ├── OrderHistoryActivity.java   📝 Lịch sử đơn hàng (cần tạo)
│   └── SearchActivity.java         📝 Tìm kiếm (cần tạo)
│
├── 📁 models/              # Data Models (Entity)
│   ├── User.java                   ✅ Model người dùng
│   ├── Product.java                ✅ Model sản phẩm
│   ├── Category.java               ✅ Model danh mục
│   ├── CartItem.java               ✅ Model item giỏ hàng
│   ├── Order.java                  ✅ Model đơn hàng
│   ├── OrderItem.java              ✅ Model item đơn hàng
│   └── ApiResponse.java            ✅ Generic response từ API
│
├── 📁 viewmodels/          # ViewModels (MVVM)
│   ├── ProductViewModel.java       📝 ViewModel cho sản phẩm (cần tạo)
│   ├── UserViewModel.java          📝 ViewModel cho user (cần tạo)
│   ├── CartViewModel.java          📝 ViewModel cho giỏ hàng (cần tạo)
│   └── OrderViewModel.java         📝 ViewModel cho đơn hàng (cần tạo)
│
├── 📁 repositories/        # Repository Layer
│   ├── ProductRepository.java      ✅ Repository sản phẩm
│   ├── UserRepository.java         ✅ Repository người dùng
│   └── OrderRepository.java        ✅ Repository đơn hàng
│
├── 📁 network/             # Network Layer
│   ├── ApiClient.java              ✅ Retrofit client
│   └── ApiService.java             ✅ API endpoints interface
│
├── 📁 adapters/            # RecyclerView Adapters
│   ├── ProductAdapter.java         ✅ Adapter danh sách sản phẩm
│   ├── CartAdapter.java            📝 Adapter giỏ hàng (cần tạo)
│   └── OrderAdapter.java           📝 Adapter đơn hàng (cần tạo)
│
└── 📁 utils/               # Utility Classes
    ├── SharedPreferencesManager.java   ✅ Quản lý lưu trữ local
    ├── Constants.java                  ✅ Các hằng số
    ├── Validator.java                  ✅ Validate dữ liệu
    └── FormatUtils.java                ✅ Format giá, ngày tháng
```

---

## 🎯 Kiến Trúc MVVM

```
┌─────────────┐
│   Activity  │ ◄── User Interface
└──────┬──────┘
       │ observes
       ▼
┌─────────────┐
│  ViewModel  │ ◄── Business Logic
└──────┬──────┘
       │ calls
       ▼
┌─────────────┐
│ Repository  │ ◄── Data Source
└──────┬──────┘
       │ fetches
       ▼
┌─────────────┐
│  API/Local  │ ◄── Network/Storage
└─────────────┘
```

---

## 📦 Dependencies Đã Thêm

### Network
- **Retrofit 2.9.0** - REST API client
- **Gson 2.10.1** - JSON parsing
- **OkHttp 4.12.0** - HTTP client

### Architecture Components
- **ViewModel 2.7.0** - Quản lý UI data
- **LiveData 2.7.0** - Observable data holder
- **Room 2.6.1** - Local database (optional)

### UI Libraries
- **Glide 4.16.0** - Image loading
- **CircleImageView 3.1.0** - Avatar tròn
- **Material Design** - UI components
- **RecyclerView 1.3.2** - Danh sách
- **SwipeRefreshLayout 1.1.0** - Pull to refresh

---

## 🔐 Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

---

## 🚀 Tính Năng Chính

### ✅ Đã Hoàn Thành
- [x] Đăng nhập / Đăng ký
- [x] Hiển thị danh sách sản phẩm
- [x] Danh mục sản phẩm (iPhone, Samsung, Xiaomi)
- [x] Profile người dùng
- [x] Bottom Navigation

### 📝 Cần Phát Triển
- [ ] Chi tiết sản phẩm
- [ ] Giỏ hàng
- [ ] Đặt hàng
- [ ] Lịch sử đơn hàng
- [ ] Tìm kiếm sản phẩm
- [ ] Filter & Sort
- [ ] Payment integration
- [ ] Push notifications
- [ ] Reviews & Ratings

---

## 💡 Hướng Dẫn Sử Dụng

### 1. Cấu Hình API
Thay đổi `BASE_URL` trong `ApiClient.java`:
```java
private static final String BASE_URL = "https://your-api-url.com/api/";
```

### 2. Test Account
- Email: `admin@gmail.com`
- Password: `123456`

### 3. Build Project
```bash
./gradlew assembleDebug
```

---

## 📚 Best Practices

### Models
- Sử dụng `@SerializedName` cho JSON mapping
- Implement getters/setters đầy đủ
- Thêm helper methods khi cần

### Repositories
- Return `LiveData` để observe data
- Handle error cases
- Log requests/responses

### ViewModels
- Không giữ reference đến Context/View
- Expose `LiveData`, không phải `MutableLiveData`
- Clean up resources trong `onCleared()`

### Activities
- Observe LiveData từ ViewModel
- Không thực hiện business logic
- Update UI dựa trên data changes

---

## 🔧 Công Cụ Hữu Ích

### Utils Available
- `SharedPreferencesManager` - Lưu trữ local
- `Validator` - Validate email, phone, password
- `FormatUtils` - Format giá tiền, ngày tháng
- `Constants` - Hằng số toàn cục

### Example Usage

```java
// Validate email
if (Validator.isValidEmail(email)) {
    // Email hợp lệ
}

// Format giá
String price = FormatUtils.formatPrice(29990000); // "29.990.000₫"

// Lưu user data
SharedPreferencesManager.getInstance(this).saveUserData(user);

// Check login status
if (SharedPreferencesManager.getInstance(this).isLoggedIn()) {
    // User đã đăng nhập
}
```

---

## 📝 Notes

- File models đã được move vào package `models`
- File adapters đã được move vào package `adapters`
- File activities đã được move vào package `activities`
- Cần update imports trong các file sau khi di chuyển
- Cần sync Gradle sau khi thêm dependencies
- Lỗi compile sẽ biến mất sau khi sync Gradle thành công

---

## 👨‍💻 Next Steps

1. **Sync Gradle** - Tải dependencies mới
2. **Tạo ViewModels** - Implement business logic
3. **Tạo các Activity còn thiếu** - ProductDetail, Cart, Order
4. **Test API integration** - Kết nối backend thực tế
5. **UI/UX improvements** - Cải thiện giao diện
6. **Error handling** - Xử lý lỗi toàn diện
7. **Add loading states** - Thêm progress indicators
8. **Implement caching** - Sử dụng Room Database

---

**Created by:** TaoStore Team  
**Last Updated:** November 7, 2025
