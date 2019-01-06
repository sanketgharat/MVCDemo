package com.example.sanket.mvcdemo.controller;

import android.content.Context;
import android.util.Log;

import com.example.sanket.mvcdemo.model.api.ApiManager;
import com.example.sanket.mvcdemo.model.pojo.Photo;
import com.example.sanket.mvcdemo.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Controller {
    PhotoFetchListener photoFetchListener;
    public static final String TAG = Controller.class.getSimpleName()+"Tag";

    public Controller(PhotoFetchListener photoFetchListener) {
        this.photoFetchListener = photoFetchListener;
    }

    public void searchAlbumById(String searchValue){

        photoFetchListener.onPreFetching();
        if(!Functions.isInteger(searchValue)){
            photoFetchListener.onFetchFailed(0,"Enter Numbers");
            return;
        }
        Call<Photo> call =  ApiManager.getApiInstance().getAlbumPhotoById(Integer.parseInt(searchValue));
        call.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                Log.d(TAG,"onResponse: "+response.code());
                if(!response.isSuccessful()){
                    photoFetchListener.onFetchFailed(response.code(),"Error "+response.code());

                    return;
                }
                Photo photo = response.body();

                if(photo == null){
                    photoFetchListener.onFetchFailed(response.code(),"No data found");
                    return;
                }
                List<Photo> list = new ArrayList<>();
                list.add(photo);
                Log.d(TAG,"onResponse: "+list.size()+" Record(s)");
                photoFetchListener.onFetchComplete(list);
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {
                photoFetchListener.onFetchFailed(0,t.getMessage());
                Log.d(TAG,"onFailure: "+t.getMessage());
            }
        });

    }

    public void getAlbumPhotos(String sortBy, String order){
        Log.d(TAG,"getAlbumPhotos: sortBy="+sortBy+" order="+order);
        photoFetchListener.onPreFetching();

        if(!Functions.isNetworkAvailable((Context) photoFetchListener)){
            photoFetchListener.onFetchFailed(0,"No internet connection!");
            return;
        }

        Call<List<Photo>> call = ApiManager.getApiInstance().getAlbumPhotos(sortBy,order);
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                Log.d(TAG,"onResponse: code "+response.code());
                if(!response.isSuccessful()){
                    photoFetchListener.onFetchFailed(response.code(),"Error");

                    return;
                }
                List<Photo> photos = response.body();
                if(photos==null){
                    photoFetchListener.onFetchFailed(response.code(),"No data found");
                }else{
                    if(photos.size()>0){
                        photos = photos.subList(0,100);
                        Log.d(TAG,"onResponse: "+photos.size()+" Record(s)");
                        photoFetchListener.onFetchComplete(photos);
                    }else{
                        photoFetchListener.onFetchFailed(response.code(),"No data found");

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                photoFetchListener.onFetchFailed(0,t.getMessage());
                Log.d(TAG,"onFailure: "+t.getMessage());
            }
        });
    }

    public void addAlbum(List<Photo> listTemp, int albumId, String title, String url, String thumbnailUrl){

        int id=listTemp.size()+1;
        Photo photo = new Photo(id, albumId, title, url, thumbnailUrl);
        listTemp.add(photo);
        photoFetchListener.onFetchComplete(listTemp);

    }

    public interface PhotoFetchListener{
        void onPreFetching();
        void onFetchComplete(List<Photo> mPhotoList);
        void onFetchFailed(int code, String response);
    }
}
