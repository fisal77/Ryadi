package com.appz_world_hackathon.ryadi.AppUtil.mApp;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {

    private final static String SHARED_FILE = "_dataFile";

    private static final String LANG = "lang";

    private SharedPreferences pref ;

    protected static final String KEY = "user-session";

    private static final String THEME_TYPE = "theme_type";

    public AppSharedPreferences(Context context) {
        pref = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
    }


    public static String restoreLanguage(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        String id = savedSession.getString(LANG, "");
        return id;
    }

    public static boolean saveLanguage(String lang, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(LANG, lang);
        return editor.commit();
    }


    public void writeString(String key, String value) {

        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();

    }
    public String readString(String key) {
        return pref.getString(key, "");
    }

    public void writeInteger(String key, int value) {

        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt(key, value);
        prefEditor.commit();

    }

    public int readInteger(String key) {
        return pref.getInt(key, 0);
    }






    public void writeBoolean(String key, boolean value) {

        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.commit();

    }

    public boolean readBoolean(String key) {
        return pref.getBoolean(key, false);
    }





    public void clear() {
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.clear();
        prefEditor.commit();

    }

    public void defualtData () {
        SharedPreferences.Editor prefEditor = pref.edit();
        ///////////////////////////////////////////
/*      prefEditor.putString("token",null);
        prefEditor.putString("refresh_token",null);*/
        ///////////////////////////////////////////
        prefEditor.commit();
    }


/*    public static boolean saveTheme(boolean isGirl, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putBoolean(THEME_TYPE, isGirl);
        return editor.commit();
    }*/

/*    public static boolean isThemeTypeGirl(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getBoolean(THEME_TYPE, false);
    }*/

}
