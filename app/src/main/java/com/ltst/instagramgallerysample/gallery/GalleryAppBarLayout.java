package com.ltst.instagramgallerysample.gallery;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
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
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.ltst.instagramgallerysample.R;
import com.ltst.instagramgallerysample.utils.Logger;

public class GalleryAppBarLayout extends AppBarLayout implements GestureDetector.OnGestureListener {

    private static final int DEFAULT_AIR_SPACE_DP = 56;
    private static final float SNAP_PERCENT_OF_COLLAPSING = 40.f;
    private static final long SNAP_ANIMATION_DURATION = 300L;

    private enum MoveEventState {
        COLLAPSED,
        MOVE_UP,
        MOVE_DOWN,
        EXPANDED
    }

    private GestureDetectorCompat mGestureDetector;

    private View mParent;

    private boolean mIsFingerDown;

    private int mStartFingerYPosition;

    private int mAirSpace;

    private Interpolator mSpanAnimationInterpolator = new AccelerateDecelerateInterpolator();

    @Nullable
    private ValueAnimator mSnapAnimator;

    @Nullable
    private OnCollapseChangeStateListener mOnCollapseChangeStateListener;

    private int mLastOffSet;
    private int mLastTopPosition;
    private MoveEventState mLastMoveEventState;

    public GalleryAppBarLayout(Context context) {
        this(context, null);
    }

    public GalleryAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSaveEnabled(true);

        mGestureDetector = new GestureDetectorCompat(context, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GalleryAppBarLayout, 0, 0);

        try {
            mAirSpace = a.getDimensionPixelOffset(
                    R.styleable.GalleryAppBarLayout_gallery_airspace, dpToPx(DEFAULT_AIR_SPACE_DP));
        } finally {
            a.recycle();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.setLastTopPosition(mLastTopPosition);
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mLastTopPosition = ss.getLastTopPosition();
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

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        super.onLayout(changed, l, t, r, b);
        int top = getTop();
        Logger.d("onLayout, top=%d, mLastTopPosition=%d", top, mLastTopPosition);
        ViewCompat.offsetTopAndBottom(this, top + mLastTopPosition);
    }

    private void bindParentAttributes(View parent) {
        if (parent == null) return;
        bindPaddingToParent(parent);
        bindOnTouchListenerToParent(parent);
    }

    private void bindPaddingToParent(View parent) {
        if (parent == null) return;
        if (parent instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) parent;
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    int measuredHeight = getMeasuredHeight();
                    recyclerView.setPadding(
                            recyclerView.getPaddingLeft(),
                            recyclerView.getPaddingTop(),
                            recyclerView.getPaddingRight(),
                            measuredHeight);
                    recyclerView.setClipToPadding(false);
                    return false;
                }
            });
        }
