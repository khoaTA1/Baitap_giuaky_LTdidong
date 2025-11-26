package com.example.bt1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.example.bt1.R;
import com.example.bt1.models.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public class RenderImage {
    public RenderImage() {}
    public void renderProductImage(Context context, Product product, ImageView imageView) {
        String imagePath = product.getImagePath();

        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                // Dùng BitmapFactory để decode từ file
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                // Fallback nếu file không tồn tại
                imageView.setImageResource(R.drawable.logo);
            }
        } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Nếu chưa tải ảnh, tải về rồi render
            downloadAndSaveImage(context, product, () -> {
                // callback chạy trên main thread
                renderProductImage(context, product, imageView);
            });
        } else {
            // Nếu không có cả URL và path
            imageView.setImageResource(R.drawable.logo);
        }
    }

    public void downloadAndSaveImage(Context context, Product product, Runnable callback) {
        new Thread(() -> {
            try {
                URL url = new URL(product.getImageUrl().trim());
                Log.d(">>> Download Image", product.getImageUrl());
                InputStream input = url.openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                // tạo id ngẫu nhiên cho tên ảnh tải về
                String randomId = UUID.randomUUID().toString() + ".jpg";
                File file = new File(context.getCacheDir(), randomId);

                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                product.setImagePath(file.getAbsolutePath());
            } catch (Exception e) {
                Log.e("!!! Download Image", "Lỗi tải ảnh: ", e);
                product.setImagePath(null);
            }
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(callback);
            }
        }).start();
    }

}
