package com.example.sanket.mvcdemo.model.api;

import com.example.sanket.mvcdemo.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static Retrofit retrofit = null;
    private static ApiInterface apiInterface = null;

    private static Retrofit getRetrofitInstance(){
        if (retrofit!=null){
            return retrofit;
        }else{
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .build();
            return retrofit;
        }

    }

    public static ApiInterface getApiInstance(){
        if (apiInterface != null){
            return apiInterface;
        }
        apiInterface = getRetrofitInstance().create(ApiInterface.class);
        return apiInterface;
    }
}
