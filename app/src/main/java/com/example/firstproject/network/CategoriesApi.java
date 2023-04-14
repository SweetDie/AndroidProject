package com.example.firstproject.network;

import com.example.firstproject.dto.category.CategoryItemDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoriesApi {
    @GET("api/categories/list")
    public Call<List<CategoryItemDTO>> list();
}
