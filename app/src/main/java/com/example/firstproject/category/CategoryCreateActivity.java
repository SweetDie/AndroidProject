package com.example.firstproject.category;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.example.firstproject.BaseActivity;
import com.example.firstproject.ChangeImageActivity;
import com.example.firstproject.MainActivity;
import com.example.firstproject.R;
import com.example.firstproject.dto.category.CategoryCreateDTO;
import com.example.firstproject.service.CategoryNetwork;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryCreateActivity extends BaseActivity {
    private static int SELECT_IMAGE_RESULT = 300;
    private ImageView ivPreviewImage;
    private Uri uri = null;
    TextInputEditText txtCategoryName;
    TextInputEditText txtCategoryPriority;
    TextInputEditText txtCategoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        ivPreviewImage = findViewById(R.id.ivPreviewImage);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtCategoryPriority = findViewById(R.id.txtCategoryPriority);
        txtCategoryDescription = findViewById(R.id.txtCategoryDescription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_RESULT) {
            uri = (Uri) data.getParcelableExtra("croppedUri");
            ivPreviewImage.setImageURI(uri);
        }
    }

    public void onClickCreateCategory(View view) {
        CategoryCreateDTO model = new CategoryCreateDTO();
        model.setName(txtCategoryName.getText().toString());
        model.setDescription(txtCategoryDescription.getText().toString());
        model.setPriority(Integer.parseInt(txtCategoryPriority.getText().toString()));
        model.setImageBase64(uriGetBase64(uri));

        CategoryNetwork.getInstance()
                .getJsonApi()
                .create(model)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent intent = new Intent(CategoryCreateActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private String uriGetBase64(Uri uri) {
        try {
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch(IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArr = bytes.toByteArray();
            return Base64.encodeToString(byteArr, Base64.DEFAULT);

        } catch(Exception ex) {
            return null;
        }
    }

    public void onClickSelectImage(View view) {
        Intent intent = new Intent(this, ChangeImageActivity.class);
        startActivityForResult(intent, SELECT_IMAGE_RESULT);
    }
}