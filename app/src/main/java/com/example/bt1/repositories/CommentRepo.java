package com.example.bt1.repositories;

import android.util.Log;

import com.example.bt1.models.Comment;
import com.example.bt1.utils.FireStoreCallBack;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CommentRepo {
    private String COLLECTION_NAME = "comments";
    private FirebaseFirestore db;

    public CommentRepo() {
        db = FirebaseFirestore.getInstance();
    }

    // create
    public void createComment(Comment comment) {
        db.collection(COLLECTION_NAME).document().set(comment)
                .addOnSuccessListener(v -> {
                    Log.d(">>> Comment Repo", "Đã tạo comment");
                }).addOnFailureListener(e -> {
                    Log.e("!!! Comment Repo", "Lỗi: ", e);
                });
    }


    // read by product id
    public void getCommentByPid(long pid, FireStoreCallBack callback) {
        Log.d(">>> Comment Repo", "Lấy comment cho sản phẩm có id: " + pid);
        db.collection(COLLECTION_NAME)
                .whereEqualTo("productId", pid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Comment> commentList = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Comment comment = doc.toObject(Comment.class);

                        Timestamp firestoreTimestamp = doc.getTimestamp("createdDate");
                        comment.setCreatedDate(firestoreTimestamp.toDate());

                        commentList.add(comment);
                    }

                    callback.returnResult(commentList);
                    Log.d(">>> Comment Repo", "Đã lấy danh sách comment: " + commentList.size());
                }).addOnFailureListener(e -> {
                    Log.e("!!! Comment Repo", "Lỗi: ", e);
                });
    }
}
