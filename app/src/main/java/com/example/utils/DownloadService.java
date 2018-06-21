package com.example.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * author:王庆
 * date：On 2018/6/21
 */
public interface DownloadService {
    @Streaming
    @GET("01a3bd5737f2e4fcc0c1939b4798b259b3c31247e/com.supercell.clashroyale.mi.apk")
    Call<ResponseBody> downloadLargeAPK();
}
