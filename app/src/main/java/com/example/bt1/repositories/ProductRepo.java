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
                        // Lưu document ID gốc
                        String docId = doc.getId();
                        product.setDocumentId(docId);
                        // Set ID từ Firebase document ID
                        try {
                            product.setId(Long.parseLong(docId));
                        } catch (NumberFormatException e) {
                            product.setId((long) docId.hashCode());
                            Log.e(">>> ProductRepo", "Cannot parse document ID: " + docId + ", using hashCode", e);
                        }
                        productList.add(product);
                    }

                    callback.returnResult(productList);   // SUCCESS
                    Log.d(">>> ProductRepo", "Loaded " + productList.size() + " products with IDs");
                })
                .addOnFailureListener(e -> {
                    Log.e(">>> ProductRepo", "Error loading products", e);
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
                        // Lưu document ID gốc
                        String docId = doc.getId();
                        product.setDocumentId(docId);
                        // Set ID từ Firebase document ID
                        try {
                            product.setId(Long.parseLong(docId));
                        } catch (NumberFormatException e) {
                            product.setId((long) docId.hashCode());
                            Log.e(">>> ProductRepo", "Cannot parse document ID: " + docId + ", using hashCode", e);
                        }
                        productList.add(product);
                    }

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

    // lấy danh sách sản phẩm theo 3 danh mục gần đây nhất
    public void getProductByRecentCate(List<String> recentCates, int limit, FireStoreCallBack callback) {
        db.collection("products")
                .whereIn("category", recentCates).limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        products.add(doc.toObject(Product.class));
                    }

                    callback.returnResult(products);
                    Log.d(">>> Product Repo", "Đã lấy dược danh sách sản phẩm, số lượng: " + products.size());
                }).addOnFailureListener(e -> {
                    Log.e("!!! Product Repo", "Lỗi: ", e);
                    callback.returnResult(null);
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
    
    // GET PRODUCT BY ID
    public void getProductById(long productId, FireStoreCallBack callback) {
        String docId = String.valueOf(productId);
        
        db.collection(COLLECTION_NAME)
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            String firebaseDocId = documentSnapshot.getId();
                            product.setDocumentId(firebaseDocId);
                            try {
                                product.setId(Long.parseLong(firebaseDocId));
                            } catch (NumberFormatException e) {
                                product.setId((long) firebaseDocId.hashCode());
                                Log.e(">>> ProductRepo", "Cannot parse document ID: " + firebaseDocId + ", using hashCode", e);
                            }
                            callback.returnResult(product);
                            Log.d(">>> ProductRepo", "Loaded product with ID: " + productId);
                        } else {
                            callback.returnResult(null);
                            Log.e(">>> ProductRepo", "Product is null after deserialization");
                        }
                    } else {
                        callback.returnResult(null);
                        Log.e(">>> ProductRepo", "Product not found with ID: " + productId);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.returnResult(null);
                    Log.e(">>> ProductRepo", "Error loading product with ID: " + productId, e);
                });
    }
    
    // UPDATE SOLD COUNT
    public void updateProductSoldCount(long productId, int soldCount) {
        String docId = String.valueOf(productId);
        
        db.collection(COLLECTION_NAME)
                .document(docId)
                .update("soldCount", soldCount)
                .addOnSuccessListener(aVoid -> {
                    Log.d(">>> ProductRepo", "Updated sold count for product " + productId + ": " + soldCount);
                })
                .addOnFailureListener(e -> {
                    Log.e(">>> ProductRepo", "Failed to update sold count for product " + productId, e);
                });
    }
    
    // CALCULATE AND UPDATE ALL PRODUCTS SOLD COUNT FROM COMPLETED ORDERS
    public void calculateAndUpdateSoldCounts(OnCompleteListener<Void> listener) {
        // Step 1: Get all completed orders
        db.collection("orders")
                .whereEqualTo("status", "Hoàn thành")
                .get()
                .addOnSuccessListener(orderSnapshots -> {
                    // Step 2: Count sold quantity for each product
                    java.util.HashMap<Long, Integer> soldCountMap = new java.util.HashMap<>();
                    
                    for (QueryDocumentSnapshot orderDoc : orderSnapshots) {
                        try {
                            // Get products array from order
                            List<java.util.HashMap<String, Object>> products = 
                                (List<java.util.HashMap<String, Object>>) orderDoc.get("products");
                            
                            if (products != null) {
                                for (java.util.HashMap<String, Object> productData : products) {
                                    try {
                                        Long productId = null;
                                        Object idObj = productData.get("id");
                                        if (idObj instanceof Long) {
                                            productId = (Long) idObj;
                                        } else if (idObj instanceof String) {
                                            productId = Long.parseLong((String) idObj);
                                        } else if (idObj instanceof Integer) {
                                            productId = ((Integer) idObj).longValue();
                                        }
                                        
                                        Integer quantity = 1; // default
                                        Object qtyObj = productData.get("quantity");
                                        if (qtyObj instanceof Long) {
                                            quantity = ((Long) qtyObj).intValue();
                                        } else if (qtyObj instanceof Integer) {
                                            quantity = (Integer) qtyObj;
                                        } else if (qtyObj instanceof String) {
                                            quantity = Integer.parseInt((String) qtyObj);
                                        }
                                        
                                        if (productId != null) {
                                            soldCountMap.put(productId, 
                                                soldCountMap.getOrDefault(productId, 0) + quantity);
                                        }
                                    } catch (Exception e) {
                                        Log.e(">>> ProductRepo", "Error parsing product in order", e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(">>> ProductRepo", "Error processing order: " + orderDoc.getId(), e);
                        }
                    }
                    
                    // Step 3: Update each product's sold count
                    Log.d(">>> ProductRepo", "Updating sold counts for " + soldCountMap.size() + " products");
                    
                    for (java.util.Map.Entry<Long, Integer> entry : soldCountMap.entrySet()) {
                        updateProductSoldCount(entry.getKey(), entry.getValue());
                    }
                    
                    if (listener != null) {
                        listener.onComplete(com.google.android.gms.tasks.Tasks.forResult(null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(">>> ProductRepo", "Failed to calculate sold counts", e);
                    if (listener != null) {
                        listener.onComplete(com.google.android.gms.tasks.Tasks.forException(e));
                    }
                });
    }
}
