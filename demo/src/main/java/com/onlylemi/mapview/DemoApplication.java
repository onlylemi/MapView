package com.onlylemi.mapview;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * DemoApplication
 *
 * @author: onlylemi
 * @time: 2016-05-14 12:07
 */
public class DemoApplication extends Application {

    private static Context context;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        refWatcher = LeakCanary.install(this);
    }

    public static Context getContext() {
        return context;
    }
}
