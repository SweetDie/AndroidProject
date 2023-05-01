package com.example.firstproject.category;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firstproject.BaseActivity;
import com.example.firstproject.ChangeImageActivity;
import com.example.firstproject.MainActivity;
import com.example.firstproject.R;
import com.example.firstproject.dto.category.CategoryCreateDTO;
import com.example.firstproject.service.CategoryNetwork;
import com.example.firstproject.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryCreateActivity extends BaseActivity {
    private static final int SELECT_IMAGE_RESULT = 300;
    private ImageView ivPreviewImage;
    private Uri uri = null;
    TextInputEditText txtCategoryName;
    TextInputEditText txtCategoryPriority;
    TextInputEditText txtCategoryDescription;
    TextInputLayout tfCategoryName;
    TextInputLayout tfCategoryPriority;
    TextInputLayout tfCategoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        ivPreviewImage = findViewById(R.id.ivPreviewImage);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtCategoryPriority = findViewById(R.id.txtCategoryPriority);
        txtCategoryDescription = findViewById(R.id.txtCategoryDescription);

        tfCategoryName = findViewById(R.id.tfCategoryName);
        tfCategoryPriority = findViewById(R.id.tfCategoryPriority);
        tfCategoryDescription = findViewById(R.id.tfCategoryDescription);

        setupError();
    }

    private void setupError() {
        txtCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 2) {
                    tfCategoryName.setError(getString(R.string.category_name_required));
                } else {
                    tfCategoryName.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtCategoryDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 2) {
                    tfCategoryName.setError(getString(R.string.category_description_required));
                } else {
                    tfCategoryName.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtCategoryPriority.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int number = 0;
                try {
                    number = Integer.parseInt(s.toString());
                } catch (Exception ex) {
                }
                if (number <= 0) {
                    tfCategoryPriority.setError(getString(R.string.category_priority_required));
                } else {
                    tfCategoryPriority.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean validation() {
        boolean isValid = true;
        String name = txtCategoryName.getText().toString();

        if (name.isEmpty() || name.length() <= 2) {
            tfCategoryName.setError(getString(R.string.category_name_required));
            isValid = false;
        }

        int number = 0;
        try {
            number = Integer.parseInt(txtCategoryPriority.getText().toString());
        } catch (Exception ex) {
        }
        if (number <= 0) {
            tfCategoryPriority.setError(getString(R.string.category_priority_required));
            isValid = false;
        }

        String description = txtCategoryDescription.getText().toString();
        if (description.isEmpty() || description.length() <= 2) {
            tfCategoryDescription.setError(getString(R.string.category_description_required));
            isValid = false;
        }
        if(uri == null) {
            isValid = false;
            Toast.makeText(this, "Оберіть фото", Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_RESULT) {
            uri = (Uri) data.getParcelableExtra("croppedUri");
            ivPreviewImage.setImageURI(uri);
        }
    }

    public void onClickCreateCategory(View view) {
        if (!validation()) {
            return;
        }
        CategoryCreateDTO model = new CategoryCreateDTO();
        model.setName(txtCategoryName.getText().toString());
        model.setDescription(txtCategoryDescription.getText().toString());
        model.setPriority(Integer.parseInt(txtCategoryPriority.getText().toString()));
        model.setImageBase64(uriGetBase64(uri));
        CommonUtils.showLoading();
        CategoryNetwork.getInstance()
                .getJsonApi()
                .create(model)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent intent = new Intent(CategoryCreateActivity.this, MainActivity.class);
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