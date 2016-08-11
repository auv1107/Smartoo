package com.sctdroid.autosigner.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sctdroid.autosigner.databases.tables.RecordColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "autosigner.db";

    public static final int DATABASE_VERSION_INIT = 1;

    public static final int DATABASE_VERSION = DATABASE_VERSION_INIT;

    private static DatabaseHelper mDatabaseHelper;

    public synchronized static DatabaseHelper getInstance(Context contex) {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(contex);
        }
        return mDatabaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        recreateDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int currentVersion = oldVersion;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**create table sql**/
        db.execSQL(RecordColumns.CREATE_TABLE_SQL);
    }

    private void recreateDB(SQLiteDatabase db){
        /** drop table **/
        db.execSQL("drop TABLE IF EXISTS " + RecordColumns.TABLE_NAME + ";");
        onCreate(db);
    }

    private void clearDB(SQLiteDatabase db){
        /** delete data from table */
        db.execSQL("DELETE FROM " + RecordColumns.TABLE_NAME + ";");
    }

    public void clearDB(){
        SQLiteDatabase db = getWritableDatabase();
        clearDB(db);
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        return query(table, columns, selection, selectionArgs, null, null, orderBy, null);
    }

    /**
     * add synchronized in case of multi thread operations on database.
     */
    public synchronized Cursor query(String table, String[] columns, String selection,
                                     String[] selectionArgs, String groupBy, String having,
                                     String orderBy, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return cursor;
    }

    public long insert(String table, ContentValues values) {
        return insert(table, null, values);
    }

    public synchronized long insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insertWithOnConflict(table, nullColumnHack, values, SQLiteDatabase.CONFLICT_REPLACE);
        //db.close();
        return id;
    }

    public int insert(String table, ContentValues[] values) {
        return insertBulk(table, null, values);
    }

    public synchronized int insertBulk(String table, String nullColumnHack, ContentValues[] values) {
        SQLiteDatabase db = getWritableDatabase();
        int affected = 0;
        try {
            db.beginTransaction();
            for (ContentValues value : values) {
                db.insertWithOnConflict(table, nullColumnHack, value, SQLiteDatabase.CONFLICT_REPLACE);
                affected++;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            affected = 0;
            e.printStackTrace();
        } finally {
            db.endTransaction();
            //db.close();
        }
        return affected;
    }

    public synchronized int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int affected = db.update(table, values, whereClause, whereArgs);
        //db.close();
        return affected;
    }

    public int update(String table, ContentValues[] values, String[] whereClause, String[][] whereArgs) {
        return updateBulk(table, values, whereClause, whereArgs);
    }

    private synchronized int updateBulk(String table, ContentValues[] values, String[] whereClause, String[][] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int affected = 0;
        try {
            db.beginTransaction();
            for (int i = 0; i < values.length; i++) {
                db.update(table, values[i], whereClause[i], whereArgs[i]);
                affected++;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            affected = 0;
            e.printStackTrace();
        } finally {
            db.endTransaction();
            //db.close();
        }
        return affected;
    }

    public synchronized int delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int affected = db.delete(table, whereClause, whereArgs);
        //db.close();
        return affected;
    }

    public synchronized int deleteBulk(String table, String[] whereClause, String[][] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int affected = 0;
        try {
            db.beginTransaction();
            for (int i = 0; i < whereClause.length; i++) {
                db.delete(table, whereClause[i], whereArgs[i]);
                affected++;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            affected = 0;
            e.printStackTrace();
        } finally {
            db.endTransaction();
            //db.close();
        }
        return affected;
    }

    protected Cursor rawQuery(String sql){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery(sql, null);
    }

    protected void executeBatchSql(String[] sqls){
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.beginTransaction();
            for(String sql : sqls){
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }
}
