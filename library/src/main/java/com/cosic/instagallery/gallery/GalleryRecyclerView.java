package com.cosic.instagallery.gallery;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.cosic.instagallery.utils.Logger;
import com.google.android.material.appbar.AppBarLayout;

public class GalleryRecyclerView extends RecyclerView {

    @Nullable
    private OnDispatchTouchListener mOnDispatchTouchListener;

    private RecyclerView.SmoothScroller mSmoothScroller;

    private AppBarLayout mChild;

    public GalleryRecyclerView(final Context context) {
        this(context, null);
    }

    public GalleryRecyclerView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryRecyclerView(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mSmoothScroller = new GallerySmoothScroller(context);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        super.onLayout(changed, l, t, r, b);
        AppBarLayout child = getNestedAppBar();
        if (child != null) {
            int childTop = child.getTop();
            int childHeight = child.getHeight();
            int childBottom = child.getBottom();
            int top = getTop();
            Logger.d("onLayout, childBottom=%d childTop=%d, childHeight=%d, top=%d",
                    childBottom, childTop, childHeight, top);
            ViewCompat.offsetTopAndBottom(this, childBottom - top);
//            ViewGroup.LayoutParams lp = getLayoutParams();
//            lp.height += child.getHeight();
//            setLayoutParams(lp);
        }
    }

    private AppBarLayout getNestedAppBar() {
        if (mChild == null) {
            ViewGroup parent = (ViewGroup) getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                if (childAt instanceof AppBarLayout) {
                    mChild = (AppBarLayout) childAt;
                }
            }
        }
        return mChild;
    }

    public void scrollToPosition(int position) {
        LayoutManager lm = getLayoutManager();
        if (lm == null) return;
        mSmoothScroller.setTargetPosition(position);
        lm.startSmoothScroll(mSmoothScroller);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent e) {
        if (mOnDispatchTouchListener != null) {
            if (mOnDispatchTouchListener.onDispatchTouchEvent(this, e)) {
                return true;
            }
        }

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Logger.d("GalleryRecyclerView: onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Logger.d("GalleryRecyclerView: onTouchEvent ACTION_UP");
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnDispatchTouchListener != null) {
                    mOnDispatchTouchListener.onDispatchTouchEvent(this, e);
                }
                Logger.d("GalleryRecyclerView: dispatchTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Logger.d("GalleryRecyclerView: dispatchTouchEvent ACTION_UP");
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Logger.d("GalleryRecyclerView: onInterceptTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Logger.d("GalleryRecyclerView: onInterceptTouchEvent ACTION_UP");
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    public void setOnDispatchTouchListener(@Nullable final OnDispatchTouchListener listener) {
        mOnDispatchTouchListener = listener;
    }

    public interface OnDispatchTouchListener {
        boolean onDispatchTouchEvent(View view, MotionEvent e);
    }

    public static class GallerySmoothScroller extends LinearSmoothScroller {

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
}
