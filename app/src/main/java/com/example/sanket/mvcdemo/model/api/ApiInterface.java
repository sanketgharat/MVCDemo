package com.example.sanket.mvcdemo.model.api;

import com.example.sanket.mvcdemo.model.pojo.Photo;
import com.example.sanket.mvcdemo.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(Constants.ALBUM_PHOTO_URL)
    Call<List<Photo>> getAlbumPhotos();

    @GET(Constants.ALBUM_PHOTO_URL_ID)
    Call<Photo> getAlbumPhotoById(@Path("id") int id);

    @GET(Constants.ALBUM_PHOTO_URL)
    Call<Photo> getAlbumPhotoByQuery(@Query("id") Integer id,@Query("albumId") Integer albumId);

    @GET(Constants.ALBUM_PHOTO_URL)
    Call<List<Photo>> getAlbumPhotos(@Query("_sort") String sort,@Query("_order") String order);

}
