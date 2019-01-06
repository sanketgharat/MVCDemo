package com.example.sanket.mvcdemo.view;

import android.content.Intent;
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

public class AddPhotoActivity extends AppCompatActivity {

    @BindView(R.id.editTextAlbum)
    EditText editTextAlbum;
    @BindView(R.id.editTextTitle)
    EditText editTextTitle;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
}
