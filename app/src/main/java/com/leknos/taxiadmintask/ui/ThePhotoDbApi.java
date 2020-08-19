package com.leknos.taxiadmintask.ui;

import com.leknos.taxiadmintask.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ThePhotoDbApi {
    @GET("photos")
    Call<List<Photo>> getPhotos();
}
