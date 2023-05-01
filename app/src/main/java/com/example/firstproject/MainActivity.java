package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firstproject.category.CategoriesAdapter;
import com.example.firstproject.dto.category.CategoryItemDTO;
import com.example.firstproject.service.CategoryNetwork;
import com.example.firstproject.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    CategoriesAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv = findViewById(R.id.imageView);
        String url = "https://pv016.allin.ml/images/1.jpg";
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions().override(600))
                .into(iv);

        recyclerView = findViewById(R.id.rcvCategories);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new CategoriesAdapter(new ArrayList<>()));
        requestServer();
    }

    void requestServer() {
        CommonUtils.showLoading();
        CategoryNetwork
                .getInstance()
                .getJsonApi()
                .list()
                .enqueue(new Callback<List<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<List<CategoryItemDTO>> call, Response<List<CategoryItemDTO>> response) {
                        List<CategoryItemDTO> data = response.body();
                        adapter = new CategoriesAdapter(data);
                        recyclerView.setAdapter(adapter);
                        CommonUtils.hideLoading();
                    }

                    @Override
                    public void onFailure(Call<List<CategoryItemDTO>> call, Throwable t) {
                        CommonUtils.hideLoading();
                    }
                });
    }
}