package vn.soaap.onlinepharmacy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.entities.ImageHandle;

public class ImageInputActivity extends AppCompatActivity{

    String UPLOAD_URL = "http://192.168.1.17/oppa/test";
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;
    ImageView ivImage;
    Bitmap bitmap = null;
    ImageHandle imageHandle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_input);
        initView();
        clickTakePhoto();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Button btnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);
//        Button btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
//        btnSelectPhoto.setOnClickListener(this);
//        btnTakePhoto.setOnClickListener(this);

        ivImage = (ImageView) findViewById(R.id.ivImage);

        //  send photo
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageHandle != null) {
                    imageHandle.uploadImage(UPLOAD_URL);
//                    startActivity(new Intent(ImageInputActivity.this,InfoInputActivity.class));
                }
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btnSelectPhoto:
////                clickSelectPhoto();
//                break;
//            case R.id.btnTakePhoto:
////                clickTakePhoto();
//                break;
//        }
//    }

    private void clickTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


//    private void clickSelectPhoto() {
//        Intent intent = new Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        intent.setType("image/*");
//
//        startActivityForResult(
//                Intent.createChooser(intent, "Select File"),
//                SELECT_FILE);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == SELECT_FILE)
                imageHandle = new ImageHandle(ImageInputActivity.this, data, 1);
            else if (requestCode == REQUEST_CAMERA)
                imageHandle = new ImageHandle(ImageInputActivity.this, data, 2);

            bitmap = imageHandle.getBitmap();
            ivImage.setImageBitmap(bitmap);
        }

    }

}
