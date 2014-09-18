package jp.co.and_ex.squid2.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
