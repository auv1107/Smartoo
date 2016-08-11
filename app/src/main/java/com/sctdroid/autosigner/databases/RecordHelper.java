package com.sctdroid.autosigner.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sctdroid.autosigner.databases.CursorParser.CursorParser;
import com.sctdroid.autosigner.databases.CursorParser.RecordCursorParser;
import com.sctdroid.autosigner.databases.tables.RecordColumns;
import com.sctdroid.autosigner.models.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lixindong on 1/19/16.
 */
public class RecordHelper extends BaseHelper {
    public static CursorParser defaultParser = new RecordCursorParser();
    public static List<Record> query(Context context, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
                                      String orderBy, String limit){
        List<Record> result = Collections.EMPTY_LIST;
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        Cursor cursor = helper.query(table, columns, selection, selectionArgs,groupBy, having, orderBy, limit);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = new ArrayList<Record>(cursor.getCount());
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    result.add(fromCursor(cursor));
                }
            }
            cursor.close();
        }
        return result;
    }

    public static Record querySingle(Context context, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
                                      String orderBy, String limit){
        Record result = null;
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        Cursor cursor = helper.query(table, columns, selection, selectionArgs,groupBy, having, orderBy, limit);
        if (cursor != null) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    result = fromCursor(cursor);
                }
                cursor.close();
            }
        }
        return result;
    }

    public static List<Record> query(Context context) {
        return query(context, RecordColumns.TABLE_NAME, RecordColumns.QUERY_COLUMNS, null, null, null, null, RecordColumns.DEFAULT_SORT_ORDER, null);
    }

    public static List<Record> query(Context context, int start, int offset) {
        String limit = "" + start + "," + offset;
        return query(context, RecordColumns.TABLE_NAME, RecordColumns.QUERY_COLUMNS, null, null, null, null, RecordColumns.DEFAULT_SORT_ORDER, limit);
    }

    public static Record fromCursor(Cursor cursor) {
        return (Record) defaultParser.fromCursor(cursor);
    }


    public static long insert(Context context, Record record) {
        ContentValues values = getContentValues(record);
        return insert(context, RecordColumns.TABLE_NAME, values);
    }

    public static ContentValues getContentValues(Record record) {
        ContentValues values = new ContentValues();
        values.put(RecordColumns.TIME_STAMP, record.getTimestamp());
        values.put(RecordColumns.BEHAVIOR_TYPE, record.getBehavior_type());
        return values;
    }

    public static int delete(Context context, List<String> ids) {
        int result = 0;
        if (ids != null && ids.size() > 0) {
            int size = ids.size();
            DatabaseHelper helper = DatabaseHelper.getInstance(context);
            String[] selections = new String[size];
            String[][] selectionArgs = new String[size][];
            for (int i = 0; i < size; i++) {
                selections[i] = RecordColumns.ID + " = ?";
                selectionArgs[i] = new String[]{ids.get(i)};
            }
            result = helper.deleteBulk(RecordColumns.TABLE_NAME, selections, selectionArgs);
        }
        return result;
    }
}
