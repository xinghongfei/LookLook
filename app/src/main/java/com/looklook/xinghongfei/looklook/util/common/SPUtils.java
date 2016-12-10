package com.looklook.xinghongfei.looklook.util.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hymanme on 2015/8/26.
 */
public class SPUtils {
    private SPUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(UIUtils.getContext());
    }

    public static String getPrefString(String key, final String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static void setPrefString(final String key, final String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    public static boolean getPrefBoolean(final String key, final boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static void setPrefBoolean(final String key, final boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public static int getPrefInt(final String key, final int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static void setPrefInt(final String key, final int value) {
        getSharedPreferences().edit().putInt(key, value).commit();
    }

    public static float getPrefFloat(final String key, final float defaultValue) {
        return getSharedPreferences().getFloat(key, defaultValue);
    }

    public static void setPrefFloat(final String key, final float value) {
        getSharedPreferences().edit().putFloat(key, value).commit();
    }

    public static long getPrefLong(final String key, final long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    public static void setPrefLong(final String key, final long value) {
        getSharedPreferences().edit().putLong(key, value).commit();
    }

    public static boolean hasKey(final String key) {
        return getSharedPreferences().contains(key);
    }

    public static void clearPreference() {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }
}
