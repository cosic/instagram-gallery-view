package com.ltst.instagramgallerysample.gallery;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import timber.log.Timber;

public class GalleryAppBarLayout extends AppBarLayout implements GestureDetector.OnGestureListener {

    private static final float SNAP_PERCENT_OF_COLLAPSING = 40.f;
    private static final long SNAP_ANIMATION_DURATION = 300L;
    private static final int MARGIN_TOP = dpToPx(56);

    private GestureDetectorCompat mGestureDetector;

    private View mParent;

    private boolean mIsFingerDown;
    private int mStartFingerYPosition;

    private Interpolator mSpanAnimationInterpolator = new AccelerateDecelerateInterpolator();

    @Nullable
    private ValueAnimator mSnapAnimator;

    @Nullable
    private OnCollapseChangeStateListener mOnCollapseChangeStateListener;

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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        final ViewGroup parent = (ViewGroup) getParent();
//        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                getViewTreeObserver().removeOnPreDrawListener(this);
//                bindParentAttributes(getNestedScrollingParent());
//                return false;
//            }
//        });
        View parent = getNestedScrollingParent();
        bindParentAttributes(parent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void bindParentAttributes(View parent) {
        if (parent == null) return;
//        bindPaddingToParent(parent);
        bindOnTouchListenerToParent(parent);
    }

    private void bindPaddingToParent(View parent) {
        if (parent == null) return;
        if (parent instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) parent;
            recyclerView.setPadding(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getPaddingRight(),
                    getMeasuredHeight());
            recyclerView.setClipToPadding(false);
        } else if (parent instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) parent;
            int paddingBottom = getMeasuredHeight();
            scrollView.setPadding(
                    scrollView.getPaddingLeft(),
                    scrollView.getPaddingTop(),
                    scrollView.getPaddingRight(),
                    paddingBottom);
            scrollView.setClipToPadding(false);
        }
    }

    private void bindOnTouchListenerToParent(View parent) {
        if (parent == null) return;
        parent.setOnTouchListener(PARENT_ON_TOUCH_LISTENER);
    }

    private void applySnapEffect() {
        float percentOfCollapsing = 100 * Math.abs(getTop()) / (getHeight() - MARGIN_TOP);
        if (percentOfCollapsing > SNAP_PERCENT_OF_COLLAPSING) {
            collapse();
        } else {
            expand();
        }
    }

    public void setSpanAnimationInterpolator(Interpolator interpolator) {
        this.mSpanAnimationInterpolator = interpolator;
    }

    public void setOnCollapseChangeStateListener(@Nullable OnCollapseChangeStateListener listener) {
        this.mOnCollapseChangeStateListener = listener;
    }

    public boolean isExpanded() {
        return getTop() == 0;
    }

    public boolean isCollapsed() {
        return !isExpanded();
    }

    public void expand() {
        final int from = getTop();
        final int to = 0;
        startSnapAnimation(from, to);
    }

    public void collapse() {

        final int from = getTop();
        final int to = -getHeight() + MARGIN_TOP;
        startSnapAnimation(from, to);
    }

    private void startSnapAnimation(final int from, int to) {
        stopSnapAnimation();
        mSnapAnimator = ValueAnimator.ofInt(from, to);
//        mSnapAnimator.setInterpolator(new BounceInterpolator());
//        mSnapAnimator.setInterpolator(new AccelerateInterpolator());
        mSnapAnimator.setInterpolator(mSpanAnimationInterpolator);
        mSnapAnimator.setDuration(SNAP_ANIMATION_DURATION);
        mSnapAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        applyOffSetChanges(from - (Integer) animation.getAnimatedValue());
                    }
                });
        mSnapAnimator.start();
    }

    private void stopSnapAnimation() {
        if (mSnapAnimator != null && mSnapAnimator.isRunning()) {
            mSnapAnimator.cancel();
        }
    }

    @Nullable
    private View getNestedScrollingParent() {
        if (mParent == null) {
            ViewGroup parent = (ViewGroup) getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                if (childAt instanceof NestedScrollingParent
                        || childAt instanceof NestedScrollingChild) {
                    mParent = childAt;
                }
            }
        }
        return mParent;
    }

    private int getNestedScrollingParentTotalScroll() {
        if (mParent != null) {
            if (mParent instanceof NestedScrollView) {
                NestedScrollView horizontalScrollview = (NestedScrollView) this.mParent;
                return horizontalScrollview.getChildAt(0).getMeasuredHeight();
            } else if (mParent instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) this.mParent;
                return recyclerView.computeVerticalScrollRange();
            }
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                stopSnapAnimation();
                mStartFingerYPosition = (int) e.getY();
                mIsFingerDown = true;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                final int positionY = (int) e.getY();
                final int dY = positionY - mStartFingerYPosition;
                applyOffSetChanges(-dY);
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsFingerDown = false;
                applySnapEffect();
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
        // nothing;
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
        // nothing;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    private OnTouchListener PARENT_ON_TOUCH_LISTENER = new OnTouchListener() {

        private int mStartFingerYPositionParent;
        private boolean mIsOutOfRegion;

        @Override
        public boolean onTouch(View view, MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    mStartFingerYPositionParent = (int) e.getY();
                    mIsFingerDown = true;
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    int scrollY = 0;
                    if (view instanceof NestedScrollingParent) {
                        scrollY = view.getScrollY();
                    } else if (view instanceof RecyclerView) {
                        scrollY = ((RecyclerView) view).computeVerticalScrollOffset();
                    }
                    int positionY = (int) e.getY();
                    int dY = positionY - mStartFingerYPositionParent;
                    int top = view.getTop();
                    Timber.d("ParentPositions: scrollY=%d, top=%d, y=%d, dY=%d", scrollY, top, positionY, dY);

                    // Если скролим ввер и палец вышел
                    // из региона скролинга списка
                    // то передаем смещение пальца в AppBar
                    if (positionY < 0) {
                        if (!mIsOutOfRegion) {
                            mIsOutOfRegion = true;
                            mStartFingerYPositionParent = positionY;
                            dY = 0;
                        }
                        applyOffSetChanges(-dY);
                        return true;
                    } else {
                        if (mIsOutOfRegion) {
                            mIsOutOfRegion = false;
                        }
                    }
                    // Если скролим вниз и достигли
                    // начала списка, то то передаем
                    // смещение пальца в AppBar
                    if (isCollapsed() && scrollY == 0) {
                        if (dY > 0) {
                            applyOffSetChanges(-dY);
                            return true;
                        }
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mIsFingerDown = false;
                    applySnapEffect();
                }
                break;
                default:
                    break;
            }
            return false;
        }
    };

    static public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    interface OnCollapseChangeStateListener {

    }

}
