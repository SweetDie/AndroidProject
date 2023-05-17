package com.example.firstproject.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.example.firstproject.ChangeImageActivity;
import com.example.firstproject.MainActivity;
import com.example.firstproject.R;
import com.example.firstproject.application.HomeApplication;
import com.example.firstproject.dto.account.ProfileDTO;
import com.example.firstproject.dto.account.RegisterDTO;
import com.example.firstproject.security.JwtSecurityService;
import com.example.firstproject.service.ApplicationNetwork;
import com.example.firstproject.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_RESULT = 300;
    Uri uri = null;

    ImageView IVPreviewImage;
    TextInputEditText txtLastName;
    TextInputEditText txtFirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        txtLastName = findViewById(R.id.txtLastName);
        txtFirstName = findViewById(R.id.txtFirstName);

        JwtSecurityService jwt = HomeApplication.getInstance();
        String body = jwt.decoded();
        Gson gson = new Gson();
        ProfileDTO model = gson.fromJson(body, ProfileDTO.class);

        txtLastName.setText(model.getLastName());
        txtFirstName.setText(model.getFirstName());

        byte[] strBytes = Base64.decode(model.getImageBase64(), Base64.DEFAULT);
        byte[] encoded = Base64.encode(
                strBytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        String resUri = new String(encoded);
        uri = Uri.parse(resUri);

        IVPreviewImage.setImageURI(uri);
    }

    public void onClickSave(View view) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setLastName(txtLastName.getText().toString());
        profileDTO.setFirstName(txtFirstName.getText().toString());
        profileDTO.setImageBase64(uriGetBase64(uri));

        CommonUtils.showLoading();
        ApplicationNetwork.getInstance()
                .getAccountApi()
                .updateProfile(profileDTO)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                String resp = response.errorBody().string();
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_RESULT) {
            uri = (Uri) data.getParcelableExtra("croppedUri");
            IVPreviewImage.setImageURI(uri);
        }
    }

    //Вибір фото і її обрізання
    public void onClickSelectImagePig(View view) {
        Intent intent = new Intent(this, ChangeImageActivity.class);
        startActivityForResult(intent, SELECT_IMAGE_RESULT);
    }
}