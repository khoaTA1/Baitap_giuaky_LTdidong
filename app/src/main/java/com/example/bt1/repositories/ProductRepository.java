package com.example.bt1.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bt1.models.ApiResponse;
import com.example.bt1.models.Product;
import com.example.bt1.network.ApiClient;
import com.example.bt1.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository cho Product - lớp trung gian giữa ViewModel và API
 */
public class ProductRepository {
    private ApiService apiService;

    public ProductRepository() {
        this.apiService = ApiClient.getApiService();
    }

    /**
     * Lấy tất cả sản phẩm từ API
     */
    public LiveData<ApiResponse<List<Product>>> getAllProducts() {
        MutableLiveData<ApiResponse<List<Product>>> data = new MutableLiveData<>();
        
        apiService.getAllProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<List<Product>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Lỗi khi tải dữ liệu");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                ApiResponse<List<Product>> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public LiveData<ApiResponse<Product>> getProductById(int productId) {
        MutableLiveData<ApiResponse<Product>> data = new MutableLiveData<>();
        
        apiService.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<Product> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không tìm thấy sản phẩm");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                ApiResponse<Product> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa
     */
    public LiveData<ApiResponse<List<Product>>> searchProducts(String keyword) {
        MutableLiveData<ApiResponse<List<Product>>> data = new MutableLiveData<>();
        
        apiService.searchProducts(keyword).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<List<Product>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không tìm thấy kết quả");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                ApiResponse<List<Product>> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Lấy sản phẩm theo brand (iPhone, Samsung, Xiaomi...)
     */
    public LiveData<ApiResponse<List<Product>>> getProductsByBrand(String brand) {
        MutableLiveData<ApiResponse<List<Product>>> data = new MutableLiveData<>();
        
        apiService.getProductsByBrand(brand).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<List<Product>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không tìm thấy sản phẩm");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                ApiResponse<List<Product>> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }
}
