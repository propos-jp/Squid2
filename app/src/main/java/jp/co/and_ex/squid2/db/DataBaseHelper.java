package jp.co.and_ex.squid2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

import jp.co.and_ex.squid2.util.Common;


/**
 * Created by obata on 2014/09/08.
 */
public class DataBaseHelper extends SQLiteOpenHelper  {

    //@formatter:on
    private static final String DROP_PLACE_TABLE_SQL = "DROP TABLE IF EXISTS " + ObserveData.TABLE_OBSERVE_DATA;
    // create sql
    private String create_sql = null;

    public DataBaseHelper(Context context) throws IOException {
        super(context, ObserveData.DATABASE_NAME, null, ObserveData.DATABASE_VERSION);

         create_sql = Common.readAssets(context,ObserveData.CREATE_SQL);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(DROP_PLACE_TABLE_SQL);

        // Create tables again
        onCreate(db);
    }


}