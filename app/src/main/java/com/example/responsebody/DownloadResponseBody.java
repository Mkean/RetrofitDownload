package com.example.responsebody;

import android.support.annotation.Nullable;

import com.example.bean.DownloadProgressEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * author:王庆
 * date：On 2018/6/21
 */
public class DownloadResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public DownloadResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(BufferedSource source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);//每次读取的字节
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                DownloadProgressEvent downloadProgressEvent = new DownloadProgressEvent();
                downloadProgressEvent.setBytesRead(totalBytesRead);
                downloadProgressEvent.setContentLength(responseBody.contentLength());
                downloadProgressEvent.setDone(bytesRead == -1);
                EventBus.getDefault().post(downloadProgressEvent);
                return bytesRead;
            }
        };
    }
}
