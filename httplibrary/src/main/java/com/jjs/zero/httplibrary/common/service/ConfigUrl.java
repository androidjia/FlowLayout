package com.jjs.zero.httplibrary.common.service;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class ConfigUrl {

    private static ConfigUrl configUrl;
    private static Context context;
    private String url = "";
    private ConfigUrl(Context context) {
        if (context == null) return;
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            url = applicationInfo.metaData.getString("BASE_URL","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ConfigUrl load(Context context) {
        if (configUrl == null) {
            configUrl = new ConfigUrl(context);
        }
        return configUrl;
    }

    public static void init(Context contexts) {
        context = contexts;
        configUrl = new ConfigUrl(context);
    }

    public String getUrl(){
        return url;
    }

}
