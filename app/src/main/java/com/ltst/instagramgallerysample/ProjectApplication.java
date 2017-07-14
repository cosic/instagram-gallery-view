package com.ltst.instagramgallerysample;

import android.app.Application;

import timber.log.Timber;

public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
