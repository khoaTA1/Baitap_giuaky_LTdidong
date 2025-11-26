package com.example.bt1.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model đại diện cho đơn hàng
 */
public class Order {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("status")
    private String status; // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("phone")
    private String phone;

    @SerializedName("payment_method")
    private String paymentMethod; // COD, BANK_TRANSFER, CREDIT_CARD

    @SerializedName("items")
    private List<OrderItem> items;

    // Constructors
    public Order() {
    }

    public Order(int userId, double totalAmount, String shippingAddress, String phone, String paymentMethod) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Helper methods
    public String getStatusText() {
        switch (status) {
            case "PENDING":
                return "Chờ xác nhận";
            case "CONFIRMED":
                return "Đã xác nhận";
            case "SHIPPING":
                return "Đang giao hàng";
            case "DELIVERED":
                return "Đã giao hàng";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return status;
        }
    }
}
