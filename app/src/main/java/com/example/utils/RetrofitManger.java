package com.example.utils;

import com.example.Interceptor.DownloadProgressInterceptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author:王庆
 * date：On 2018/6/21
 */
public class RetrofitManger {


    public static DownloadService createService() {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(5, TimeUnit.SECONDS)//
                .connectTimeout(5, TimeUnit.SECONDS)//
                .addInterceptor(new DownloadProgressInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://f5.market.xiaomi.com/download/AppStore/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .callbackExecutor(newFixedThreadPool)
                .build();
        DownloadService apiService = retrofit.create(DownloadService.class);
        return apiService;
    }
}
