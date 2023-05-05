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
import com.example.firstproject.dto.category.CategoryUpdateDTO;
import com.example.firstproject.service.ApplicationNetwork;
import com.example.firstproject.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryEditActivity extends BaseActivity {
    private static final int SELECT_IMAGE_RESULT = 300;
    private ImageView ivPreviewImage;
    private Uri uri = null;
    TextInputEditText txtCategoryName;
    TextInputEditText txtCategoryPriority;
    TextInputEditText txtCategoryDescription;
    TextInputLayout tfCategoryName;
    TextInputLayout tfCategoryPriority;
    TextInputLayout tfCategoryDescription;
    CategoryUpdateDTO categoryUpdateDTO;

    CategoryEditActivity(CategoryUpdateDTO categoryUpdateDTO) {
        this.categoryUpdateDTO = categoryUpdateDTO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        ivPreviewImage = findViewById(R.id.ivPreviewImage);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtCategoryPriority = findViewById(R.id.txtCategoryPriority);
        txtCategoryDescription = findViewById(R.id.txtCategoryDescription);
        tfCategoryName = findViewById(R.id.tfCategoryName);
        tfCategoryPriority = findViewById(R.id.tfCategoryPriority);
        tfCategoryDescription = findViewById(R.id.tfCategoryDescription);

        txtCategoryName.setText(categoryUpdateDTO.getName());
        txtCategoryDescription.setText(categoryUpdateDTO.getDescription());
        txtCategoryPriority.setText(categoryUpdateDTO.getPriority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_RESULT) {
            uri = (Uri) data.getParcelableExtra("croppedUri");
            ivPreviewImage.setImageURI(uri);
        }
    }

    public void onClickEditCategory(View view) {
//        if (!validation()) {
//            return;
//        }
        categoryUpdateDTO.setName(txtCategoryName.getText().toString());
        categoryUpdateDTO.setDescription(txtCategoryDescription.getText().toString());
        categoryUpdateDTO.setPriority(Integer.parseInt(txtCategoryPriority.getText().toString()));
        if(uri != null) {
            categoryUpdateDTO.setImageBase64(uriGetBase64(uri));

        }
        CommonUtils.showLoading();
        ApplicationNetwork.getInstance()
                .getJsonApi()
                .update(categoryUpdateDTO)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent intent = new Intent(CategoryEditActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        CommonUtils.hideLoading();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CommonUtils.hideLoading();
                    }
                });
    }

    private String uriGetBase64(Uri uri) {
        try {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArr = bytes.toByteArray();
            return Base64.encodeToString(byteArr, Base64.DEFAULT);

        } catch (Exception ex) {
            return null;
        }
    }

    public void onClickSelectImage(View view) {
        Intent intent = new Intent(this, ChangeImageActivity.class);
        startActivityForResult(intent, SELECT_IMAGE_RESULT);
    }
}