# 🔧 Hướng Dẫn Sửa Lỗi & Build Project

## ❌ Lỗi Đã Sửa

### Lỗi: `package R does not exist`

**Nguyên nhân:**
- Sau khi di chuyển các file Activity vào package `activities`, cần import lại class R
- AndroidManifest.xml chưa cập nhật đường dẫn MainActivity mới

**Giải pháp đã áp dụng:**

1. ✅ **Thêm import R vào các Activity:**
```java
import com.example.bt1.R;
```

2. ✅ **Cập nhật AndroidManifest.xml:**
```xml
<!-- Trước -->
<activity android:name=".MainActivity" ...>

<!-- Sau -->
<activity android:name=".activities.MainActivity" ...>
```

3. ✅ **Clean & Rebuild project:**
```bash
.\gradlew --stop
.\gradlew clean
.\gradlew build
```

---

## 📋 Checklist Build Success

### Bước 1: Sync Gradle (trong Android Studio)
```
File → Sync Project with Gradle Files
```
Hoặc nhấn icon "Sync" trên toolbar

### Bước 2: Clean Project
```
Build → Clean Project
```

### Bước 3: Rebuild Project
```
Build → Rebuild Project
```

### Bước 4: Kiểm tra lỗi
```
Build → Make Project (Ctrl+F9)
```

---

## 🔍 Các File Đã Cập Nhật

### 1. MainActivity.java ✅
- Package: `com.example.bt1.activities`
- Import: `com.example.bt1.R`
- Location: `activities/MainActivity.java`

### 2. HomeActivity.java ✅
- Package: `com.example.bt1.activities`
- Import: `com.example.bt1.R`, `models.Product`, `adapters.ProductAdapter`
- Location: `activities/HomeActivity.java`

### 3. ProfileActivity.java ✅
- Package: `com.example.bt1.activities`
- Import: `com.example.bt1.R`
- Location: `activities/ProfileActivity.java`

### 4. RegisterActivity.java ✅
- Package: `com.example.bt1.activities`
- Import: `com.example.bt1.R`
- Location: `activities/RegisterActivity.java`

### 5. ProductAdapter.java ✅
- Package: `com.example.bt1.adapters`
- Import: `com.example.bt1.R`, `models.Product`
- Location: `adapters/ProductAdapter.java`

### 6. Product.java ✅
- Package: `com.example.bt1.models`
- Location: `models/Product.java`

### 7. AndroidManifest.xml ✅
- MainActivity path: `.activities.MainActivity`
- Thêm permissions: INTERNET, ACCESS_NETWORK_STATE

### 8. build.gradle.kts ✅
- Thêm dependencies: Retrofit, Gson, ViewModel, LiveData, Room, Glide

---

## 🚨 Lỗi Thường Gặp & Cách Xử Lý

### Lỗi: "Cannot resolve symbol 'R'"
**Giải pháp:**
1. File → Invalidate Caches → Invalidate and Restart
2. Build → Clean Project
3. Build → Rebuild Project

### Lỗi: "Unresolved reference: models/adapters/activities"
**Giải pháp:**
- Kiểm tra package declaration ở đầu file
- Đảm bảo import đúng package
- Sync Gradle

### Lỗi: "Duplicate class found"
**Giải pháp:**
- Xóa file cũ nếu còn tồn tại ở cả 2 nơi
- Clean project

### Lỗi compile dependencies
**Giải pháp:**
```bash
.\gradlew clean build --refresh-dependencies
```

---

## 📱 Test Build

### 1. Build APK Debug
```bash
.\gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Install trên thiết bị
```bash
.\gradlew installDebug
```

### 3. Run app
- Nhấn Run (Shift+F10) trong Android Studio
- Hoặc: `.\gradlew installDebug` rồi mở app trên thiết bị

---

## 🎯 Kiểm Tra Hoạt Động

### Test Login:
- Email: `admin@gmail.com`
- Password: `123456`

### Test Register:
- Tạo tài khoản mới
- Kiểm tra lưu vào SharedPreferences
- Đăng nhập bằng tài khoản vừa tạo

### Test Home:
- Hiển thị danh sách sản phẩm
- Click vào danh mục (iPhone, Samsung, Xiaomi)
- Bottom navigation

### Test Profile:
- Hiển thị thông tin user
- Nút đăng xuất

---

## 📊 Cấu Trúc Final

```
com.example.bt1/
├── activities/
│   ├── MainActivity.java          ✅
│   ├── HomeActivity.java          ✅
│   ├── ProfileActivity.java       ✅
│   └── RegisterActivity.java      ✅
├── adapters/
│   └── ProductAdapter.java        ✅
├── models/
│   ├── Product.java               ✅
│   ├── User.java                  ✅
│   ├── Category.java              ✅
│   ├── CartItem.java              ✅
│   ├── Order.java                 ✅
│   ├── OrderItem.java             ✅
│   └── ApiResponse.java           ✅
├── repositories/
│   ├── ProductRepository.java     ✅
│   ├── UserRepository.java        ✅
│   └── OrderRepository.java       ✅
├── network/
│   ├── ApiClient.java             ✅
│   └── ApiService.java            ✅
└── utils/
    ├── SharedPreferencesManager.java  ✅
    ├── Constants.java                 ✅
    ├── Validator.java                 ✅
    └── FormatUtils.java               ✅
```

---

## 💡 Tips

1. **Luôn sync Gradle** sau khi thay đổi dependencies
2. **Clean project** khi có lỗi lạ về R class
3. **Invalidate Caches** nếu IDE không nhận diện code mới
4. **Check logcat** khi app crash để debug
5. **Sử dụng TODO comments** để đánh dấu code cần hoàn thiện

---

## 🎉 Kết Quả Mong Đợi

Sau khi build thành công:
- ✅ Không có lỗi compile
- ✅ App chạy được trên emulator/device
- ✅ Tất cả Activity hoạt động bình thường
- ✅ Navigation giữa các màn hình mượt mà
- ✅ Cấu trúc code clean và dễ maintain

---

**Build Status:** 🔄 Building...  
**Last Updated:** November 7, 2025
