package com.spresto.righttobeforgotten.utils;

import android.content.Context;
import android.util.Log;

import com.spresto.righttobeforgotten.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by spresto on 2018-09-17.
 */

public class SexualSiteLists {
    private static final String TAG = SexualSiteLists.class.getSimpleName();
    /**
     *
     * https://www.pornhub.com/video?c=103 : Korea
     * https://www.pornhub.com/video?c=111 : Japan
     */
    public static ArrayList<String> keys = new ArrayList<>();
    public static HashMap<String, String> siteMap = new HashMap<>(); // key: default_site url , value: details_site url
    public static Map<String, String> siteMapValues = new HashMap<>(); // @string <key,value>

    public static void initialize(Context context, Set<String> sharedSet){
        String[] _keys = context.getResources().getStringArray(R.array.site_key);
        String[] _values = context.getResources().getStringArray(R.array.site_detail);
        for(int i = 0; i < _keys.length; i++){
            siteMapValues.put(_keys[i],_values[i]);
        }

        for(String url : sharedSet){
            Log.e(TAG, "url: "+url);
            keys.add(url);
        }

        for(int i = 0; i < keys.size(); i++){
            siteMap.put(keys.get(i),siteMapValues.get(keys.get(i)));
        }

        Log.e(TAG, "keys.size(): "+keys.size());
        Log.e(TAG, "siteMap.size(): "+siteMap.size());
    }

    public static void allClear(){
        keys.clear();
        siteMap.clear();
        siteMapValues.clear();
    }

}
