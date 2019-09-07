package com.example.sanket.mvcdemo.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.sanket.mvcdemo.R;
import com.example.sanket.mvcdemo.utils.Constants;
import com.example.sanket.mvcdemo.utils.Functions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.security.Permission;

public class AddPhotoActivity extends AppCompatActivity {

    @BindView(R.id.editTextAlbum)
    EditText editTextAlbum;
    @BindView(R.id.editTextTitle)
    EditText editTextTitle;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageViewPhoto)
    ImageView imageViewPhoto;

    private static final int REQ_CODE_GALLARY = 1;
    private static final int REQ_CODE_CAMERA = 1;
    private static final int REQ_CODE_PERMISSION_CAMERA = 1;

    Uri imageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        ButterKnife.bind(this);

        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });
    }

    @OnClick(R.id.buttonAdd)
    public void addPhoto(View v) {
        String albumId = editTextAlbum.getText().toString();
        String title = editTextTitle.getText().toString();
        if(TextUtils.isEmpty(albumId) || TextUtils.isEmpty(title)){
            Functions.showSnackBar(v,"Enter data");
            return;
        }
        if(!Functions.isInteger(albumId)){
            Functions.showSnackBar(v,"Album id should be a number");
            return;
        }
        String url=Constants.PHOTO_URL_TEMP;
        Intent intent = new Intent();
        intent.putExtra(Constants.P_ALBUM_ID,Integer.parseInt(albumId));
        intent.putExtra(Constants.P_TITLE,title);
        intent.putExtra(Constants.P_URL,url);
        intent.putExtra(Constants.P_THUMBNAIL_URL,url);
        setResult(1,intent);
        finish();
    }

    @OnClick(R.id.imageViewPhoto)
    public void selectImage(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Image Selector")
                .setMessage("Capture image or pick from gallary")
                .setPositiveButton("Gallary", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pickImage();
                    }
                })
                .setNegativeButton("Capture Image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureImage();
                    }
                })
                .setNeutralButton("Cancel", null );
        builder.show();

    }

    private void captureImage() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddPhotoActivity.this,new String[]{Manifest.permission.CAMERA},REQ_CODE_PERMISSION_CAMERA);

        }else {
            Intent intent= new Intent();
            intent.setAction("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent,REQ_CODE_CAMERA);
        }

    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_GALLARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if(requestCode == REQ_CODE_GALLARY && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageURI = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURI);
                imageViewPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode == REQ_CODE_CAMERA && resultCode == RESULT_OK){
            imageURI = data.getData();
            bitmap = (Bitmap) data.getExtras().get("data");
            imageViewPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && requestCode == REQ_CODE_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            captureImage();
        }
    }
}
