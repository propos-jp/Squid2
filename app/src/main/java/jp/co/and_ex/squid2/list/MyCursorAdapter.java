package jp.co.and_ex.squid2.list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import jp.co.and_ex.squid2.db.ObserveDataContract;

/**
 * Created by obata on 2014/09/24.
 */
public class MyCursorAdapter extends SimpleCursorAdapter {
    static final String TAG = MyCursorAdapter.class.getSimpleName();
    private String myId;

    public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG,cursor.getString(ObserveDataContract.FIELD_ORDER.USER_ID.ordinal()));
        if (myId != null ){
            if (myId.equals(cursor.getString(ObserveDataContract.FIELD_ORDER.USER_ID.ordinal()))){
                view.setBackgroundColor(Color.rgb(0,127,255));
            }else{
                 view.setBackgroundColor(Color.rgb(255,105,95));
            }
        }

        super.bindView(view, context, cursor);
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
