package com.example.bt1;

import java.util.Date;

public class Comment {
    private long id;
    private long userId;
    private long productId;
    private int rate;
    private Date createdDate;
    private String content;

    public Comment() {
        this.rate = 0;
        this.createdDate = new Date();
        this.content = "";
    }

    public Comment(int rate, Date createdDate, String content) {
        this.rate = rate;
        this.createdDate = createdDate;
        this.content = content;
    }

    public long getUserId() {return this.userId;}
    public long getProductId() {return this.productId;}
    public int getRate() {return this.rate;}
    public Date getCreatedDate() {return this.createdDate;}
    public String getContent() {return this.content;}
}
