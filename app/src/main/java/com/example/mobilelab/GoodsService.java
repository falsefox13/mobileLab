package com.example.mobilelab;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GoodsService {
    @GET("goods/all")
    Call<List<Good>> listGoods();
}
