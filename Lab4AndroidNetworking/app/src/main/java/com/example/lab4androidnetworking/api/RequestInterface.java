package com.example.lab4androidnetworking.api;

import com.example.lab4androidnetworking.model.ServerRequest;
import com.example.lab4androidnetworking.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("learn-login-register/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
