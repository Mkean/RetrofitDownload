package com.example.retrofitdownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bean.DownloadProgressEvent;
import com.example.utils.DownloadService;
import com.example.utils.RetrofitManger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBt;
    private TextView mTv;
    private ProgressBar mBar;
    private String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/retrofit/b1";

    private File downloadFile;
    private DownloadService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Log.e("TAG", savePath);
        final File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        downloadFile = new File(file, "RoyalWar.apk");
        service = RetrofitManger.createService();
        mBt.setOnClickListener(this);

    }

    private void initView() {
        mBt = (Button) findViewById(R.id.bt);
        mTv = (TextView) findViewById(R.id.tv);
        mBar = (ProgressBar) findViewById(R.id.bar);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            downloadFile.delete();
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        // 注册一个广播
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解除广播
        unregisterReceiver(mBroadcastReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadProgressUpdate(DownloadProgressEvent downloadProgressEvent) {
        long totalBytesRead = (downloadProgressEvent.getBytesRead() * 100) / downloadProgressEvent.getContentLength();

        if (downloadProgressEvent.isDone()) {
            mBt.setText("下载完成");
            Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
            installApk();
        } else {
            mTv.setText("已经完成" + totalBytesRead + "%");
            mBar.setProgress((int) totalBytesRead);
        }
    }

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
        startActivityForResult(intent, 1000);

    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
        Call<ResponseBody> call = service.downloadLargeAPK();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    BufferedSink sink = null;
                    try {
                        sink = Okio.buffer(Okio.sink(downloadFile));
                        sink.writeAll(response.body().source());
                    } catch (Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                    } finally {
                        if (sink != null) {
                            try {
                                sink.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("下载失败", t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
