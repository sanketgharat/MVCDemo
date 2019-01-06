package com.example.sanket.mvcdemo.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanket.mvcdemo.R;
import com.example.sanket.mvcdemo.controller.ControllerDetailActivity;
import com.example.sanket.mvcdemo.model.pojo.Photo;
import com.example.sanket.mvcdemo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements ControllerDetailActivity.DetailActivityListener {

    @BindView(R.id.imageViewPhoto)
    ImageView imageViewPhoto;
    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.textViewId)
    TextView textViewId;
    @BindView(R.id.textViewAlbumId)
    TextView textViewAlbumId;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ControllerDetailActivity controllerDetailActivity;
    Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        configView();
        controllerDetailActivity = new ControllerDetailActivity(this);
        photo =(Photo) getIntent().getSerializableExtra(Constants.PHOTO_TAG);

        if (photo != null){
            controllerDetailActivity.setPhotoInfo(photo);
        }
    }

    private void configView() {
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


    @Override
    public void setInfo(Photo photo) {
        Glide.with(DetailActivity.this)
                .load(photo.getUrl())
                .into(imageViewPhoto);

        textViewTitle.setText(photo.getTitle());
        textViewId.setText("ID: " +String.valueOf(photo.getId()));
        textViewAlbumId.setText("Album: "+String.valueOf(photo.getAlbumId()));
    }
}
