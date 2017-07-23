package com.ltst.instagramgallerysample.gallery;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public class GalleryRecyclerView extends RecyclerView {

    @Nullable
    private OnDispatchTouchListener mOnDispatchTouchListener;

    private AppBarLayout mChild;

    public GalleryRecyclerView(final Context context) {
        super(context);
    }

    public GalleryRecyclerView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryRecyclerView(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
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
            Timber.d("onLayout, childBottom=%d childTop=%d, childHeight=%d, top=%d",
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

    @Override
    public boolean onTouchEvent(final MotionEvent e) {
        if (mOnDispatchTouchListener != null) {
            if (mOnDispatchTouchListener.onDispatchTouchEvent(this, e)) {
                return true;
            }
        }

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Timber.d("GalleryRecyclerView: onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Timber.d("GalleryRecyclerView: onTouchEvent ACTION_UP");
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
                Timber.d("GalleryRecyclerView: dispatchTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Timber.d("GalleryRecyclerView: dispatchTouchEvent ACTION_UP");
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Timber.d("GalleryRecyclerView: onInterceptTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Timber.d("GalleryRecyclerView: onInterceptTouchEvent ACTION_UP");
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
}
