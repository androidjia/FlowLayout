package com.jjs.zero.httplibrary.common.service;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class BaseManager {
    private Retrofit retrofit;
    private Gson gson;
    private String token, userCode;
    private ConfigUrl configUrl;
    private Context mContext;
    public BaseManager(Context mContext) {
        this(mContext,null);
    }

    public BaseManager(Context mContext,Gson gson) {
        this.mContext = mContext;
        this.gson = gson == null ? new Gson():gson;
        configUrl = ConfigUrl.load(mContext);
        initRectofit();
    }

    public BaseManager(Context mContext,String token,String userCode) {
        this(mContext);
        this.token = token;
        this.userCode = userCode;
    }

    private void initRectofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl.Builder urlBuilder = originalHttpUrl.newBuilder();
                    if (!TextUtils.isEmpty(token)) {
                        urlBuilder.addQueryParameter("accessToken", token);
                    }
                    if (!TextUtils.isEmpty(userCode)) {
                        urlBuilder.addQueryParameter("userCode", userCode);
                    }
                    HttpUrl url = urlBuilder.build();
                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .method(original.method(), original.body())
                            .url(url);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(configUrl.getUrl())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public <T> T getInterface(Class<T> tClass) {
        return retrofit.create(tClass);
    }
}
