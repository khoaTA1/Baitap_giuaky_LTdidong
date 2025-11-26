package com.example.bt1.repositories;

import android.util.Log;

import com.example.bt1.models.Order;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepo {
    
    private FirebaseFirestore db;
    private static final String TAG = ">>> OrderRepo";
    
    public OrderRepo() {
        db = FirebaseFirestore.getInstance();
    }
    
    /**
     * Tạo đơn hàng mới trong Firebase
     * @param userId ID của user (String to match Firebase document ID)
     * @param order Thông tin đơn hàng
     * @param callback Callback khi hoàn thành
     */
    public void createOrder(String userId, Order order, OnCompleteListener callback) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("orderId", order.getOrderId());
        orderData.put("orderDate", order.getOrderDate());
        orderData.put("totalAmount", order.getTotalAmount());
        orderData.put("status", order.getStatus());
        orderData.put("products", order.getProducts()); // Lưu cả danh sách products
        
        Log.d(TAG, "Creating order with userId: " + userId + ", orderId: " + order.getOrderId());
        
        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Order created successfully - Document ID: " + documentReference.getId() + ", userId: " + userId);
                if (callback != null) {
                    callback.onComplete(true, "Đơn hàng đã được tạo thành công");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error creating order", e);
                if (callback != null) {
                    callback.onComplete(false, "Lỗi khi tạo đơn hàng: " + e.getMessage());
                }
            });
    }
    
    /**
     * Lấy danh sách đơn hàng của user
     * @param userId ID của user (String to match Firebase document ID)
     * @param callback Callback trả về danh sách orders
     */
    public void getOrdersByUserId(String userId, OnDataListener callback) {
        Log.d(TAG, "Querying orders for userId: " + userId);
        
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Order> orders = new ArrayList<>();
                
                Log.d(TAG, "Query returned " + queryDocumentSnapshots.size() + " documents");
                
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    try {
                        Log.d(TAG, "Processing document: " + doc.getId() + ", userId in doc: " + doc.getString("userId"));
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            orders.add(order);
                            Log.d(TAG, "Order parsed: orderId=" + order.getOrderId());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing order: " + doc.getId(), e);
                    }
                }
                
                Log.d(TAG, "Successfully loaded " + orders.size() + " orders for user " + userId);
                if (callback != null) {
                    callback.onDataLoaded(orders);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading orders for userId: " + userId, e);
                if (callback != null) {
                    callback.onDataLoaded(new ArrayList<>());
                }
            });
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     * @param orderId ID của đơn hàng
     * @param newStatus Trạng thái mới
     * @param callback Callback khi hoàn thành
     */
    public void updateOrderStatus(String orderId, String newStatus, OnCompleteListener callback) {
        db.collection("orders")
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    
                    doc.getReference().update("status", newStatus)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Order status updated: " + orderId);
                            if (callback != null) {
                                callback.onComplete(true, "Cập nhật trạng thái thành công");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating order status", e);
                            if (callback != null) {
                                callback.onComplete(false, "Lỗi cập nhật: " + e.getMessage());
                            }
                        });
                } else {
                    if (callback != null) {
                        callback.onComplete(false, "Không tìm thấy đơn hàng");
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error finding order", e);
                if (callback != null) {
                    callback.onComplete(false, "Lỗi: " + e.getMessage());
                }
            });
    }
    
    /**
     * Xóa đơn hàng
     * @param orderId ID của đơn hàng
     * @param callback Callback khi hoàn thành
     */
    public void deleteOrder(String orderId, OnCompleteListener callback) {
        db.collection("orders")
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    
                    doc.getReference().delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Order deleted: " + orderId);
                            if (callback != null) {
                                callback.onComplete(true, "Xóa đơn hàng thành công");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error deleting order", e);
                            if (callback != null) {
                                callback.onComplete(false, "Lỗi xóa: " + e.getMessage());
                            }
                        });
                } else {
                    if (callback != null) {
                        callback.onComplete(false, "Không tìm thấy đơn hàng");
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error finding order", e);
                if (callback != null) {
                    callback.onComplete(false, "Lỗi: " + e.getMessage());
                }
            });
    }
    
    // Callback interfaces
    public interface OnCompleteListener {
        void onComplete(boolean success, String message);
    }
    
    public interface OnDataListener {
        void onDataLoaded(List<Order> orders);
    }
}
