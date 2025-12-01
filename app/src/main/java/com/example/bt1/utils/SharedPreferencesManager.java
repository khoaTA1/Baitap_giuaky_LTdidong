package com.example.bt1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bt1.models.User;
import com.example.bt1.models.Product;
import com.example.bt1.models.Order;
import com.example.bt1.models.Notification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Quản lý SharedPreferences để lưu trữ dữ liệu local
 */
public class SharedPreferencesManager {
    private static final String PREF_NAME = "TaoStorePrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_USER_AVATAR = "user_avatar";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_RECENT_CATES = "user_recent_categories";

    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Lưu thông tin user sau khi đăng nhập
     */
    public void saveUserData(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        // Lưu userId dưới dạng String (Firebase document ID)
        editor.putString(KEY_USER_ID, String.valueOf(user.getId()));
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_ADDRESS, user.getAddress());
        editor.putString(KEY_USER_AVATAR, user.getAvatarUrl());
        editor.putString(KEY_USER_ROLE, user.getRole());

        Set<String> setString = new LinkedHashSet<>(user.getRecentCategries());
        editor.putStringSet(KEY_USER_RECENT_CATES, setString);
        editor.apply();
    }

    /**
     * Kiểm tra xem user đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy User ID (String - Firebase document ID)
     * Migration: Nếu userId cũ là Long, convert sang String
     */
    public String getUserId() {
        try {
            // Thử đọc như String trước
            return sharedPreferences.getString(KEY_USER_ID, null);
        } catch (ClassCastException e) {
            // Nếu lỗi, có nghĩa là dữ liệu cũ là Long, cần migrate
            try {
                long oldUserId = sharedPreferences.getLong(KEY_USER_ID, -1);
                if (oldUserId != -1) {
                    String newUserId = String.valueOf(oldUserId);
                    // Lưu lại dưới dạng String
                    sharedPreferences.edit()
                        .remove(KEY_USER_ID)
                        .putString(KEY_USER_ID, newUserId)
                        .apply();
                    return newUserId;
                }
            } catch (Exception ex) {
                // Ignore
            }
            return null;
        }
    }

    /**
     * Lấy email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Lấy tên user
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    /**
     * Lấy số điện thoại
     */
    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    /**
     * Lấy địa chỉ
     */
    public String getUserAddress() {
        return sharedPreferences.getString(KEY_USER_ADDRESS, "");
    }

    /**
     * Lấy avatar URL
     */
    public String getUserAvatar() {
        return sharedPreferences.getString(KEY_USER_AVATAR, "");
    }

    /**
     * Lấy role của user
     */
    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "user");
    }

    // lưu và lấy dánh sách danh mục gần đây của user
    public List<String> getRecentCates() {
        Set<String> setString = sharedPreferences.getStringSet(KEY_USER_RECENT_CATES, null);

        if (setString != null) {
            List<String> recentCates = new ArrayList<>(setString);

            return recentCates;
        } else {
            return null;
        }
    }

    public void insertRecentCate(Product product) {
        String newCate = product.getCategory();

        // Lấy danh sách hiện tại
        Set<String> stored = sharedPreferences.getStringSet(KEY_USER_RECENT_CATES, null);

        // Chuyển sang list để xử lý
        List<String> recentCategories = (stored == null)
                ? new ArrayList<>()
                : new ArrayList<>(stored);

        // Giới hạn tối đa 3 phần tử
        int maxSize = 3;

        // Nếu phần tử đã tồn tại → xoá để tránh trùng
        recentCategories.remove(newCate);

        // Dịch các phần tử xuống
        // và thêm phần tử mới vào đầu
        recentCategories.add(0, newCate);

        // Cắt danh sách về tối đa 3 phần tử
        if (recentCategories.size() > maxSize) {
            recentCategories = recentCategories.subList(0, maxSize);
        }

        // Lưu lại bằng Set (LinkedHashSet để giữ thứ tự)
        Set<String> setString = new LinkedHashSet<>(recentCategories);
        editor.putStringSet(KEY_USER_RECENT_CATES, setString).apply();

        Log.d(">>> SharedPrefManag", "Danh sách cate gần đây: " + recentCategories.size());

        /*
        List<String> recentCategories = new ArrayList<>(sharedPreferences.getStringSet(KEY_USER_RECENT_CATES, null));

        try {
            // nếu hiện tại chưa có cate gần đây nào, thì tạo mới
            if (recentCategories == null || recentCategories.isEmpty()) {
                Set<String> setString = new LinkedHashSet<>();
                setString.add(product.getCategory());
                editor.putStringSet(KEY_USER_RECENT_CATES, setString).apply();
                Log.d(">>> SharedPrefManag", "đã tạo mới danh sách danh mục gần đây");

                return;
            }

            // nếu đã tồn tại trong local thì đẩy các cate cũ xuống
            // và thêm cate mới nhất vào đầu
            for (int i = Math.min(recentCategories.size(), 2); i > 0; i--) {
                recentCategories.set(i, recentCategories.get(i - 1));
            }

            recentCategories.set(0, product.getCategory());

            Set<String> setString = new LinkedHashSet<>(recentCategories);
            editor.putStringSet(KEY_USER_RECENT_CATES, setString).apply();

            Log.d(">>> SharedPrefManag", "insert danh mục gần đây, danh sách hiện tại: " + setString.size());
        } catch (Exception e) {
            Log.e("!!! SharedPrefManag", "Lỗi:", e);
        }*/
    }

    /**
     * Lưu role của user
     */
    public void saveUserRole(String role) {
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    /**
     * Lấy đối tượng User từ SharedPreferences
     */
    public User getUser() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        String userIdStr = getUserId();
        user.setId(userIdStr != null ? Integer.parseInt(userIdStr) : -1);
        user.setEmail(getUserEmail());
        user.setFullName(getUserName());
        user.setPhone(getUserPhone());
        user.setAddress(getUserAddress());
        user.setAvatarUrl(getUserAvatar());
        user.setRole(getUserRole());
        return user;
    }

    /**
     * Cập nhật thông tin user
     */
    public void updateUser(User user) {
        saveUserData(user);
    }

    /**
     * Đăng xuất - xóa thông tin user
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Xóa dữ liệu user (alias của logout)
     */
    public void clearUserData() {
        logout();
    }

    /**
     * Lưu mật khẩu cho tài khoản (dùng cho đăng ký local)
     */
    public void savePassword(String email, String password) {
        editor.putString(email + "_password", password);
        editor.apply();
    }

    /**
     * Lấy mật khẩu đã lưu
     */
    public String getPassword(String email) {
        return sharedPreferences.getString(email + "_password", null);
    }

    /**
     * Lưu giỏ hàng (dạng JSON string)
     */
    public void saveCart(String cartJson) {
        editor.putString("cart_data", cartJson);
        editor.apply();
    }

    /**
     * Lấy giỏ hàng
     */
    public String getCart() {
        return sharedPreferences.getString("cart_data", "[]");
    }

    /**
     * Xóa giỏ hàng
     */
    public void clearCart() {
        editor.remove("cart_data");
        editor.apply();
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng
     */
    public java.util.List<com.example.bt1.models.Product> getCartProducts() {
        String json = getCart();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.ArrayList<com.example.bt1.models.Product>>() {}.getType();
        java.util.List<com.example.bt1.models.Product> products = gson.fromJson(json, type);
        return products != null ? products : new java.util.ArrayList<>();
    }

    /**
     * Lưu đơn hàng
     */
    public void addOrder(com.example.bt1.models.Order order) {
        String json = getOrders();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.ArrayList<com.example.bt1.models.Order>>() {}.getType();
        java.util.List<com.example.bt1.models.Order> orders = gson.fromJson(json, type);

        if (orders == null) {
            orders = new java.util.ArrayList<>();
        }

        orders.add(0, order); // Thêm vào đầu danh sách

        String updatedJson = gson.toJson(orders);
        editor.putString("orders_data", updatedJson);
        editor.apply();
    }

    /**
     * Lấy danh sách đơn hàng
     */
    public String getOrders() {
        return sharedPreferences.getString("orders_data", "[]");
    }

    /**
     * Lưu thông báo mới
     */
    public void addNotification(com.example.bt1.models.Notification notification) {
        String json = getNotifications();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.ArrayList<com.example.bt1.models.Notification>>() {}.getType();
        java.util.List<com.example.bt1.models.Notification> notifications = gson.fromJson(json, type);

        if (notifications == null) {
            notifications = new java.util.ArrayList<>();
        }

        notifications.add(0, notification); // Thêm vào đầu danh sách

        String updatedJson = gson.toJson(notifications);
        editor.putString("notifications_data", updatedJson);
        editor.apply();

        // Tăng số lượng thông báo chưa đọc
        incrementNewNotificationCount();
    }

    /**
     * Lấy danh sách thông báo
     */
    public String getNotifications() {
        return sharedPreferences.getString("notifications_data", "[]");
    }

    /**
     * Lấy số lượng thông báo mới
     */
    public int getNewNotificationCount() {
        return sharedPreferences.getInt("new_notification_count", 0);
    }

    /**
     * Tăng số lượng thông báo mới
     */
    private void incrementNewNotificationCount() {
        int currentCount = getNewNotificationCount();
        editor.putInt("new_notification_count", currentCount + 1);
        editor.apply();
    }

    /**
     * Reset số lượng thông báo mới (không xóa nội dung)
     */
    public void resetNotificationCount() {
        editor.putInt("new_notification_count", 0);
        editor.apply();
    }

    /**
     * Xóa tất cả thông báo (không dùng nữa - chỉ reset badge)
     */
    @Deprecated
    public void clearNotifications() {
        // Chỉ reset counter, không xóa nội dung
        resetNotificationCount();
    }
}
