package com.sctdroid.autosigner.databases;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by yangyang on 15-11-13.
 */
public class BaseHelper {

    public static long insert(Context context, String table, ContentValues values){
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        return helper.insert(table, values);
    }

    public static int insertBulk(Context context, String table, ContentValues[] values){
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        return helper.insertBulk(table, null, values);
    }

    /**
     * delete all.
     *
     * @param context
     * @return
     */
    public static int delete(Context context, String table) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        return helper.delete(table, null, null);
    }
}
