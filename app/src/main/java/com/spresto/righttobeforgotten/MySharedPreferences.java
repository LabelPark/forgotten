package com.spresto.righttobeforgotten;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by spresto on 2018-10-04.
 */

public class MySharedPreferences {

    private static String PREF_WHOLE_SITE = "pref_whole_site";
    private static String CHECK_SITE_URL = "check_site_url";

    public static void setSiteUrl(Context context, Set<String> mSet){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(CHECK_SITE_URL, mSet);
        editor.apply();
    }
    public static Set<String> getSiteUrl(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getStringSet(CHECK_SITE_URL, null);
    }

    public static void deleteSiteUrl(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove(CHECK_SITE_URL).commit();
    }


    public static void setPref(Context context, boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_WHOLE_SITE, isEnabled);
        editor.apply();
    }

    public static boolean getPref(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_WHOLE_SITE, false);
    }
}
