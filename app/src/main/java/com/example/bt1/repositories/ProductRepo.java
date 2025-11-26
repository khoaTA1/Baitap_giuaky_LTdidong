package com.example.bt1.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bt1.models.Product;
import com.example.bt1.utils.FireStoreCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductRepo {

    private static final String COLLECTION_NAME = "products";
    private final FirebaseFirestore db;
    private DocumentSnapshot lastVisibleDoc = null;

    public ProductRepo() {
        db = FirebaseFirestore.getInstance();
    }

    // CREATE
    public void addProduct(Product product) {
        DocumentReference docRef = db.collection("pref").document("trackLastProductId");

        db.runTransaction(trans -> {
            DocumentSnapshot snap = trans.get(docRef);

            long lastProductId = snap.getLong("lastProductId");

            trans.update(docRef, "lastProductId", lastProductId + 1);

            trans.set(db.collection("products").document(String.valueOf(lastProductId + 1)), product);

            return lastProductId + 1;
        }).addOnSuccessListener(productId -> {
            Log.d(">>> Firestore", "Đã thêm 1 sản phẩm: " + productId);
        }).addOnFailureListener(e -> {
            Log.e("!!! Firestore", "Đã có lỗi: ", e);
        });
    }

    // READ ALL
    public void getAllProducts(FireStoreCallBack callback) {

        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> productList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Product product = doc.toObject(Product.class);
                        productList.add(product);
                    }

                    callback.returnResult(productList);   // SUCCESS
                })
                .addOnFailureListener(e -> {
                    callback.returnResult(null);          // ERROR
                });
    }

    // Lấy một số sản phẩm
    public void getProductsBatch(int limit, FireStoreCallBack callback) {
        Query query = db.collection(COLLECTION_NAME)
                .orderBy("name") // hoặc "name" nếu muốn sắp xếp khác
                .limit(limit);

        if (lastVisibleDoc != null) {
            query = query.startAfter(lastVisibleDoc);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Product product = doc.toObject(Product.class);
                        productList.add(product);
                    }

                    // Cập nhật document cuối cùng để load tiếp lần sau
                    if (!querySnapshot.isEmpty()) {
                        lastVisibleDoc = querySnapshot.getDocuments()
                                .get(querySnapshot.size() - 1);
                    }

                    callback.returnResult(productList);
                    Log.d(">>> Firestore", "Đã load danh sách sản phẩm tiếp theo, số sản phẩm: " + productList.size());
                })
                .addOnFailureListener(e -> {
                    callback.returnResult(null);
                    Log.e("!!! Firestore", "Không thể load danh sách sản phẩm tiếp theo");
                });
    }

    // UPDATE
    public void updateProduct(Product product) {
        String docId = String.valueOf(product.getId());

        db.collection(COLLECTION_NAME)
                .document(docId)
                .set(product, SetOptions.merge())
                .addOnSuccessListener(m -> {
                    Log.d(">>> Firestore", "Update sản phẩm: " + docId);
                })
                .addOnFailureListener(e -> {
                    Log.e("!!! Firestore", "Sản phẩm chưa được update: " + docId);
                });
    }

    // DELETE
    public void deleteProduct(long productId) {
        String docId = String.valueOf(productId);

        db.collection(COLLECTION_NAME)
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(">>> Firestore", "Xóa sản phẩm thành công: " + docId))
                .addOnFailureListener(e -> Log.e("!!! Firestore", "Sản phẩm chưa được xóa: " + docId));
    }


}
