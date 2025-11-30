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
    private double maxDiscount;     // Giảm tối đa bao nhiêu tiền
    private int totalQuantity;      // Tổng số lượng voucher
    private int usedCount;          // Số lượng đã sử dụng
    private boolean isFreeShip;     // Miễn phí ship hay không
    private double freeShipAmount;  // Số tiền ship được miễn phí (nếu isFreeShip = true)
    private boolean isActive;       // Còn hiệu lực hay không
    private String expiryDate;      // Ngày hết hạn (format: "dd/MM/yyyy")
    private String description;     // Mô tả voucher
    
    // Constructor mặc định (cần cho Firebase)
    public Voucher() {
        this.usedCount = 0;
        this.isActive = true;
        this.isFreeShip = false;
        this.freeShipAmount = 0;
    }
    
    // Constructor đầy đủ
    public Voucher(String code, int discountPercent, double minOrderAmount, 
                   double maxDiscount, int totalQuantity, boolean isFreeShip,
                   double freeShipAmount, String expiryDate, String description) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscount = maxDiscount;
        this.totalQuantity = totalQuantity;
        this.usedCount = 0;
        this.isFreeShip = isFreeShip;
        this.freeShipAmount = freeShipAmount;
        this.isActive = true;
        this.expiryDate = expiryDate;
        this.description = description;
    }
    
    // Kiểm tra voucher có còn khả dụng không
    public boolean isAvailable() {
        return isActive && (usedCount < totalQuantity);
    }
    
    // Kiểm tra đơn hàng có đủ điều kiện áp dụng voucher không
    public boolean isEligible(double orderAmount) {
        return isAvailable() && (orderAmount >= minOrderAmount);
    }
    
    // Tính số tiền được giảm
    public double calculateDiscount(double orderAmount) {
        if (!isEligible(orderAmount)) {
            return 0;
        }
        
        double discount = orderAmount * discountPercent / 100.0;
        
        // Giới hạn giảm tối đa
        if (maxDiscount > 0 && discount > maxDiscount) {
            discount = maxDiscount;
        }
        
        return discount;
    }
    
    // Tính tổng tiền được giảm (bao gồm cả free ship)
    public double calculateTotalDiscount(double orderAmount, double shippingFee) {
        double discount = calculateDiscount(orderAmount);
        
        if (isFreeShip && freeShipAmount > 0) {
            // Giảm phí ship (tối đa bằng phí ship thực tế)
            discount += Math.min(freeShipAmount, shippingFee);
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
    
    public double getMaxDiscount() {
        return maxDiscount;
    }
    
    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
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
        return isFreeShip;
    }

    public void setFreeShip(boolean freeShip) {
        isFreeShip = freeShip;
    }    public double getFreeShipAmount() {
        return freeShipAmount;
    }
    
    public void setFreeShipAmount(double freeShipAmount) {
        this.freeShipAmount = freeShipAmount;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
