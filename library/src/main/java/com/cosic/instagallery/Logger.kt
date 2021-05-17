package com.cosic.instagallery

import android.util.Log

object Logger {

    private const val TAG = "InstagramGallerySample"
    private var isDebuggable = false

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun d(message: String, vararg args: Any) {
        Log.d(TAG, String.format(message, *args))
    }

    fun setDebuggable(debuggable: Boolean) {
        isDebuggable = debuggable
    }

}
