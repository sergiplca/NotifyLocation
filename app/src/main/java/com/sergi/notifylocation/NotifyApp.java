package com.sergi.notifylocation;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Sergi on 25/05/17.
 */

public class NotifyApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

    }
}
