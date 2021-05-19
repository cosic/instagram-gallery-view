package com.cosic.instagramgallerysample

import android.app.Application
import com.cosic.instagallery.Logger

class ProjectApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.setDebuggable(true)
    }
}
