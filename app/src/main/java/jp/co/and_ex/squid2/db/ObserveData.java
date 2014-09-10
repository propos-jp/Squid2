package jp.co.and_ex.squid2.db;

import android.net.Uri;
import android.provider.BaseColumns;
import jp.co.and_ex.squid2.R;

/**
 * Created by obata on 2014/09/08.
 */
public class ObserveData {
      public static final String TABLE_OBSERVE_DATA = "observe_data";
      public static final String AUTHORITY = "jp.co.and_ex.squid.provider";

      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.squid.observe_data";
      public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.squid.observe_data";
      // Columns names
      public static final String KEY_ID = BaseColumns._ID;
      public static final String KEY_GLOBAL_ID = "global_id";
      public static final String KEY_OBSERVE_DATE = "observe_date";
      public static final String KEY_LATITUDE = "latitude";
      public static final String KEY_LONGITUDE = "longitude";
      public static final String KEY_USER_ID = "user_id";
      public static final String KEY_DATA = "data";


}
