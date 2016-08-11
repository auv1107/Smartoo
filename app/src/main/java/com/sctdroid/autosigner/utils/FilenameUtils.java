package com.sctdroid.autosigner.utils;

import android.text.TextUtils;

/**
 * Created by lixindong on 1/27/16.
 */
public class FilenameUtils {
    public static String getBasename(String url) {
        if (TextUtils.isEmpty(url)) return "";

        int index = url.lastIndexOf("/");
        if (index == -1) {
            return url;
        } else {
            return url.substring(index+1);
        }
    }

    public static String getPath(String url) {
        if (TextUtils.isEmpty(url)) return "";

        int index = url.lastIndexOf("/");
        if (index == -1) {
            return url;
        } else {
            return url.substring(0, index);
        }
    }
}
