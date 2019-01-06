package com.example.sanket.mvcdemo.controller;

import com.example.sanket.mvcdemo.model.pojo.Photo;

public class ControllerDetailActivity {
    DetailActivityListener detailActivityListener;

    public ControllerDetailActivity(DetailActivityListener detailActivityListener) {
        this.detailActivityListener = detailActivityListener;
    }

    public void setPhotoInfo(Photo photo){
        detailActivityListener.setInfo(photo);
    }

    public interface DetailActivityListener{
        void setInfo(Photo photo);
    }
}
