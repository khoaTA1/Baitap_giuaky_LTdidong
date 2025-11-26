package com.example.bt1.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client Retrofit để kết nối với API backend
 */
public class ApiClient {
    // TODO: Thay đổi BASE_URL thành URL backend thực tế của bạn
    private static final String BASE_URL = "https://api.taostore.com/api/";
    
    // Singleton instance
    private static Retrofit retrofit = null;

    /**
     * Lấy instance Retrofit
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Lấy ApiService instance
     */
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
