package com.example.bt1;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private long id;
    private String name;
    private String price;
    private int imageResId; // ID của ảnh trong drawable
    private String screenSize;
    private String memory;
    private String storage;

    public Product(String name, String price, int imageResId, String screenSize, String memory, String storage) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.screenSize = screenSize;
        this.memory = memory;
        this.storage = storage;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }
}
