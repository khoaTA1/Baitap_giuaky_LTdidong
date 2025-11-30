package com.example.bt1.models;

import java.util.List;

public class Order {
    private String orderId;
    private String orderDate;
    private double totalAmount;
    private String status;
    private List<Product> products;  // Đổi từ 'items' thành 'products' để khớp với Firebase
    private String deliveryAddress;  // Địa chỉ giao hàng
    private String voucherCode;      // Mã voucher đã sử dụng
    private double voucherDiscount;  // Số tiền giảm giá từ voucher

    // No-argument constructor required for Firebase Firestore deserialization
    public Order() {
    }

    public Order(String orderId, String orderDate, double totalAmount, String status, List<Product> products) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.products = products;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Product> getItems() {
        return products;  // Trả về products
    }

    public void setItems(List<Product> items) {
        this.products = items;  // Set vào products
    }

    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getVoucherCode() {
        return voucherCode;
    }
    
    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
    
    public double getVoucherDiscount() {
        return voucherDiscount;
    }
    
    public void setVoucherDiscount(double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }
}
