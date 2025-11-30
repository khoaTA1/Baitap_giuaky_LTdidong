# 🎫 MÃ GIẢM GIÁ MẪU (SAMPLE VOUCHERS)

## 📋 Danh sách 10 Voucher mẫu

### 1. WELCOME10 - Chào mừng khách hàng mới
```
Mã: WELCOME10
Giảm: 10%
Đơn tối thiểu: 50.000đ
Giảm tối đa: 20.000đ
Số lượng: 100
Miễn phí ship: Không
Hết hạn: 31/12/2025
Mô tả: Giảm 10% cho đơn hàng đầu tiên - Tối đa 20.000đ
```

### 2. SAVE20 - Tiết kiệm 20%
```
Mã: SAVE20
Giảm: 20%
Đơn tối thiểu: 100.000đ
Giảm tối đa: 50.000đ
Số lượng: 50
Miễn phí ship: Không
Hết hạn: 31/01/2026
Mô tả: Giảm 20% cho đơn từ 100k - Tối đa 50.000đ
```

### 3. FREESHIP30 - Miễn phí vận chuyển
```
Mã: FREESHIP30
Giảm: 0%
Đơn tối thiểu: 150.000đ
Giảm tối đa: 0đ
Số lượng: 200
Miễn phí ship: Có (30.000đ)
Hết hạn: 28/02/2026
Mô tả: Miễn phí ship 30k cho đơn từ 150k
```

### 4. MEGA30 - Siêu giảm giá
```
Mã: MEGA30
Giảm: 30%
Đơn tối thiểu: 200.000đ
Giảm tối đa: 100.000đ
Số lượng: 30
Miễn phí ship: Không
Hết hạn: 15/12/2025
Mô tả: Giảm 30% cho đơn từ 200k - Tối đa 100.000đ
```

### 5. COMBO25 - Combo deal
```
Mã: COMBO25
Giảm: 25%
Đơn tối thiểu: 300.000đ
Giảm tối đa: 80.000đ
Số lượng: 40
Miễn phí ship: Có (25.000đ)
Hết hạn: 31/03/2026
Mô tả: Giảm 25% + Miễn phí ship cho đơn từ 300k
```

### 6. HEALTH15 - Sức khỏe
```
Mã: HEALTH15
Giảm: 15%
Đơn tối thiểu: 80.000đ
Giảm tối đa: 30.000đ
Số lượng: 150
Miễn phí ship: Không
Hết hạn: 31/12/2025
Mô tả: Giảm 15% cho sản phẩm sức khỏe - Tối đa 30.000đ
```

### 7. FLASH50 - Flash Sale
```
Mã: FLASH50
Giảm: 50%
Đơn tối thiểu: 500.000đ
Giảm tối đa: 200.000đ
Số lượng: 10 (Giới hạn)
Miễn phí ship: Có (50.000đ)
Hết hạn: 10/12/2025
Mô tả: FLASH SALE - Giảm 50% + Free ship cho đơn từ 500k
```

### 8. NEWYEAR35 - Năm mới
```
Mã: NEWYEAR35
Giảm: 35%
Đơn tối thiểu: 250.000đ
Giảm tối đa: 120.000đ
Số lượng: 60
Miễn phí ship: Không
Hết hạn: 05/01/2026
Mô tả: Mừng năm mới - Giảm 35% cho đơn từ 250k
```

### 9. VIP40 - Khách hàng VIP (Đã tắt)
```
Mã: VIP40
Giảm: 40%
Đơn tối thiểu: 400.000đ
Giảm tối đa: 150.000đ
Số lượng: 20
Miễn phí ship: Có (40.000đ)
Hết hạn: 31/12/2025
Trạng thái: INACTIVE (Đã tắt)
Mô tả: VIP ONLY - Giảm 40% + Free ship cho đơn từ 400k
```

### 10. STUDENT12 - Ưu đãi sinh viên
```
Mã: STUDENT12
Giảm: 12%
Đơn tối thiểu: 60.000đ
Giảm tối đa: 25.000đ
Số lượng: 500
Miễn phí ship: Không
Hết hạn: 30/06/2026
Mô tả: Ưu đãi sinh viên - Giảm 12% cho đơn từ 60k
```

---

## 🔥 CÁCH NHẬP VÀO FIREBASE

### Phương pháp 1: Qua App (Khuyến nghị)
1. **Chạy app** và login với tài khoản **Admin**
2. Vào **Profile** → **"🎫 Quản lý Mã giảm giá"**
3. Click nút **"+"** để thêm voucher
4. Copy thông tin từ các voucher mẫu ở trên
5. Điền vào form và click **"Thêm"**

### Phương pháp 2: Firebase Console (Nhanh hơn)
1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **HealthyMulti**
3. Vào **Firestore Database**
4. Click **"Start collection"** → Nhập tên: `vouchers`
5. Click **"Auto-ID"** và copy JSON từ file `sample_vouchers.json`
6. Lặp lại cho tất cả 10 vouchers

### Phương pháp 3: Import Script (Cho Developer)
```javascript
// Run in Firebase Console
const vouchers = [
  {
    code: "WELCOME10",
    discountPercent: 10,
    minOrderAmount: 50000,
    maxDiscount: 20000,
    totalQuantity: 100,
    usedCount: 0,
    isFreeShip: false,
    freeShipAmount: 0,
    isActive: true,
    expiryDate: "31/12/2025",
    description: "Giảm 10% cho đơn hàng đầu tiên - Tối đa 20.000đ"
  },
  // ... (thêm các voucher khác)
];

// Import to Firestore
const db = firebase.firestore();
vouchers.forEach(voucher => {
  db.collection('vouchers').add(voucher)
    .then(() => console.log('Added:', voucher.code))
    .catch(err => console.error('Error:', err));
});
```

