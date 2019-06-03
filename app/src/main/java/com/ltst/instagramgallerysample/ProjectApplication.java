package com.ltst.instagramgallerysample;

import android.app.Application;
import com.cosic.instagallery.utils.Logger;

public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setDebugable(true);
    }
}
