package jp.co.and_ex.squid2.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by obata on 2014/09/09.
 */
public class Common {
    public static String readAssets(Context context, String path) throws IOException {
        AssetManager as = context.getResources().getAssets();

        InputStream is = null;
        BufferedReader br = null;

        StringBuilder sb = new StringBuilder();
        try {
            try {
                is = as.open(path);
                br = new BufferedReader(new InputStreamReader(is));

                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str + "\n");
                }
            } finally {
                if (br != null) br.close();
            }
        } catch (IOException e) {
            throw e;
        }
        return sb.toString();
    }

    public static String getJST(String utc)
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

            return jst;
        } catch (ParseException e) {

        }
        return utc;
    }
}