---

## 📊 PHÂN LOẠI VOUCHERS

### 🎁 Theo mức giảm giá:
- **Thấp (10-15%):** WELCOME10, STUDENT12, HEALTH15
- **Trung bình (20-30%):** SAVE20, COMBO25, MEGA30
- **Cao (35-50%):** NEWYEAR35, VIP40, FLASH50

### 💰 Theo giá trị đơn hàng:
- **Entry (50k-80k):** WELCOME10, STUDENT12, HEALTH15
- **Medium (100k-200k):** SAVE20, FREESHIP30, MEGA30
- **Premium (250k-500k):** COMBO25, NEWYEAR35, VIP40, FLASH50

### 🚚 Theo loại ưu đãi:
- **Discount only:** WELCOME10, SAVE20, MEGA30, HEALTH15, NEWYEAR35, STUDENT12
- **Free shipping only:** FREESHIP30
- **Combo (Discount + Free ship):** COMBO25, FLASH50, VIP40

### 🎯 Theo đối tượng:
- **Khách mới:** WELCOME10
- **Mọi người:** SAVE20, FREESHIP30, MEGA30, HEALTH15
- **Khách hàng lớn:** COMBO25, NEWYEAR35, FLASH50
- **VIP:** VIP40
- **Sinh viên:** STUDENT12

---

## 🧪 TEST SCENARIOS

### Test Case 1: Đơn hàng 75.000đ
- ✅ **WELCOME10:** Giảm 7.500đ (10%)
- ✅ **STUDENT12:** Giảm 9.000đ (12%)
- ✅ **HEALTH15:** Giảm 11.250đ (15%)
- ❌ **SAVE20:** Không đủ điều kiện (cần 100k)

### Test Case 2: Đơn hàng 180.000đ
- ✅ **SAVE20:** Giảm 36.000đ (20%)
- ✅ **FREESHIP30:** Free ship 30.000đ
- ✅ **HEALTH15:** Giảm 27.000đ (15%, max 30k)
- ❌ **MEGA30:** Không đủ điều kiện (cần 200k)

### Test Case 3: Đơn hàng 350.000đ
- ✅ **MEGA30:** Giảm 100.000đ (30%, max 100k)
- ✅ **COMBO25:** Giảm 80.000đ + Free ship 25k = 105.000đ
- ✅ **NEWYEAR35:** Giảm 120.000đ (35%, max 120k)
- ❌ **FLASH50:** Không đủ điều kiện (cần 500k)

### Test Case 4: Đơn hàng 550.000đ
- ✅ **FLASH50:** Giảm 200.000đ + Free ship 50k = 250.000đ (BEST!)
- ✅ **VIP40:** Không áp dụng (đã tắt - isActive: false)

---

## 💡 TIPS SỬ DỤNG

### Cho Admin:
1. **Bật/Tắt voucher linh hoạt:** Dùng switch thay vì xóa
2. **Flash Sale:** Tạo voucher số lượng ít (10-20), giảm cao (40-50%)
3. **Seasonal vouchers:** Tạo theo dịp (Tết, Black Friday...)
4. **A/B Testing:** Tạo 2 voucher tương tự, xem cái nào hiệu quả hơn

### Cho User:
1. **So sánh vouchers:** Xem voucher nào giảm nhiều nhất
2. **Kết hợp với Flash Sale sản phẩm:** Tiết kiệm tối đa
3. **Chú ý điều kiện:** Đơn tối thiểu, giảm tối đa
4. **Ưu tiên combo vouchers:** Giảm giá + Free ship

---

## 📈 STATISTICS (Giả định)

| Voucher Code | Used | Success Rate | Avg Order Value | Total Discount |
|--------------|------|--------------|-----------------|----------------|
| WELCOME10    | 0/100 | 0% | - | 0đ |
| SAVE20       | 0/50 | 0% | - | 0đ |
| FREESHIP30   | 0/200 | 0% | - | 0đ |
| MEGA30       | 0/30 | 0% | - | 0đ |
| COMBO25      | 0/40 | 0% | - | 0đ |
| HEALTH15     | 0/150 | 0% | - | 0đ |
| FLASH50      | 0/10 | 0% | - | 0đ |
| NEWYEAR35    | 0/60 | 0% | - | 0đ |
| VIP40        | 0/20 | N/A (Inactive) | - | 0đ |
| STUDENT12    | 0/500 | 0% | - | 0đ |

*(Số liệu sẽ tự động cập nhật khi có người dùng áp dụng)*

---

## 🔐 SECURITY NOTES

1. ✅ **Duplicate check:** Không thể tạo 2 voucher cùng code
2. ✅ **Quantity limit:** Kiểm tra totalQuantity vs usedCount
3. ✅ **Atomic increment:** usedCount tăng atomic, tránh race condition
4. ✅ **Eligibility check:** Kiểm tra minOrderAmount trước khi apply
5. ✅ **Max discount cap:** Giới hạn số tiền giảm tối đa

---

**Created:** November 29, 2025  
**Format:** Markdown (.md)  
**Purpose:** Sample data for voucher system testing  
**Status:** ✅ Ready to use
