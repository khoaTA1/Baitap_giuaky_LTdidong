package com.example.bt1.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bt1.models.ApiResponse;
import com.example.bt1.models.Order;
import com.example.bt1.network.ApiClient;
import com.example.bt1.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository cho Order - quản lý các thao tác liên quan đến đơn hàng
 */
public class OrderRepository {
    private ApiService apiService;

    public OrderRepository() {
        this.apiService = ApiClient.getApiService();
    }

    /**
     * Tạo đơn hàng mới
     */
    public LiveData<ApiResponse<Order>> createOrder(Order order) {
        MutableLiveData<ApiResponse<Order>> data = new MutableLiveData<>();
        
        apiService.createOrder(order).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<Order> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Tạo đơn hàng thất bại");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                ApiResponse<Order> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Lấy danh sách đơn hàng của user
     */
    public LiveData<ApiResponse<List<Order>>> getOrdersByUser(int userId) {
        MutableLiveData<ApiResponse<List<Order>>> data = new MutableLiveData<>();
        
        apiService.getOrdersByUser(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không tìm thấy đơn hàng");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Lấy chi tiết đơn hàng theo ID
     */
    public LiveData<ApiResponse<Order>> getOrderById(int orderId) {
        MutableLiveData<ApiResponse<Order>> data = new MutableLiveData<>();
        
        apiService.getOrderById(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<Order> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không tìm thấy đơn hàng");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                ApiResponse<Order> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Hủy đơn hàng
     */
    public LiveData<ApiResponse<Void>> cancelOrder(int orderId) {
        MutableLiveData<ApiResponse<Void>> data = new MutableLiveData<>();
        
        apiService.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> successResponse = new ApiResponse<>();
                    successResponse.setSuccess(true);
                    successResponse.setMessage("Hủy đơn hàng thành công");
                    data.setValue(successResponse);
                } else {
                    ApiResponse<Void> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Hủy đơn hàng thất bại");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                ApiResponse<Void> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }
}