//        else if (parent instanceof NestedScrollView) {
//            NestedScrollView scrollView = (NestedScrollView) parent;
//            int paddingBottom = getMeasuredHeight();
//            scrollView.setPadding(
//                    scrollView.getPaddingLeft(),
//                    scrollView.getPaddingTop(),
//                    scrollView.getPaddingRight(),
//                    paddingBottom);
//            scrollView.setClipToPadding(false);
//        }
    }

    private void bindOnTouchListenerToParent(View parent) {
        if (parent == null) return;
        if (parent instanceof GalleryRecyclerView) {
//            parent.setOnTouchListener(PARENT_ON_TOUCH_LISTENER);
            ((GalleryRecyclerView)parent).setOnDispatchTouchListener(PARENT_ON_TOUCH_LISTENER);
        }
    }

    private void applySnapEffect() {
        float percentOfCollapsing = 100 * Math.abs(getTop()) / (getHeight() - mAirSpace);
        if (mLastMoveEventState == MoveEventState.MOVE_UP) {
            collapse();
        } else if (mLastMoveEventState == MoveEventState.MOVE_DOWN) {
            expand();
        } else if (percentOfCollapsing > SNAP_PERCENT_OF_COLLAPSING) {
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

    public void setAirSpace(final int airSpace) {
        mAirSpace = airSpace;
    }

    public boolean isExpanded() {
        return getTop() == 0;
    }

    public boolean isCollapsed() {
        return getTop() == mAirSpace - getHeight();
    }

    public void expand() {
        final int from = getTop();
        final int to = 0;
        startSnapAnimation(from, to);
    }

    public void collapse() {

        final int from = getTop();
        final int to = -getHeight() + mAirSpace;
        startSnapAnimation(from, to);
    }

    private void startSnapAnimation(final int from, int to) {
        stopSnapAnimation();
        final int[] lastState = {from};
        mSnapAnimator = ValueAnimator.ofInt(from, to);
//        mSnapAnimator.setInterpolator(new BounceInterpolator());
//        mSnapAnimator.setInterpolator(new AccelerateInterpolator());
        mSnapAnimator.setInterpolator(mSpanAnimationInterpolator);
        mSnapAnimator.setDuration(SNAP_ANIMATION_DURATION);
        mSnapAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer animatedValue = (Integer) animation.getAnimatedValue();
                        applyOffSetChanges(lastState[0] - animatedValue);
                        lastState[0] = animatedValue;
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

        mLastMoveEventState = dY > 0 ? MoveEventState.MOVE_UP : MoveEventState.MOVE_DOWN;

        int height = getHeight();
        int top = getTop();
        int scrollY = parent.getScrollY();

        int childOffSet = -dY;
        int offset = Math.abs(dY);

        // Move to DOWN
        if (dY < 0) {
            if (top == 0 || top + offset > 0) {
                childOffSet = -top;
            } else {
                childOffSet = offset;
            }
        }

        // Move to UP
        if (dY > 0 && (top - offset < -height + mAirSpace)) {
            childOffSet = -height - top + mAirSpace;
        }

        Logger.d("Child: height=%d, scrollY=%d, dY=%d, parentTop=%d, childOffSet=%d",
                height, scrollY, dY, parent.getTop(), childOffSet);

        if (childOffSet != 0) {
            ViewCompat.offsetTopAndBottom(this, childOffSet);
            ViewCompat.offsetTopAndBottom(parent, getBottom() - parent.getTop());
        }

        if (isExpanded()) {
            mLastMoveEventState = MoveEventState.EXPANDED;
        } else if (isCollapsed()) {
            mLastMoveEventState = MoveEventState.COLLAPSED;
        }

        if (mOnCollapseChangeStateListener != null && mLastOffSet != 0 && childOffSet == 0) {
            if (isCollapsed()) {
                mOnCollapseChangeStateListener.onCollapsed();
            } else if (isExpanded()) {
                mOnCollapseChangeStateListener.onExpended();
            }
        }
        mLastOffSet = childOffSet;
        mLastTopPosition = getTop();
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

    private GalleryRecyclerView.OnDispatchTouchListener PARENT_ON_TOUCH_LISTENER =
            new GalleryRecyclerView.OnDispatchTouchListener() {

        private int mStartFingerYPositionParent;
        private boolean mIsOutOfRegion;

        @Override
        public boolean onDispatchTouchEvent(View view, MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    mStartFingerYPositionParent = (int) e.getY();
                    Logger.d("ParentPositions: startFingerYPosition =%d", mStartFingerYPositionParent);
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
                    Logger.d("ParentPositions: scrollY=%d, top=%d, positionY=%d, dY=%d", scrollY, top, positionY, dY);

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
                    // начала списка, то передаем
                    // смещение пальца в AppBar

                    if (!isExpanded() && scrollY == 0) { // Here !isExpanded() doesn't mean isCollapsed().
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

    public final static class SavedState extends BaseSavedState {

        private int mLastTopPosition;

        public SavedState(final Parcelable superState) {
            super(superState);
        }

        public SavedState(final Parcel in) {
            super(in);
            mLastTopPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mLastTopPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public int getLastTopPosition() {
            return mLastTopPosition;
        }

        public void setLastTopPosition(final int lastTopPosition) {
            mLastTopPosition = lastTopPosition;
        }
    }

    public interface OnCollapseChangeStateListener {

        void onCollapsed();

        void onExpended();
    }

}
