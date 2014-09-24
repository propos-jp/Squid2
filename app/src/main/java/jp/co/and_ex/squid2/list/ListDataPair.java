package jp.co.and_ex.squid2.list;

import android.util.Pair;

/**
 * Created by obata on 2014/09/24.
 */
public class ListDataPair extends Pair<String,String> {

    public ListDataPair(String key, String value) {
        super(key, value);
    }

    public String getKey(){
        return super.first;
    }

    public String getValue(){
        return super.second;
    }
}

