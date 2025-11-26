package com.example.bt1.models;

public class Voucher {
    private String code;
    private String description;
    private String expiryDate;

    public Voucher(String code, String description, String expiryDate) {
        this.code = code;
        this.description = description;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
