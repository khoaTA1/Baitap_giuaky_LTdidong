package com.example.bt1.models;

import java.util.Date;
import java.util.List;

public abstract class ProductDetailsAbstract {
    public static class ProductImage extends ProductDetailsAbstract {
        public Product product;
        public ProductImage(Product product) {
            this.product = product;
        }
    }

    public static class ProductPreview extends ProductDetailsAbstract {
        public String productName;
        public String productPrice;
        public String productDesc;
        public ProductPreview(String productName, String productPrice, String productDesc) {
            this.productName = productName;
            this.productPrice = productPrice;
            this.productDesc = productDesc;
        }
    }
    public static class SpecTitle extends ProductDetailsAbstract {
        public String title;
        public SpecTitle(String title) {
            this.title = title;
        }
    }

    public static class ProductSpecGroup extends ProductDetailsAbstract {
        public List<ProductSpec> list;

        public ProductSpecGroup(List<ProductSpec> list) {
            this.list = list;
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
        public String fullname;
        public Date createdDate;
        public float rate;
        public String content;
        public Comment(String fullname, Date createdDate, float rate, String content) {
            this.fullname = fullname;
            this.createdDate = createdDate;
            this.rate = rate;
            this.content = content;
        }
    }

    public static class CommentInput extends ProductDetailsAbstract {
        public CommentInput() {}
    }
}
