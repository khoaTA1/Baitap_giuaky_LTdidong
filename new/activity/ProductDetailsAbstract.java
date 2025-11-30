package com.example.bt1;

import java.util.Date;

public abstract class ProductDetailsAbstract {
    public static class ProductImage extends ProductDetailsAbstract {
        public int imageId;
        public ProductImage(int imageId) {
            this.imageId = imageId;
        }
    }

    public static class ProductPreview extends ProductDetailsAbstract {
        public String productName;
        public String productPrice;
        public int soldCount;
        public ProductPreview(String productName, String productPrice, int soldCount) {
            this.productName = productName;
            this.productPrice = productPrice;
            this.soldCount = soldCount;
        }
    }

    public static class SpecTitle extends ProductDetailsAbstract {
        public String title;
        public SpecTitle(String title) {
            this.title = title;
        }
    }
    public static class ProductSpec extends ProductDetailsAbstract {
        public String key;
        public String value;
        public ProductSpec(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class CommentTitle extends ProductDetailsAbstract {
        public String title;
        public CommentTitle(String title) {
            this.title = title;
        }
    }

    public static class Comment extends ProductDetailsAbstract {
        public String userName;
        public Date createdDate;
        public int rate;
        public String content;
        public Comment(String userName, Date createdDate, int rate, String content) {
            this.userName = userName;
            this.createdDate = createdDate;
            this.rate = rate;
            this.content = content;
        }
    }
}
