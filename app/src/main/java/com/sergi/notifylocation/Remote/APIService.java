package com.sergi.notifylocation.Remote;

import com.sergi.notifylocation.Models.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @POST("/history")
    @FormUrlEncoded
    Call<Log> saveLog(@Field("usuari") String usuari,
                      @Field("location") String location,
                      @Field("userTime") String userTime);

    @GET("/history/android")
    Call<List<Log>> getLog(@Query("usuari") String usuari);
}