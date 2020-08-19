package com.leknos.taxiadmintask.utils;

import com.leknos.taxiadmintask.ui.ThePhotoDbApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    public static NetworkService getInstance(){
        if(mInstance == null){
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ThePhotoDbApi getJSONApi() {
        return mRetrofit.create(ThePhotoDbApi.class);
    }
}
