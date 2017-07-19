package com.ltst.instagramgallerysample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import timber.log.Timber;

public class GalleryAppBarLayout extends AppBarLayout implements GestureDetector.OnGestureListener {

    private static final long SNAP_ANIMATION_DURATION = 500L;
    private static final int MARGIN_TOP = 160;

    private GestureDetectorCompat mGestureDetector;

    private View mParent;

    private boolean mIsFingerDown;
    private int mStartFingerYPosition;

    public GalleryAppBarLayout(Context context) {
        super(context);
        init(context);
    }

    public GalleryAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetectorCompat(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        View parent = getNestedScrollingParent();
//        bindParentAttributes(parent);
    }

    private void bindParentAttributes(View parent) {
        if (parent == null) return;
        if (parent instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) parent;
            recyclerView.setPadding(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getPaddingRight(),
                    MARGIN_TOP);
            recyclerView.setClipToPadding(false);
        } else if (parent instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) parent;
            int paddingLeft = scrollView.getPaddingLeft();
            int paddingTop = scrollView.getPaddingTop();
            int paddingRight = scrollView.getPaddingRight();
            scrollView.setPadding(
                    paddingLeft,
                    paddingTop,
                    paddingRight,
                    MARGIN_TOP);
            scrollView.setClipToPadding(false);
        }
    }

    private void applySnap() {
        if (getTop() < -getHeight() / 2) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {

        final int from = getTop();
        final int to = 0;
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(SNAP_ANIMATION_DURATION);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        applyOffSetChanges(from - (Integer) animation.getAnimatedValue());
                    }
                });
        animator.start();
    }

    public void collapse() {

        final int from = getTop();
        final int to = -getHeight() + MARGIN_TOP;
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(SNAP_ANIMATION_DURATION);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        applyOffSetChanges(from - (Integer) animation.getAnimatedValue());
                    }
                });
        animator.start();
    }

    @Nullable
    private View getNestedScrollingParent() {
        if (mParent == null) {
            ViewGroup parent = (ViewGroup) getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                if (childAt instanceof NestedScrollingParent) {
                    mParent = childAt;
//                    bindParentAttributes(mParent);
                }
            }
        }
        return mParent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mStartFingerYPosition = (int) e.getY();
                mIsFingerDown = true;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                final int oldY = (int) e.getY();
                final int dY = oldY - mStartFingerYPosition;
//                mStartFingerYPosition = oldY;
                applyOffSetChanges(-dY);
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsFingerDown = false;
                applySnap();
            }
            break;
            default:
                break;
        }
        super.onTouchEvent(e);
        return true;
    }

    /**
     * dY > 0 - motion to UP
     * dY < 0 - motion to DOWN
     */
    private void applyOffSetChanges(int dY) {
        View parent = getNestedScrollingParent();
        if (parent == null) return;
        int childHeight = getHeight();
        int childTop = getTop();
        int childBottom = getBottom();
        int scrollY = parent.getScrollY();
        int offsetUptoChild = getBottom() - parent.getTop();
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

//        if (dY < 0 && scrollY > 100) {
//            childOffSet = 0;
//        }

        Timber.d("childHeight=%d, scrollY=%d, offsetUptoChild=%d, dY=%d, childTop=%d, childOffSet=%d",
                childHeight, scrollY, offsetUptoChild, dY, parent.getTop(), childOffSet);

        if (childOffSet != 0) {
            ViewCompat.offsetTopAndBottom(parent, childOffSet);
            ViewCompat.offsetTopAndBottom(this, childOffSet);
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
