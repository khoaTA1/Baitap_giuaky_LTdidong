package com.example.bt1.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Class chứa các phương thức validate dữ liệu
 */
public class Validator {
    
    /**
     * Validate email
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate mật khẩu
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return password.length() >= Constants.MIN_PASSWORD_LENGTH 
                && password.length() <= Constants.MAX_PASSWORD_LENGTH;
    }
    
    /**
     * Validate số điện thoại Việt Nam
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches(Constants.PHONE_PATTERN);
    }
    
    /**
     * Validate tên (không được rỗng và có độ dài hợp lệ)
     */
    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && trimmedName.length() <= 50;
    }
    
    /**
     * Validate địa chỉ
     */
    public static boolean isValidAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        return address.trim().length() >= 10;
    }
    
    /**
     * Kiểm tra xem mật khẩu có match không
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }
    
    /**
     * Validate giá tiền (phải lớn hơn 0)
     */
    public static boolean isValidPrice(double price) {
        return price > 0;
    }
    
    /**
     * Validate số lượng (phải lớn hơn 0)
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }
    
    /**
     * Lấy error message cho email
     */
    public static String getEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Email không được để trống";
        }
        if (!isValidEmail(email)) {
            return "Email không hợp lệ";
        }
        return null;
    }
    
    /**
     * Lấy error message cho mật khẩu
     */
    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Mật khẩu không được để trống";
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            return "Mật khẩu phải có ít nhất " + Constants.MIN_PASSWORD_LENGTH + " ký tự";
        }
        if (password.length() > Constants.MAX_PASSWORD_LENGTH) {
            return "Mật khẩu không được quá " + Constants.MAX_PASSWORD_LENGTH + " ký tự";
        }
        return null;
    }
    
    /**
     * Lấy error message cho số điện thoại
     */
    public static String getPhoneError(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "Số điện thoại không được để trống";
        }
        if (!isValidPhone(phone)) {
            return "Số điện thoại không hợp lệ (phải có 10 số và bắt đầu bằng 0)";
        }
        return null;
    }
    
    /**
     * Lấy error message cho tên
     */
    public static String getNameError(String name) {
        if (TextUtils.isEmpty(name)) {
            return "Tên không được để trống";
        }
        if (name.trim().length() < 2) {
            return "Tên phải có ít nhất 2 ký tự";
        }
        return null;
    }
}
