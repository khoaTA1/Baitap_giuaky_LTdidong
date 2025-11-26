package com.example.bt1.network;

import com.example.bt1.models.ApiResponse;
import com.example.bt1.models.Product;
import com.example.bt1.models.User;
import com.example.bt1.models.Order;
import com.example.bt1.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các API endpoints
 */
public interface ApiService {
    
    // ============ USER APIs ============
    
    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body User user);
    
    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body User user);
    
    @GET("users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") int userId);
    
    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") int userId, @Body User user);
    
    // ============ PRODUCT APIs ============
    
    @GET("products")
    Call<ApiResponse<List<Product>>> getAllProducts();
    
    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") int productId);
    
    @GET("products/search")
    Call<ApiResponse<List<Product>>> searchProducts(@Query("keyword") String keyword);
    
    @GET("products/category/{categoryId}")
    Call<ApiResponse<List<Product>>> getProductsByCategory(@Path("categoryId") int categoryId);
    
    @GET("products/brand/{brand}")
    Call<ApiResponse<List<Product>>> getProductsByBrand(@Path("brand") String brand);
    
    // ============ CATEGORY APIs ============
    
    @GET("categories")
    Call<ApiResponse<List<Category>>> getAllCategories();
    
    @GET("categories/{id}")
    Call<ApiResponse<Category>> getCategoryById(@Path("id") int categoryId);
    
    // ============ ORDER APIs ============
    
    @POST("orders")
    Call<ApiResponse<Order>> createOrder(@Body Order order);
    
    @GET("orders/user/{userId}")
    Call<ApiResponse<List<Order>>> getOrdersByUser(@Path("userId") int userId);
    
    @GET("orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") int orderId);
    
    @PUT("orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(@Path("id") int orderId, @Query("status") String status);
    
    @DELETE("orders/{id}")
    Call<ApiResponse<Void>> cancelOrder(@Path("id") int orderId);
}
