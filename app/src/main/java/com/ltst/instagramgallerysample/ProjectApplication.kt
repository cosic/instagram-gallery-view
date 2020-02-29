package com.ltst.instagramgallerysample

import android.app.Application
import com.cosic.instagallery.utils.Logger

class ProjectApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.setDebugable(true)
    }
}
