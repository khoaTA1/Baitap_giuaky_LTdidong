package com.example.bt1.repositories;

import android.util.Log;

import com.example.bt1.models.Voucher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

public class VoucherRepo {
    
    private static final String COLLECTION_NAME = "vouchers";
    private final FirebaseFirestore db;
    
    public interface VoucherCallback {
        void onSuccess(List<Voucher> vouchers);
        void onFailure(String error);
    }
    
    public interface SingleVoucherCallback {
        void onSuccess(Voucher voucher);
        void onFailure(String error);
    }
    
    public interface OperationCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
    
    public VoucherRepo() {
        db = FirebaseFirestore.getInstance();
    }
    
    // Thêm voucher mới
    public void addVoucher(Voucher voucher, OperationCallback callback) {
        // Kiểm tra mã voucher đã tồn tại chưa
        db.collection(COLLECTION_NAME)
            .whereEqualTo("code", voucher.getCode())
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    callback.onFailure("Mã voucher đã tồn tại");
                } else {
                    // Thêm voucher mới
                    db.collection(COLLECTION_NAME)
                        .add(voucher)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("VoucherRepo", "Added voucher: " + documentReference.getId());
                            callback.onSuccess("Thêm voucher thành công");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("VoucherRepo", "Error adding voucher", e);
                            callback.onFailure("Lỗi: " + e.getMessage());
                        });
                }
            })
            .addOnFailureListener(e -> {
                callback.onFailure("Lỗi kiểm tra voucher: " + e.getMessage());
            });
    }
    
    // Lấy tất cả voucher
    public void getAllVouchers(VoucherCallback callback) {
        db.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Voucher> vouchers = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Voucher voucher = doc.toObject(Voucher.class);
                    voucher.setId(doc.getId());
                    vouchers.add(voucher);
                }
                Log.d("VoucherRepo", "Loaded " + vouchers.size() + " vouchers");
                callback.onSuccess(vouchers);
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error loading vouchers", e);
                callback.onFailure("Lỗi tải voucher: " + e.getMessage());
            });
    }
    
    // Lấy voucher còn khả dụng (cho người dùng)
    public void getAvailableVouchers(VoucherCallback callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("active", true)  // Firebase lưu isActive thành active
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Voucher> vouchers = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Voucher voucher = doc.toObject(Voucher.class);
                    voucher.setId(doc.getId());
                    // Chỉ thêm voucher còn số lượng
                    if (voucher.isAvailable()) {
                        vouchers.add(voucher);
                    }
                }
                Log.d("VoucherRepo", "Loaded " + vouchers.size() + " available vouchers");
                callback.onSuccess(vouchers);
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error loading available vouchers", e);
                callback.onFailure("Lỗi tải voucher: " + e.getMessage());
            });
    }
    
    // Lấy voucher theo mã
    public void getVoucherByCode(String code, SingleVoucherCallback callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (querySnapshot.isEmpty()) {
                    callback.onFailure("Mã voucher không tồn tại");
                } else {
                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                    Voucher voucher = doc.toObject(Voucher.class);
                    voucher.setId(doc.getId());
                    callback.onSuccess(voucher);
                }
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error finding voucher by code", e);
                callback.onFailure("Lỗi: " + e.getMessage());
            });
    }
    
    // Cập nhật voucher
    public void updateVoucher(Voucher voucher, OperationCallback callback) {
        if (voucher.getId() == null || voucher.getId().isEmpty()) {
            callback.onFailure("ID voucher không hợp lệ");
            return;
        }
        
        db.collection(COLLECTION_NAME)
            .document(voucher.getId())
            .set(voucher)
            .addOnSuccessListener(aVoid -> {
                Log.d("VoucherRepo", "Updated voucher: " + voucher.getId());
                callback.onSuccess("Cập nhật voucher thành công");
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error updating voucher", e);
                callback.onFailure("Lỗi: " + e.getMessage());
            });
    }
    
    // Xóa voucher
    public void deleteVoucher(String voucherId, OperationCallback callback) {
        db.collection(COLLECTION_NAME)
            .document(voucherId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d("VoucherRepo", "Deleted voucher: " + voucherId);
                callback.onSuccess("Xóa voucher thành công");
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error deleting voucher", e);
                callback.onFailure("Lỗi: " + e.getMessage());
            });
    }
    
    // Tăng số lượng đã sử dụng (khi apply voucher)
    public void incrementUsedCount(String voucherId, OperationCallback callback) {
        db.collection(COLLECTION_NAME)
            .document(voucherId)
            .update("usedCount", FieldValue.increment(1))
            .addOnSuccessListener(aVoid -> {
                Log.d("VoucherRepo", "Incremented used count for voucher: " + voucherId);
                callback.onSuccess("Áp dụng voucher thành công");
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error incrementing used count", e);
                callback.onFailure("Lỗi: " + e.getMessage());
            });
    }
    
    // Bật/tắt trạng thái active của voucher
    public void toggleVoucherStatus(String voucherId, boolean isActive, OperationCallback callback) {
        db.collection(COLLECTION_NAME)
            .document(voucherId)
            .update("active", isActive)
            .addOnSuccessListener(aVoid -> {
                Log.d("VoucherRepo", "Toggled voucher status: " + voucherId);
                callback.onSuccess(isActive ? "Đã kích hoạt voucher" : "Đã vô hiệu hóa voucher");
            })
            .addOnFailureListener(e -> {
                Log.e("VoucherRepo", "Error toggling voucher status", e);
                callback.onFailure("Lỗi: " + e.getMessage());
            });
    }
}
