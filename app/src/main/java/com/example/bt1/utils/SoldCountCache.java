package com.example.bt1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoldCountCache {
    private static final String PREF_NAME = "SoldCountCache";
    private static final String KEY_PREFIX = "sold_count_";
    private static final String KEY_LAST_UPDATE = "last_update_time";
    
    private final SharedPreferences prefs;
    private final FirebaseFirestore db;
    
    public SoldCountCache(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
    }
    
    // Lấy sold count từ cache (local)
    public int getSoldCount(long productId) {
        return prefs.getInt(KEY_PREFIX + productId, 0);
    }
    
    // Lưu sold count vào cache
    private void saveSoldCount(long productId, int count) {
        prefs.edit().putInt(KEY_PREFIX + productId, count).apply();
    }
    
    // Lấy thời gian update lần cuối
    public long getLastUpdateTime() {
        return prefs.getLong(KEY_LAST_UPDATE, 0);
    }
    
    // Cập nhật sold count từ Firebase (query đơn hàng hoàn thành)
    public void updateFromFirebase(OnUpdateListener listener) {
        db.collection("orders")
                .whereEqualTo("status", "Hoàn thành")
                .get()
                .addOnSuccessListener(orderSnapshots -> {
                    HashMap<Long, Integer> soldCountMap = new HashMap<>();
                    
                    // Đếm số lượng bán của từng sản phẩm
                    for (QueryDocumentSnapshot orderDoc : orderSnapshots) {
                        try {
                            List<HashMap<String, Object>> products = 
                                (List<HashMap<String, Object>>) orderDoc.get("products");
                            
                            if (products != null) {
                                for (HashMap<String, Object> productData : products) {
                                    try {
                                        Long productId = parseProductId(productData.get("id"));
                                        Integer quantity = parseQuantity(productData.get("quantity"));
                                        
                                        if (productId != null) {
                                            soldCountMap.put(productId, 
                                                soldCountMap.getOrDefault(productId, 0) + quantity);
                                        }
                                    } catch (Exception e) {
                                        Log.e("SoldCountCache", "Error parsing product in order", e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("SoldCountCache", "Error processing order: " + orderDoc.getId(), e);
                        }
                    }
                    
                    // Lưu vào cache
                    SharedPreferences.Editor editor = prefs.edit();
                    for (Map.Entry<Long, Integer> entry : soldCountMap.entrySet()) {
                        editor.putInt(KEY_PREFIX + entry.getKey(), entry.getValue());
                    }
                    editor.putLong(KEY_LAST_UPDATE, System.currentTimeMillis());
                    editor.apply();
                    
                    Log.d("SoldCountCache", "Updated sold counts for " + soldCountMap.size() + " products");
                    
                    if (listener != null) {
                        listener.onUpdateComplete(true, soldCountMap);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SoldCountCache", "Failed to update sold counts", e);
                    if (listener != null) {
                        listener.onUpdateComplete(false, null);
                    }
                });
    }
    
    // Helper methods để parse data từ Firebase
    private Long parseProductId(Object idObj) {
        if (idObj instanceof Long) {
            return (Long) idObj;
        } else if (idObj instanceof String) {
            try {
                return Long.parseLong((String) idObj);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (idObj instanceof Integer) {
            return ((Integer) idObj).longValue();
        }
        return null;
    }
    
    private Integer parseQuantity(Object qtyObj) {
        if (qtyObj instanceof Long) {
            return ((Long) qtyObj).intValue();
        } else if (qtyObj instanceof Integer) {
            return (Integer) qtyObj;
        } else if (qtyObj instanceof String) {
            try {
                return Integer.parseInt((String) qtyObj);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1; // default
    }
    
    // Clear cache
    public void clearCache() {
        prefs.edit().clear().apply();
        Log.d("SoldCountCache", "Cache cleared");
    }
    
    // Listener interface
    public interface OnUpdateListener {
        void onUpdateComplete(boolean success, HashMap<Long, Integer> soldCountMap);
    }
}
