package com.hongfei02chen.wxautoatreply.network.okhttp;

import android.util.Log;

import com.hongfei02chen.wxautoatreply.MyApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * created by chenhongfei on 2018/5/14
 */
public class OkHttpUtils {
    public static String requestUrl = "http://publicobject.com/helloworld.txt";

    public static void addQueue(String group, String nickname) {
        String url = requestUrl + "?group=" + group + "&nickname=" + nickname;
        request(url);
    }


    public static void request(String url)   {
        Request request = new Request.Builder()
                .url(url)
                .build();

        MyApplication.getmHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("json", "IOException:" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("json", "sucess body:" + response.body().string());
            }

        });
    }
}
