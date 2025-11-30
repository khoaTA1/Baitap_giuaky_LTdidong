# HƯỚNG DẪN SỬA VOUCHER TRONG FIREBASE

## ⚠️ VẤN ĐỀ

Voucher hiện tại trong Firebase có cấu trúc SAI:
- ❌ Thiếu field: `isActive`
- ❌ Dùng `remainingQuantity` thay vì `totalQuantity` + `usedCount`

## ✅ GIẢI PHÁP

### Cách 1: Sửa trực tiếp trong Firebase Console

1. Click vào document `ugOR5Mw13GvdGGRSHdhf`
2. **Xóa field:** `remainingQuantity: 10`
3. **Thêm các field sau:**

```
isActive: true (boolean)
totalQuantity: 10 (number)
usedCount: 0 (number)
```

4. Sửa field `id` từ `null` thành xóa field này đi (app sẽ tự set)
5. Click **Save**

### Cách 2: Xóa và tạo lại document đúng

1. **XÓA document hiện tại**
2. Click **Add document** → **Auto-ID**
3. **Copy paste JSON này:**

```json
{
  "code": "SAVE20",
  "discountPercent": 20,
  "minOrderAmount": 100000,
  "maxDiscount": 50000,
  "totalQuantity": 10,
  "usedCount": 0,
  "isFreeShip": false,
  "freeShipAmount": 0,
  "isActive": true,
  "expiryDate": "30/11/2025",
  "description": "Giảm 20% cho đơn từ 100k - Tối đa 50.000đ"
}
```

4. Click **Save**

## 📋 CẤU TRÚC ĐÚNG CỦA VOUCHER

```
code: "SAVE20" (string)
discountPercent: 20 (number)
minOrderAmount: 100000 (number)
maxDiscount: 50000 (number)
totalQuantity: 10 (number)         ← QUAN TRỌNG
usedCount: 0 (number)              ← QUAN TRỌNG
isFreeShip: false (boolean)
freeShipAmount: 0 (number)
isActive: true (boolean)           ← QUAN TRỌNG
expiryDate: "30/11/2025" (string)
description: "..." (string)
```

## ⚡ SAU KHI SỬA

1. Reload app
2. Vào Payment
3. Click "Chọn mã giảm giá"
4. Voucher sẽ hiển thị! ✅

---

**Lỗi chính:** Firebase document thiếu field `isActive` nên query `whereEqualTo("isActive", true)` không tìm thấy gì!
