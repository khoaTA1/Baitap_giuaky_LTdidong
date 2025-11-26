package com.example.bt1.models;

import java.util.Date;

public class Comment {
    private long id;
    private long userId;
    private long productId;
    private float rate;
    private Date createdDate;
    private String content;

    public Comment() {
    }

    public Comment(long uid, long pid, float rate, Date createdDate, String content) {
        this.userId = uid;
        this.productId = pid;
        this.rate = rate;
        this.createdDate = createdDate;
        this.content = content;
    }

    public long getUserId() {return this.userId;}
    public long getProductId() {return this.productId;}

    public float getRate() {return this.rate;}
    public Date getCreatedDate() {return this.createdDate;}
    public String getContent() {return this.content;}
    
    public void setUserId(long userId) { this.userId = userId; }
    public void setProductId(long productId) { this.productId = productId; }
    public void setRate(float rate) { this.rate = rate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public void setContent(String content) { this.content = content; }
}
