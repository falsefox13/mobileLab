package com.example.mobilelab;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private GoodsService apiService;
    private FirebaseAuth auth;

    public void onCreate() {
        super.onCreate();
        auth = FirebaseAuth.getInstance();
        apiService = createApiService();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public GoodsService getApiService() {
        return apiService;
    }

    private GoodsService createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://bowling-iot.pp.ua")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GoodsService.class);
    }
}
