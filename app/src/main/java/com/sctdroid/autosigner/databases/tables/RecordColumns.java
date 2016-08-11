package com.sctdroid.autosigner.databases.tables;

import android.provider.BaseColumns;

/**
 * Created by lixindong on 1/19/16.
 */
public class RecordColumns implements BaseColumns {
    public static final String ID = "id";
    public static final String TIME_STAMP = "time_stamp";
    public static final String BEHAVIOR_TYPE = "behavior_type";

    public static final int ID_INDEX = 0;
    public static final int TIME_STAMP_INDEX = 1;
    public static final int BEHAVIOR_TYPE_INDEX = 2;

    public static final String TABLE_NAME = "records";

    public static final String CREATE_TABLE_SQL ="CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//            ID + " TEXT UNIQUE," +
            TIME_STAMP + " TEXT," +
            BEHAVIOR_TYPE + " TEXT" +
            ");";

    public static final String[] QUERY_COLUMNS = new String[] {
            _ID, TIME_STAMP, BEHAVIOR_TYPE
    };
    public static final String DEFAULT_SORT_ORDER = "CAST(" + TIME_STAMP  + " AS DECIMAL) DESC";
}
