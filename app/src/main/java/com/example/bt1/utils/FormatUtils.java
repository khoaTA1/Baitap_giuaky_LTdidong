package com.example.bt1.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class chứa các hàm tiện ích format dữ liệu
 */
public class FormatUtils {
    
    /**
     * Format giá tiền theo định dạng Việt Nam
     * Ví dụ: 29990000 -> 29.990.000₫
     */
    public static String formatPrice(double price) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.applyPattern("#,###");
        return formatter.format(price) + "₫";
    }
    
    /**
     * Format giá tiền với chữ "đ"
     */
    public static String formatPriceWithText(double price) {
        return formatPrice(price).replace("₫", " đ");
    }
    
    /**
     * Format số lượng
     */
    public static String formatQuantity(int quantity) {
        return "x" + quantity;
    }
    
    /**
     * Format ngày tháng
     * Ví dụ: 2025-11-07 14:30:00 -> 07/11/2025
     */
    public static String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    /**
     * Format ngày tháng đầy đủ
     * Ví dụ: 2025-11-07 14:30:00 -> 07/11/2025 14:30
     */
    public static String formatDateTime(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    /**
     * Format số điện thoại
     * Ví dụ: 0987654321 -> 098 765 4321
     */
    public static String formatPhone(String phone) {
        if (phone != null && phone.length() == 10) {
            return phone.substring(0, 3) + " " + phone.substring(3, 6) + " " + phone.substring(6);
        }
        return phone;
    }
    
    /**
     * Rút gọn văn bản
     * Ví dụ: "LéAna Ocavill 30 viên" -> "LéAna Ocavill..."
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
    
    /**
     * Format discount percent
     * Ví dụ: 20 -> -20%
     */
    public static String formatDiscount(int percent) {
        if (percent > 0) {
            return "-" + percent + "%";
        }
        return "";
    }
    
    /**
     * Format rating
     * Ví dụ: 4.5 -> 4.5★
     */
    public static String formatRating(float rating) {
        return String.format(Locale.getDefault(), "%.1f★", rating);
    }
    
    /**
     * Chuyển đổi số sang chữ (cho đơn vị tiền tệ)
     */
    public static String numberToWords(double number) {
        // Implementation simplified - có thể mở rộng thêm
        if (number >= 1_000_000_000) {
            return String.format(Locale.getDefault(), "%.1f tỷ", number / 1_000_000_000);
        } else if (number >= 1_000_000) {
            return String.format(Locale.getDefault(), "%.1f triệu", number / 1_000_000);
        } else if (number >= 1_000) {
            return String.format(Locale.getDefault(), "%.0f nghìn", number / 1_000);
        }
        return String.format(Locale.getDefault(), "%.0f", number);
    }
    
    /**
     * Lấy ngày hiện tại theo format
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
