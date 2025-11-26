package com.example.bt1.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bt1.models.ApiResponse;
import com.example.bt1.models.User;
import com.example.bt1.network.ApiClient;
import com.example.bt1.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository cho User - quản lý các thao tác liên quan đến người dùng
 */
public class UserRepository {
    private ApiService apiService;

    public UserRepository() {
        this.apiService = ApiClient.getApiService();
    }

    /**
     * Đăng nhập
     */
    public LiveData<ApiResponse<User>> login(String email, String password) {
        MutableLiveData<ApiResponse<User>> data = new MutableLiveData<>();
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        
        apiService.login(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<User> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Đăng nhập thất bại");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                ApiResponse<User> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Đăng ký tài khoản
     */
    public LiveData<ApiResponse<User>> register(User user) {
        MutableLiveData<ApiResponse<User>> data = new MutableLiveData<>();
        
        apiService.register(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<User> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Đăng ký thất bại");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                ApiResponse<User> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Lấy thông tin user theo ID
     */
    public LiveData<ApiResponse<User>> getUserById(int userId) {
        MutableLiveData<ApiResponse<User>> data = new MutableLiveData<>();
        
        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<User> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không tìm thấy người dùng");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                ApiResponse<User> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }

    /**
     * Cập nhật thông tin user
     */
    public LiveData<ApiResponse<User>> updateUser(int userId, User user) {
        MutableLiveData<ApiResponse<User>> data = new MutableLiveData<>();
        
        apiService.updateUser(userId, user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    ApiResponse<User> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Cập nhật thất bại");
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                ApiResponse<User> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setError(t.getMessage());
                data.setValue(errorResponse);
            }
        });
        
        return data;
    }
}
