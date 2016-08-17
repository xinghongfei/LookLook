package com.looklook.xinghongfei.looklook.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.looklook.xinghongfei.looklook.R;

/**
 * Created by 蔡小木 on 2016/3/13 0013.
 */
public class SharePreferenceUtil {

    private SharePreferenceUtil() {}

    public static final String SHARED_PREFERENCE_NAME = "micro_reader";
    public static final String IMAGE_DESCRIPTION = "image_description";
    public static final String VIBRANT = "vibrant";
    public static final String MUTED = "muted";
    public static final String IMAGE_GET_TIME = "image_get_time";
    public static final String SAVED_CHANNEL = "saved_channel";

    public static boolean isRefreshOnlyWifi(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pre_refresh_data), false);
    }

    public static boolean isChangeThemeAuto(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pre_get_image), true);
    }

    public static boolean isImmersiveMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pre_status_bar), true);
    }

    public static boolean isChangeNavColor(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pre_nav_color), true);
    }

    public static boolean isUseLocalBrowser(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pre_use_local), false);
    }

    public static int getNevigationItem(Context context){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.nevigation_item),-1);
    }
    public static void putNevigationItem(Context context,int t){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(context.getString(R.string.nevigation_item),t);
        editor.commit();
    }

}
