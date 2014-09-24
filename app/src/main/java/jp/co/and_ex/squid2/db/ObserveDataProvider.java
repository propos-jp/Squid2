package jp.co.and_ex.squid2.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

public class ObserveDataProvider extends ContentProvider {

    private static final int OBSERVE_DATA = 1;
    private static final int KEY_ID = 2;
    private static final int GLOBAL_ID = 3;


    public static final Uri CONTENT_URI = ObserveDataContract.CONTENT_URI;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ObserveDataContract.AUTHORITY, "observe_data", OBSERVE_DATA);
        URI_MATCHER.addURI(ObserveDataContract.AUTHORITY, "observe_data/#", KEY_ID);

    }

    private DataBaseHelper mDBHelper;

    @Override
    public boolean onCreate() {

        try {
            mDBHelper = new DataBaseHelper(getContext());
            return true;
        } catch (IOException e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;

        switch (URI_MATCHER.match(uri)) {
            case OBSERVE_DATA:
                count = db.delete(ObserveDataContract.TABLE_OBSERVE_DATA, " " +
                        ObserveDataContract.KEY_ID + " like '%'", null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        //@formatter:off
        switch (URI_MATCHER.match(uri)) {
            case OBSERVE_DATA:
                return ObserveDataContract.CONTENT_TYPE; // expect the Cursor to contain 0..x items
            case KEY_ID:
                return ObserveDataContract.CONTENT_ITEM_TYPE; // expect the Cursor to contain 1 item
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
        //@formatter:on
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (URI_MATCHER.match(uri) != OBSERVE_DATA) {
            throw new IllegalArgumentException("Unknown URL *** " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // 追加、又はplace_idが重複の場合は更新
        long rowID = db.replace(ObserveDataContract.TABLE_OBSERVE_DATA, "NULL", values);

        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(
                    CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ObserveDataContract.TABLE_OBSERVE_DATA);

        Log.d("query", uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case OBSERVE_DATA:
                break;

            case KEY_ID:
                qb.appendWhere(ObserveDataContract.KEY_ID + "="
                        + uri.getPathSegments().get(1));
                break;
            default:

        }
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = ObserveDataContract.KEY_OBSERVE_DATE + " DESC"; // 新しい順
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        switch (URI_MATCHER.match(uri)) {
            case OBSERVE_DATA:
                count = db.update(ObserveDataContract.TABLE_OBSERVE_DATA, values, where, whereArgs);
                break;

            case KEY_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(ObserveDataContract.TABLE_OBSERVE_DATA, values,
                        ObserveDataContract.KEY_ID + "="
                                + id
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
