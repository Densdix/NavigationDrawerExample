package com.leknos.navigationdrawerexample.ui;

import com.leknos.navigationdrawerexample.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ThePhotoDbApi {
    @GET("photos")
    Call<List<Photo>> getPhotos();
}
