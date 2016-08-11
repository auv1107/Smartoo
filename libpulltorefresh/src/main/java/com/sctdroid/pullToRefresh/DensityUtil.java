package com.sctdroid.pullToRefresh;

import android.content.Context;

/**
 * 创建人： limeng
 * 日期： 11/19/15
 */
public class DensityUtil {
    public static int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
