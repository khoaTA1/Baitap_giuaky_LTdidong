package com.example.bt1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model đại diện cho sản phẩm điện thoại
 */
public class Product implements Serializable {
    private Long id;

    //@Column(nullable = false)
    private String name;
    //@Column(nullable = false)
    private String brand;

    //@Column(precision = 12, scale = 2, nullable = false)
    private double price;

    //@Column(nullable = false)
    private Integer stock;

    //@Builder.Default
    //private LocalDateTime createdAt = LocalDateTime.now();

    //private String imageUrl;
    private int imageResId;

    //@Column(name = "on_deal")
    //@Builder.Default
    private Boolean onDeal = false;

    //@Column(name = "deal_percentage")
    private Integer dealPercentage;

    //@Column(name = "is_active")
    //@Builder.Default
    private Boolean isActive = true;

    // --- Specs ---
    private String screenSize;
    private String displayTech;
    private String rearCamera;
    private String frontCamera;
    private String chipset;
    private String nfcSupport;
    private String ram;
    private String storage;
    private String battery;
    private String simType;
    private String os;
    private String resolution;
    private String displayFeatures;
    private String cpuSpecs;

    public Product() {
    }

    public Product(Long id, String name, String brand, double price, Integer stock, int imageResId, Boolean onDeal, Integer dealPercentage, Boolean isActive, String screenSize, String displayTech, String rearCamera, String frontCamera, String chipset, String nfcSupport, String ram, String storage, String battery, String simType, String os, String resolution, String displayFeatures, String cpuSpecs) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.imageResId = imageResId;
        this.onDeal = onDeal;
        this.dealPercentage = dealPercentage;
        this.isActive = isActive;
        this.screenSize = screenSize;
        this.displayTech = displayTech;
        this.rearCamera = rearCamera;
        this.frontCamera = frontCamera;
        this.chipset = chipset;
        this.nfcSupport = nfcSupport;
        this.ram = ram;
        this.storage = storage;
        this.battery = battery;
        this.simType = simType;
        this.os = os;
        this.resolution = resolution;
        this.displayFeatures = displayFeatures;
        this.cpuSpecs = cpuSpecs;
    }

}

//    @SerializedName("id")
//    private int id;
//
//    @SerializedName("name")
//    private String name;
//
//    @SerializedName("brand")
//    private String brand; // iPhone, Samsung, Xiaomi...
//
//    @SerializedName("price")
//    private double price;
//
//    @SerializedName("description")
//    private String description;
//
//    @SerializedName("image_url")
//    private String imageUrl;
//
//    @SerializedName("category_id")
//    private int categoryId;
//
//    @SerializedName("stock_quantity")
//    private int stockQuantity;
//
//    @SerializedName("discount_percent")
//    private int discountPercent;
//
//    @SerializedName("rating")
//    private float rating;
//
//    @SerializedName("specifications")
//    private String specifications;
//
//    // Local field (không từ API)
//    private int imageResId; // ID của ảnh trong drawable (dùng khi chưa có API)
//    private boolean isSelected; // For favorite selection
//
//    // Hot Sale specific fields
//    private String originalPrice; // Giá gốc trước khi giảm (dạng string formatted)
////    private int stockTotal; // Tổng số stock
////    private int stockSold; // Số lượng đã bán
////    private String soldCount; // Formatted sold count
//    private int reviewCount; // Số lượng review
//
//    // Constructors
//    public Product() {
//    }
//
//    // Constructor cho dữ liệu local (khi chưa có API)
//    public Product(String name, String priceStr, int imageResId) {
//        this.name = name;
//        this.imageResId = imageResId;
//        // Xử lý chuỗi giá tiền "29.990.000₫" -> double
//        try {
//            String cleanPrice = priceStr.replace(".", "").replace("₫", "").replace(",", "").trim();
//            this.price = Double.parseDouble(cleanPrice);
//        } catch (Exception e) {
//            this.price = 0;
//        }
//    }
//
//    public Product(int id, String name, String brand, double price, String imageUrl) {
//        this.id = id;
//        this.name = name;
//        this.brand = brand;
//        this.price = price;
//        this.imageUrl = imageUrl;
//    }
//
//    // Getters and Setters
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getBrand() {
//        return brand;
//    }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    // Getter cho compatibility với code cũ - trả về string formatted
//    public String getPriceString() {
//        return getPriceFormatted();
//    }
//
//    // Get actual numeric price
//    public double getPriceDouble() {
//        return price;
//    }
//
//    public String getPriceFormatted() {
//        // Format: 29.990.000₫
//        return String.format("%,.0f₫", price).replace(",", ".");
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public int getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(int categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public int getStockQuantity() {
//        return stockQuantity;
//    }
//
//    public void setStockQuantity(int stockQuantity) {
//        this.stockQuantity = stockQuantity;
//    }
//
//    public int getDiscountPercent() {
//        return discountPercent;
//    }
//
//    public void setDiscountPercent(int discountPercent) {
//        this.discountPercent = discountPercent;
//    }
//
//    public float getRating() {
//        return rating;
//    }
//
//    public void setRating(float rating) {
//        this.rating = rating;
//    }
//
//    public String getSpecifications() {
//        return specifications;
//    }
//
//    public void setSpecifications(String specifications) {
//        this.specifications = specifications;
//    }
//
//    public int getImageResId() {
//        return imageResId;
//    }
//
//    public void setImageResId(int imageResId) {
//        this.imageResId = imageResId;
//    }
//
//    // Hot Sale getters and setters
//    public String getOriginalPrice() {
//        return originalPrice;
//    }
//
//    public void setOriginalPrice(String originalPrice) {
//        this.originalPrice = originalPrice;
//    }
//
////    public String getSoldCount() {
////        return soldCount;
////    }
////
////    public void setSoldCount(String soldCount) {
////        this.soldCount = soldCount;
////    }
////
////    public int getStockSold() {
////        return stockSold;
////    }
////
////    public void setStockSold(int stockSold) {
////        this.stockSold = stockSold;
////    }
////
////    public int getStockTotal() {return stockTotal;}
////
////    public void setStockTotal(int stockTotal) {
////        this.stockTotal = stockTotal;
////    }
//
//    // Selection methods for favorites
//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }
//
//    // Review count methods
//    public int getReviewCount() {
//        return reviewCount;
//    }
//
//    public void setReviewCount(int reviewCount) {
//        this.reviewCount = reviewCount;
//    }
//
//    // Helper methods
//    public boolean isInStock() {
//        return stockQuantity > 0;
//    }
//
//    public double getDiscountedPrice() {
//        if (discountPercent > 0) {
//            return price * (100 - discountPercent) / 100.0;
//        }
//        return price;
//    }
//
////    public int getStockProgress() {
////        if (stockTotal > 0) {
////            return (stockSold * 100) / stockTotal;
////        }
////        return 0;
////    }
//}
