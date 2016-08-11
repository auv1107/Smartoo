package com.sctdroid.autosigner.databases.CursorParser;

import android.database.Cursor;

import com.sctdroid.autosigner.databases.tables.RecordColumns;
import com.sctdroid.autosigner.models.Record;

/**
 * Created by lixindong on 1/19/16.
 */
public class RecordCursorParser implements CursorParser<Record> {
    @Override
    public Record fromCursor(Cursor cursor) {
        Record record = new Record();
        record.setId(cursor.getString(RecordColumns.ID_INDEX));
        record.setBehavior_type(cursor.getString(RecordColumns.BEHAVIOR_TYPE_INDEX));
        record.setTimestamp(cursor.getString(RecordColumns.TIME_STAMP_INDEX));
        return record;
    }
}
