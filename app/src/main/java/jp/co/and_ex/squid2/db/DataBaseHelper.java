package jp.co.and_ex.squid2.db;

import android.content.Context;

import java.io.IOException;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import jp.co.and_ex.squid2.R;


/**
 * Created by obata on 2014/09/08.
 */
public class DataBaseHelper extends SQLiteAssetHelper {

    public DataBaseHelper(Context context) throws IOException {
        super(context, context.getResources().getString(R.string.database_name), null, context.getResources().getInteger(R.integer.database_version));

    }
}