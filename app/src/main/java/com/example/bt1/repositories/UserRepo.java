package com.example.bt1.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bt1.models.User;
import com.example.bt1.utils.FireStoreCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {
    private String COLLECTION_NAME = "users";
    private final FirebaseFirestore db;

    public UserRepo() {
        db = FirebaseFirestore.getInstance();
    }

    // CREATE
    public void createUser(Map<String, Object> user, Context context) {

        // lưu vào firestore
        // lấy user id mới nhất và cập nhật lại bằng transaction nhằm tránh race condition
        DocumentReference docRef = db.collection("pref").document("trackLastUserId");

        db.runTransaction(transaction -> {
                    DocumentSnapshot docSnap = transaction.get(docRef);

                    long lastUserId = docSnap.getLong("lastUserId");

                    // cập nhật pref
                    transaction.update(docRef, "lastUserId", lastUserId + 1);

                    // lưu user mới vào firestore
                    transaction.set(db.collection(COLLECTION_NAME).document(String.valueOf(lastUserId + 1)), user);

                    return lastUserId + 1;
                }).addOnSuccessListener(userId -> {
                    Log.d(">>> User Repo", "Đã thêm user: " + userId);
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("!!! User Repo", "Lỗi: ", e);
                    Toast.makeText(context, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                });
    }

    // READ
    public void getUserById(long id, FireStoreCallBack callback) {

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Log.d(">>> User Repo", "Tồn tại user");

                        User user = new User();

                        // user.setId(Long.valueOf(snapshot.getId())); // snapshot.getId() returns String, not convertible
                        user.setFullName(snapshot.getString("fullname"));
                        user.setEmail(snapshot.getString("email"));
                        user.setAddress(snapshot.getString("address"));
                        //user.setCreatedAt(snapshot.getTimestamp("createdAt").toString());
                        //user.setPassword(snapshot.getString("password"));
                        user.setRecentCategries((List<String>) snapshot.get("recentCategories"));

                        callback.returnResult(user);
                    } else {
                        Log.d("!!! User Repo", "Không tồn tại user");
                        callback.returnResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("!!! User Repo", "Lỗi: ", e);
                });
    }

    // update recent categories
    public void updateUserRecentCategories(long uid, String mostRecentCate) {
        // lấy danh sách các cate gần đây của người dùng
        db.collection(COLLECTION_NAME).document(String.valueOf(uid))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(">>> User Repo", "Tìm thấy user");
                    List<String> recentCategories = new ArrayList<>();

                    if (documentSnapshot.contains("recentCategories")) {
                        Log.d(">>> User Repo", "User đã có trường recent categories");
                        recentCategories = (List<String>) documentSnapshot.get("recentCategories");

                        // dời các category cũ hơn về sau
                        // để chừa vị trí đầu tiên cho categry gần đây nhất
                        for (int i = Math.min(recentCategories.size(), 2); i > 0; i--) {
                            recentCategories.set(i, recentCategories.get(i - 1));
                        }
                    }

                    recentCategories.set(0, mostRecentCate);

                    db.collection(COLLECTION_NAME).document(String.valueOf(uid))
                            .update("recentCategories", recentCategories)
                            .addOnSuccessListener(v -> {
                                Log.d(">>> User Repo", "Đã update recent categories cho user với id: " + uid);
                            }).addOnFailureListener(e -> {
                                Log.e("!!! User Repo", "Lỗi: ", e);
                            });
                }).addOnFailureListener(e -> {
                    Log.e("!!! User Repo", "Lỗi: ", e);
                });

    }

    // UPDATE
    public void updateUser(User user) {

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(user.getId()))
                .set(user)
                .addOnSuccessListener(v -> {
                    Log.d(">>> User Repo", "Đã cập nhật user");
                }).addOnFailureListener(e -> {
                    Log.e("!!! User Repo", "Lỗi: ", e);
                });
    }

    // DELETE
    public void deleteUser(long id) {

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(v -> {
                    Log.d(">>> User Repo", "Đã xóa user");
                })
                .addOnFailureListener(e -> {
                    Log.e("!!! User Repo", "Lỗi: ", e);
                });
    }
}
