package jp.co.and_ex.squid2.list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jp.co.and_ex.squid2.db.ObserveDataContract;

/**
 * Created by obata on 2014/09/24.
 */
public class MyCursorAdapter extends SimpleCursorAdapter implements SimpleCursorAdapter.ViewBinder {
    static final String TAG = MyCursorAdapter.class.getSimpleName();
    private String myId;

    public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        setViewBinder(this);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, cursor.getString(ObserveDataContract.FIELD_ORDER.USER_ID.ordinal()));
        if (myId != null) {
            if (myId.equals(cursor.getString(ObserveDataContract.FIELD_ORDER.USER_ID.ordinal()))) {
                view.setBackgroundColor(Color.rgb(0, 127, 255));
            } else {
                view.setBackgroundColor(Color.rgb(255, 105, 95));
            }
        }
        TextView content = (TextView) view.findViewById(android.R.id.text1);

        String utc = cursor.getString(ObserveDataContract.FIELD_ORDER.OBSERVE_DATE.ordinal());
        if (utc != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date utcDate = null;


                utcDate = sdf.parse(utc);

                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("JST"));
                String jst = sdf.format(utcDate);
                content.setText(jst);
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
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

    @Override
    public boolean setViewValue(View view, Cursor cursor, int column) {
        int dateColumn = cursor.getColumnIndex(ObserveDataContract.KEY_OBSERVE_DATE);
        if (column == dateColumn) {
            String dateString = cursor.getString(dateColumn);
            ((TextView)view).setText(getJST(dateString));

            return true; // Return true to show you've handled this column
        }

        return false;
    }

    private String getJST(String utc)
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date gmtDate = sdf.parse(utc);

            Calendar cal = Calendar.getInstance();

            cal.setTime(gmtDate);
            cal.add(Calendar.HOUR,9);

            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("JST"));
            String jst = sdf.format(cal.getTime());
            Log.d(TAG,utc + " -> " + jst);
            return jst;
        } catch (ParseException e) {
            Log.d(TAG,e.getMessage());
        }
        return utc;
    }
}

