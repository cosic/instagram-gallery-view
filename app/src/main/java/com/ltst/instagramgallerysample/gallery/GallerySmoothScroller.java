package com.ltst.instagramgallerysample.gallery;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;

public class GallerySmoothScroller extends LinearSmoothScroller {

    private static final float MILLISECONDS_PER_INCH = 50f; //default is 25f (bigger == slower)

    public GallerySmoothScroller(final Context context) {
        super(context);
    }

    @Override protected int getVerticalSnapPreference() {
        return LinearSmoothScroller.SNAP_TO_START;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return super.computeScrollVectorForPosition(targetPosition);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
}
