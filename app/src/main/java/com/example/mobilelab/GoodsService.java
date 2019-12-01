package com.example.mobilelab;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GoodsService {
    @GET("goods/all")
    Call<List<Good>> listGoods();

    @POST("goods_android/")
    Call<Good> addGood(@Body Good good);
}
