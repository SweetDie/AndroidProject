package com.example.firstproject.category;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firstproject.BaseActivity;
import com.example.firstproject.R;
import com.oginotihiro.cropview.CropView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CategoryCreateActivity extends BaseActivity {

    private ImageView resultIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);

        CropView cropView = (CropView) findViewById(R.id.cropView);
        Button buttonImage = (Button) findViewById(R.id.addImageButton);
        resultIv = (ImageView) findViewById(R.id.resultIv);

        buttonImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
    }

    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Uri imgUri = Uri.parse(result.toString());
            resultIv.setImageURI(imgUri);
        }
    });

}