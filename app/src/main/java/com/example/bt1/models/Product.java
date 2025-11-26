package com.example.bt1.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Product implements Serializable {

    private long id;
    private String name;
    private String brand;
    private double price;
    private Integer stock;

    private String imageUrl;
    private String imagePath;
   // private int imageResId;
    private Boolean onDeal = false;
    private Integer dealPercentage;
    private Boolean isActive = true;
    private String original;

    // danh mục
    private String category;

    // dạng bào chế
    private String dosageForm;

    // quy cách
    private String include;

    // thành phần
    private String ingredient;

    // cách dùng
    private String use;

    // tác dụng phụ
    private String sideEffects;

    // đối tượng sử dụng
    private String object;

    // Methods for compatibility với HotSaleAdapter và các adapter khác
    private String originalPrice;
    private Integer discountPercent;
    private Float rating;
    private String description;
    private int quantity;
    // constructors
    public Product() {
    }

    public Product(String name, String brand, double price, Integer stock,
                   String imageUrl, Boolean onDeal, Integer discountPercent,
                   Boolean isActive, String original, String category, String dosageForm,
                   String include, String ingredient, Float rating, String description,
                   String use, String sideEffects, String object) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.onDeal = onDeal;
        this.discountPercent = discountPercent;
        this.isActive = isActive;
        this.original = original;
        this.category = category;
        this.dosageForm = dosageForm;
        this.include = include;
        this.ingredient = ingredient;
        this.rating = rating;
        this.description = description;
        this.use = use;
        this.sideEffects = sideEffects;
        this.object = object;
    }

    // getters, setters
    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /*
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }*/

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Boolean getOnDeal() {
        return onDeal;
    }

    public void setOnDeal(Boolean onDeal) {
        this.onDeal = onDeal;
    }

    public Integer getDealPercentage() {
        return dealPercentage;
    }

    public void setDealPercentage(Integer dealPercentage) {
        this.dealPercentage = dealPercentage;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Float getRating() {
        return rating != null ? rating : Float.valueOf(0f);
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /*
    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }*/

    public String getPriceFormatted() {
        // Format: 29.990.000₫
        return String.format("%,.0f₫", price).replace(",", ".");
    }
    // Helper methods
    public boolean isInStock() {
        return stock != null && stock > 0;
    }

    public double getDiscountedPrice() {
        if (discountPercent != null && discountPercent > 0) {
            return price * (100 - discountPercent) / 100.0;
        }
        return price;
    }


}
