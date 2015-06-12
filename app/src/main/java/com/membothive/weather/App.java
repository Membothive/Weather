package com.membothive.weather;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pranay Pradhan on 12-06-2015.
 */
public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }
}
