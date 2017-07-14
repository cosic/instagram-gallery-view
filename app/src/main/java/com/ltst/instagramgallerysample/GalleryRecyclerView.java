package com.ltst.instagramgallerysample;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import timber.log.Timber;

public class GalleryRecyclerView extends NestedScrollView implements GestureDetector.OnGestureListener {

    private static final int MARGIN_TOP = 160;

    private GestureDetectorCompat mGestureDetector;

    public GalleryRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetectorCompat(context, this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        AppBarLayout child = getNestedChild();
        if (child != null) {
//            int offset = child.getBottom() - getTop();
//            int verticalScrollOffset = computeVerticalScrollOffset();
//            Timber.d("offset=%d, verticalScrollOffset=%d, childTop=%d",
//                    offset, verticalScrollOffset, child.getTop());
            ViewCompat.offsetTopAndBottom(this, child.getBottom() - getTop());
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height += child.getHeight();
            setLayoutParams(lp);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        super.onTouchEvent(e);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
        AppBarLayout child = getNestedChild();
        if (child != null) {
            int h = child.getHeight();
            int childTop = child.getTop();
            int childBottom = child.getBottom();
            int scrollPosition = getScrollY();
            int offset = child.getBottom() - getTop();
            int verticalScrollOffset = computeVerticalScrollOffset();
            Timber.d("h=%d, scrollPosition=%d, offset=%d, dY=%f, verticalScrollOffset=%d, childTop=%d",
                    h, scrollPosition, offset, dY, verticalScrollOffset, child.getTop());
            int childOffSet = (int) -dY;
            if (dY < 0 && (childTop == 0 || childTop - dY > 0)) {
                childOffSet = -childTop;
            }
            if (dY > 0 && (childTop - dY < -h + MARGIN_TOP)) {
                childOffSet = -h - childTop + MARGIN_TOP;
            }
            if (childOffSet != 0) {
                ViewCompat.offsetTopAndBottom(child, childOffSet);
                ViewCompat.offsetTopAndBottom(this, childOffSet);
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private AppBarLayout getNestedChild() {
        ViewGroup parent = (ViewGroup) getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            AppBarLayout childAt = (AppBarLayout) parent.getChildAt(i);
            if (childAt != null) {
                return childAt;
            }
        }
        return null;
    }

    public interface GalleryRecyclerViewChild {

    }
}