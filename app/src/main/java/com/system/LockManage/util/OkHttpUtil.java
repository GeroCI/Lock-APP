package com.system.LockManage.util;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    private static OkHttpUtil myOkHttpClient;
    private    OkHttpClient okHttpClient;
    private Handler handler;

    private OkHttpUtil() {
        okHttpClient = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtil getInstance() {
        if (myOkHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (myOkHttpClient == null) {
                    myOkHttpClient = new OkHttpUtil();
                }
            }
        }
        return myOkHttpClient;
    }

    class StringCallBack implements Callback {
        private HttpCallBack httpCallBack;
        private Request request;

        public StringCallBack(Request request, HttpCallBack httpCallBack) {
            this.request = request;
            this.httpCallBack = httpCallBack;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final IOException fe = e;
            if (httpCallBack != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onError(request, fe);
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String result = response.body().string();
            if (httpCallBack != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onSuccess(request, result);
                    }
                });
            }
        }
    }

    public void asyncGet(String url, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }


    public void asyncPost(String url, RequestBody requestBody, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }

    public interface HttpCallBack {
        void onError(Request request, IOException e);

        void onSuccess(Request request, String result);
    }
}