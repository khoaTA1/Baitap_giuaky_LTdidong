package com.example.bt1.models;

/**
 * Model đại diện cho một item trong giỏ hàng
 */
public class CartItem {
    private int id;
    private Product product;
    private int quantity;
    private boolean isSelected; // Để chọn bỏ chọn khi thanh toán

    // Constructors
    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.isSelected = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Helper methods
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public double getTotalDiscountedPrice() {
        return product.getDiscountedPrice() * quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
}
