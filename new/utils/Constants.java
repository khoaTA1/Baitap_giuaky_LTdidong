package com.example.bt1.utils;

/**
 * Class chứa các hằng số sử dụng trong app
 */
public class Constants {
    
    // API Constants
    public static final String BASE_URL = "https://api.taostore.com/api/";
    public static final int CONNECTION_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "TaoStorePrefs";
    public static final String KEY_USER_DATA = "user_data";
    public static final String KEY_CART_DATA = "cart_data";
    
    // Request Codes
    public static final int REQUEST_LOGIN = 1001;
    public static final int REQUEST_REGISTER = 1002;
    public static final int REQUEST_PRODUCT_DETAIL = 1003;
    public static final int REQUEST_CHECKOUT = 1004;
    
    // Intent Extra Keys
    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";
    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";
    public static final String EXTRA_ORDER_ID = "ORDER_ID";
    public static final String EXTRA_CATEGORY_ID = "CATEGORY_ID";
    
    // Product Brands
    public static final String BRAND_IPHONE = "iPhone";
    public static final String BRAND_SAMSUNG = "Samsung";
    public static final String BRAND_XIAOMI = "Xiaomi";
    public static final String BRAND_OPPO = "OPPO";
    public static final String BRAND_VIVO = "Vivo";
    
    // Order Status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_SHIPPING = "SHIPPING";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    
    // Payment Methods
    public static final String PAYMENT_COD = "COD";
    public static final String PAYMENT_BANK_TRANSFER = "BANK_TRANSFER";
    public static final String PAYMENT_CREDIT_CARD = "CREDIT_CARD";
    public static final String PAYMENT_MOMO = "MOMO";
    public static final String PAYMENT_ZALOPAY = "ZALOPAY";
    
    // Error Messages
    public static final String ERROR_NETWORK = "Lỗi kết nối mạng. Vui lòng thử lại.";
    public static final String ERROR_SERVER = "Lỗi server. Vui lòng thử lại sau.";
    public static final String ERROR_UNKNOWN = "Đã xảy ra lỗi không xác định.";
    public static final String ERROR_EMPTY_CART = "Giỏ hàng trống.";
    public static final String ERROR_OUT_OF_STOCK = "Sản phẩm đã hết hàng.";
    
    // Success Messages
    public static final String SUCCESS_LOGIN = "Đăng nhập thành công!";
    public static final String SUCCESS_REGISTER = "Đăng ký thành công!";
    public static final String SUCCESS_ADD_TO_CART = "Đã thêm vào giỏ hàng!";
    public static final String SUCCESS_ORDER_CREATED = "Đặt hàng thành công!";
    public static final String SUCCESS_UPDATE_PROFILE = "Cập nhật thông tin thành công!";
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String PHONE_PATTERN = "^(0[0-9]{9})$"; // Vietnam phone format
    
    // Pagination
    public static final int PAGE_SIZE = 20;
    
    // Image Loading
    public static final int IMAGE_PLACEHOLDER = android.R.drawable.ic_menu_gallery;
    public static final int IMAGE_ERROR = android.R.drawable.ic_menu_report_image;
    
    // Default Values
    public static final String DEFAULT_AVATAR = "https://ui-avatars.com/api/?name=User&background=random";
    
    // Test Account (for development)
    public static final String TEST_EMAIL = "admin@gmail.com";
    public static final String TEST_PASSWORD = "123456";
}
