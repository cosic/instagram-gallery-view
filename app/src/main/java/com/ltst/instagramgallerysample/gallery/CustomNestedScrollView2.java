package com.ltst.instagramgallerysample.gallery;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ltst.instagramgallerysample.utils.Logger;

public class CustomNestedScrollView2 extends NestedScrollView implements GestureDetector.OnGestureListener {

    private static final int MARGIN_TOP = 160;

    private GestureDetectorCompat mGestureDetector;

    private AppBarLayout mChild;

    private boolean mIsFingerDown;
    private int mStartFingerYPosition;

    public CustomNestedScrollView2(Context context) {
        super(context);
        init(context);
    }

    public CustomNestedScrollView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomNestedScrollView2(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetectorCompat(context, this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        AppBarLayout child = getNestedAppBar();
        if (child != null) {
            ViewCompat.offsetTopAndBottom(this, child.getBottom() - getTop());
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height += child.getHeight();
            setLayoutParams(lp);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        mGestureDetector.onTouchEvent(e);
//
//        switch (e.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN: {
//                mStartFingerYPosition = (int) e.getY();
//                mIsFingerDown = true;
//            }
//            break;
//            case MotionEvent.ACTION_MOVE: {
//                final int oldY = (int) e.getY();
//                final int dY = oldY - mStartFingerYPosition;
////                mStartFingerYPosition = oldY;
//                applyOffSetChanges(-dY);
//            }
//            break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL: {
//                mIsFingerDown = false;
//            }
//            break;
//            default:
//                break;
//        }
        super.onTouchEvent(e);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
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
        return true;
    }

    /**
     * dY > 0 - motion to UP
     * dY < 0 - motion to DOWN
     */
    private void applyOffSetChanges(int dY) {
        AppBarLayout child = getNestedAppBar();
        if (child == null) return;
        int childHeight = child.getHeight();
        int childTop = child.getTop();
        int childBottom = child.getBottom();
        int scrollY = getScrollY();
        int offsetUptoChild = child.getBottom() - getTop();
        int verticalScrollOffset = computeVerticalScrollOffset();

        int childOffSet = (int) -dY;
        int offset = Math.abs(dY);

        // Move to DOWN
        if (dY < 0) {
            if (childTop == 0 || childTop + offset > 0) {
                childOffSet = -childTop;
            } else {
                childOffSet = offset;
            }
        }

        // Move to UP
        if (dY > 0 && (childTop - offset < -childHeight + MARGIN_TOP)) {
            childOffSet = -childHeight - childTop + MARGIN_TOP;
        }

        if (dY < 0 && scrollY > 100) {
            childOffSet = 0;
        }

        Logger.d("childHeight=%d, scrollY=%d, offsetUptoChild=%d, dY=%d, childTop=%d, childOffSet=%d",
                childHeight, scrollY, offsetUptoChild, dY, child.getTop(), childOffSet);

        if (childOffSet != 0) {
            ViewCompat.offsetTopAndBottom(child, childOffSet);
            ViewCompat.offsetTopAndBottom(this, childOffSet);
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
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

    public interface GalleryRecyclerViewChild {

    }
}