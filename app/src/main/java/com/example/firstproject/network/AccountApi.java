package com.example.firstproject.network;

import com.example.firstproject.dto.account.LoginDTO;
import com.example.firstproject.dto.account.LoginResponse;
import com.example.firstproject.dto.account.RegisterDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountApi {
    @POST("/api/Account/register")
    public Call<Void> register(@Body RegisterDTO registerDTO);

    @POST("/api/Account/login")
    public Call<LoginResponse> login(@Body LoginDTO loginDTO);
}