package com.cosic.instagallery.utils

import android.util.Log

object Logger {

    private val TAG = "InstagramGallarySample"
    private var isDebugable = false

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun d(message: String, vararg args: Any) {
        Log.d(TAG, String.format(message, *args))
    }

    fun setDebugable(debugable: Boolean) {
        isDebugable = debugable
    }

}
