package com.ltst.instagramgallerysample.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "InstagramGallarySample";
    private static boolean isDebugable = false;

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void d(String message, Object... args) {
        Log.d(TAG, String.format(message, args));
    }

    public static void setDebugable(boolean debugable) {
        isDebugable = debugable;
    }

}
