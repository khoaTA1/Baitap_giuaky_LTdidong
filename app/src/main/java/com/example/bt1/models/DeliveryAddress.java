package com.example.bt1.models;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

/**
 * Model đại diện cho địa chỉ giao hàng của người dùng
 */
public class DeliveryAddress implements Serializable {
    
    private String id; // Document ID from Firebase
    
    @PropertyName("user_id")
    private String userId;
    
    @PropertyName("recipient_name")
    private String recipientName; // Tên người nhận
    
    @PropertyName("phone_number")
    private String phoneNumber; // Số điện thoại người nhận
    
    @PropertyName("province")
    private String province; // Tỉnh/Thành phố
    
    @PropertyName("district")
    private String district; // Quận/Huyện
    
    @PropertyName("ward")
    private String ward; // Phường/Xã
    
    @PropertyName("detail_address")
    private String detailAddress; // Số nhà, tên đường
    
    @PropertyName("is_default")
    private Boolean isDefault = false; // Địa chỉ mặc định
    
    @PropertyName("label")
    private String label; // Nhãn địa chỉ (Nhà, Văn phòng, ...)
    
    @PropertyName("created_at")
    private Long createdAt; // Timestamp

    // Constructors
    public DeliveryAddress() {
    }

    public DeliveryAddress(String userId, String recipientName, String phoneNumber, 
                          String province, String district, String ward, 
                          String detailAddress, Boolean isDefault, String label) {
        this.userId = userId;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
        this.label = label;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("user_id")
    public String getUserId() {
        return userId;
    }

    @PropertyName("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("recipient_name")
    public String getRecipientName() {
        return recipientName;
    }

    @PropertyName("recipient_name")
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    @PropertyName("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @PropertyName("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @PropertyName("detail_address")
    public String getDetailAddress() {
        return detailAddress;
    }

    @PropertyName("detail_address")
    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    @PropertyName("is_default")
    public Boolean getIsDefault() {
        return isDefault != null ? isDefault : false;
    }

    @PropertyName("is_default")
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @PropertyName("created_at")
    public Long getCreatedAt() {
        return createdAt;
    }

    @PropertyName("created_at")
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Lấy địa chỉ đầy đủ dưới dạng chuỗi
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (detailAddress != null && !detailAddress.isEmpty()) {
            sb.append(detailAddress);
        }
        
        if (ward != null && !ward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward);
        }
        
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        
        if (province != null && !province.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(province);
        }
        
        return sb.toString();
    }

    /**
     * Lấy thông tin người nhận (Tên - SĐT)
     */
    public String getRecipientInfo() {
        StringBuilder sb = new StringBuilder();
        
        if (recipientName != null && !recipientName.isEmpty()) {
            sb.append(recipientName);
        }
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(phoneNumber);
        }
        
        return sb.length() > 0 ? sb.toString() : "";
    }
}
