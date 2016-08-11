package com.sctdroid.autosigner.databases.CursorParser;

import android.database.Cursor;

/**
 * Created by lixindong on 12/3/15.
 */
public interface CursorParser<T> {
    T fromCursor(Cursor cursor);
}
