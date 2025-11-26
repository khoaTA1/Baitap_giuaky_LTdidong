package com.example.bt1.utils;

import com.example.bt1.models.Product;
import com.example.bt1.models.User;

public interface FireStoreCallBack<T> {
    void returnResult(T result);
    //void onFailure(Exception e);
}
