package com.example.bt1.models;

import java.io.Serializable;

/**
 * Model đại diện cho mã giảm giá/voucher
 */
public class Voucher implements Serializable {
    
    private String id;              // Document ID trong Firestore
    private String code;            // Mã voucher (VD: "FREESHIP50", "SALE20")
    private int discountPercent;    // Phần trăm giảm giá (0-100)
    private double minOrderAmount;  // Đơn hàng tối thiểu để áp dụng
    private int totalQuantity;      // Tổng số lượng voucher
    private int usedCount;          // Số lượng đã sử dụng
    private boolean freeShip;       // Miễn phí ship hay không
    private double freeShipAmount;  // Số tiền ship được miễn phí (nếu freeShip = true)
    private boolean active;         // Còn hiệu lực hay không
    private String expiryDate;      // Ngày hết hạn (format: "dd/MM/yyyy")
    private String description;     // Mô tả voucher
    
    // Constructor mặc định (cần cho Firebase)
    public Voucher() {
        this.usedCount = 0;
        this.active = true;
        this.freeShip = false;
        this.freeShipAmount = 0;
    }
    
    // Constructor đầy đủ
    public Voucher(String code, int discountPercent, double minOrderAmount, 
                   int totalQuantity, boolean freeShip,
                   double freeShipAmount, String expiryDate, String description) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.minOrderAmount = minOrderAmount;
        this.totalQuantity = totalQuantity;
        this.usedCount = 0;
        this.freeShip = freeShip;
        this.freeShipAmount = freeShipAmount;
        this.active = true;
        this.expiryDate = expiryDate;
        this.description = description;
    }
    
    // Kiểm tra voucher có còn khả dụng không
    public boolean isAvailable() {
        return active && (usedCount < totalQuantity);
    }
    
    // Kiểm tra đơn hàng có đủ điều kiện áp dụng voucher không
    public boolean isEligible(double orderAmount) {
        return isAvailable() && (orderAmount >= minOrderAmount);
    }
    
    // Tính số tiền được giảm (không giới hạn tối đa)
    public double calculateDiscount(double orderAmount) {
        if (!isEligible(orderAmount)) {
            return 0;
        }
        
        // Giảm giá = tổng đơn hàng * phần trăm giảm (không có giới hạn tối đa)
        return orderAmount * discountPercent / 100.0;
    }
    
    // Tính tổng tiền được giảm (bao gồm cả free ship)
    public double calculateTotalDiscount(double orderAmount, double shippingFee) {
        double discount = calculateDiscount(orderAmount);
        
        // Nếu có free ship, trừ thêm phí ship
        if (freeShip) {
            discount += shippingFee;
        }
        
        return discount;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public int getDiscountPercent() {
        return discountPercent;
    }
    
    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    public double getMinOrderAmount() {
        return minOrderAmount;
    }
    
    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public int getUsedCount() {
        return usedCount;
    }
    
    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }
    
    public int getRemainingQuantity() {
        return totalQuantity - usedCount;
    }
    
    public boolean isFreeShip() {
        return freeShip;
    }

    public void setFreeShip(boolean freeShip) {
        this.freeShip = freeShip;
    }    public double getFreeShipAmount() {
        return freeShipAmount;
    }
    
    public void setFreeShipAmount(double freeShipAmount) {
        this.freeShipAmount = freeShipAmount;
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }    public String getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Voucher{" +
                "code='" + code + '\'' +
                ", discountPercent=" + discountPercent +
                ", minOrderAmount=" + minOrderAmount +
                ", remaining=" + getRemainingQuantity() +
                '}';
    }
}
