package com.jjs.zero.utillibrary;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2017/2/19.
 */

public class DensityUtil {


    public static int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return ( int ) (var1 * var2 + 0.5F);
    }

    public static int dp2px(float value) {
        final float scale = Resources.getSystem().getDisplayMetrics().densityDpi;
        return ( int ) (value * (scale / 160) + 0.5f);
    }

    public static int px2dip(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return ( int ) (var1 / var2 + 0.5F);
    }

    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return ( int ) (var1 * var2 + 0.5F);
    }

    public static int px2sp(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return ( int ) (var1 / var2 + 0.5F);
    }

    public static int getWidth(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
//        int heightPixels = outMetrics.heightPixels;
        return widthPixels;
    }
}
