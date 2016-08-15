package com.looklook.xinghongfei.looklook;

import android.app.Application;

/**
 * Created by xinghongfei on 16/8/12.
 */
public class MyApplication extends Application {

    public static MyApplication myApplication;

    public static Application getContext() {

        return myApplication;

    }

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;

    }


}
