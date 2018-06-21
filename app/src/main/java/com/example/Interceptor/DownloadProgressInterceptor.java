package com.example.Interceptor;

import com.example.responsebody.DownloadResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author:王庆
 * date：On 2018/6/21
 */
public class DownloadProgressInterceptor implements Interceptor {
    public DownloadProgressInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Response build = response.newBuilder().body(new DownloadResponseBody(response.body())).build();
        return build;
    }
}
