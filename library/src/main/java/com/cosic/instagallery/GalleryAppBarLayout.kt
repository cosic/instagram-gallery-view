package com.cosic.instagallery

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class GalleryAppBarLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppBarLayout(context, attrs), GestureDetector.OnGestureListener {

    private val mGestureDetector: GestureDetectorCompat

    private var mParent: View? = null

    private var mIsFingerDown: Boolean = false

    private var mStartFingerYPosition: Int = 0

    private var mAirSpace: Int = 0

    private var mSpanAnimationInterpolator: Interpolator = AccelerateDecelerateInterpolator()

    private var mSnapAnimator: ValueAnimator? = null

    private var mOnCollapseChangeStateListener: OnCollapseChangeStateListener? = null

    private var mLastOffSet: Int = 0
    private var mLastTopPosition: Int = 0
    private var mLastMoveEventState: MoveEventState? = null

    val isExpanded: Boolean
        get() = top == 0

    val isCollapsed: Boolean
        get() = top == mAirSpace - height

    private val nestedScrollingParent: View?
        get() {
            if (mParent == null) {
                val parent = parent as ViewGroup
                for (i in 0 until parent.childCount) {
                    val childAt = parent.getChildAt(i)
                    if (childAt is NestedScrollingParent || childAt is NestedScrollingChild) {
                        mParent = childAt
                    }
                }
            }
            return mParent
        }

    private val nestedScrollingParentTotalScroll: Int
        get() {
            if (mParent != null) {
                if (mParent is NestedScrollView) {
                    val horizontalScrollview = this.mParent as NestedScrollView?
                    return horizontalScrollview!!.getChildAt(0).measuredHeight
                } else if (mParent is RecyclerView) {
                    val recyclerView = this.mParent as RecyclerView?
                    return recyclerView!!.computeVerticalScrollRange()
                }
            }
            return 0
        }

    private val PARENT_ON_TOUCH_LISTENER = object : GalleryRecyclerView.OnDispatchTouchListener {

        private var mStartFingerYPositionParent: Int = 0
        private var mIsOutOfRegion: Boolean = false

        override fun onDispatchTouchEvent(view: View, e: MotionEvent): Boolean {
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    mStartFingerYPositionParent = e.y.toInt()
                    Logger.d("ParentPositions: startFingerYPosition =%d", mStartFingerYPositionParent)
                    mIsFingerDown = true
                }
                MotionEvent.ACTION_MOVE -> {
                    var scrollY = 0
                    if (view is NestedScrollingParent) {
                        scrollY = view.scrollY
                    } else if (view is RecyclerView) {
                        scrollY = view.computeVerticalScrollOffset()
                    }
                    val positionY = e.y.toInt()
                    var dY = positionY - mStartFingerYPositionParent
                    val top = view.top
                    Logger.d("ParentPositions: scrollY=%d, top=%d, positionY=%d, dY=%d", scrollY, top, positionY, dY)

                    // Если скролим ввер и палец вышел
                    // из региона скролинга списка
                    // то передаем смещение пальца в AppBar
                    if (positionY < 0) {
                        if (!mIsOutOfRegion) {
                            mIsOutOfRegion = true
                            mStartFingerYPositionParent = positionY
                            dY = 0
                        }
                        applyOffSetChanges(-dY)
                        return true
                    } else {
                        if (mIsOutOfRegion) {
                            mIsOutOfRegion = false
                        }
                    }
                    // Если скролим вниз и достигли
                    // начала списка, то передаем
                    // смещение пальца в AppBar

                    if (!isExpanded && scrollY == 0) { // Here !isExpanded() doesn't mean isCollapsed().
                        if (dY > 0) {
                            applyOffSetChanges(-dY)
                            return true
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    mIsFingerDown = false
                    applySnapEffect()
                }
                else -> {
                }
            }
            return false
        }
    }

    init {

        isSaveEnabled = true

        mGestureDetector = GestureDetectorCompat(context, this)

        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.GalleryAppBarLayout, 0, 0)

        try {
            mAirSpace = a.getDimensionPixelOffset(
                R.styleable.GalleryAppBarLayout_gallery_airspace, dpToPx(DEFAULT_AIR_SPACE_DP))
        } finally {
            a.recycle()
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val ss = SavedState(super.onSaveInstanceState())
        ss.lastTopPosition = mLastTopPosition
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        mLastTopPosition = ss.lastTopPosition
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //        final ViewGroup parent = (ViewGroup) getParent();
        //        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        //            @Override
        //            public boolean onPreDraw() {
        //                getViewTreeObserver().removeOnPreDrawListener(this);
        //                bindParentAttributes(getNestedScrollingParent());
        //                return false;
        //            }
        //        });
        val parent = nestedScrollingParent
        bindParentAttributes(parent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val top = top
        Logger.d("onLayout, top=%d, mLastTopPosition=%d", top, mLastTopPosition)
        ViewCompat.offsetTopAndBottom(this, top + mLastTopPosition)
    }

    private fun bindParentAttributes(parent: View?) {
        if (parent == null) return
        bindPaddingToParent(parent)
        bindOnTouchListenerToParent(parent)
    }

    private fun bindPaddingToParent(parent: View?) {
        if (parent == null) return
        if (parent is RecyclerView) {
            val recyclerView = parent as RecyclerView?
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    val measuredHeight = measuredHeight
                    recyclerView!!.setPadding(
                        recyclerView.paddingLeft,
                        recyclerView.paddingTop,
                        recyclerView.paddingRight,
                        measuredHeight)
                    recyclerView.clipToPadding = false
                    return false
                }
            })
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

    private fun bindOnTouchListenerToParent(parent: View?) {
        if (parent == null) return
        if (parent is GalleryRecyclerView) {
            //            parent.setOnTouchListener(PARENT_ON_TOUCH_LISTENER);
            parent.setOnDispatchTouchListener(PARENT_ON_TOUCH_LISTENER)
        }
    }

    private fun applySnapEffect() {
        val percentOfCollapsing = (100 * Math.abs(top) / (height - mAirSpace)).toFloat()
        if (mLastMoveEventState == MoveEventState.MOVE_UP) {
            collapse()
        } else if (mLastMoveEventState == MoveEventState.MOVE_DOWN) {
            expand()
        } else if (percentOfCollapsing > SNAP_PERCENT_OF_COLLAPSING) {
            collapse()
        } else {
            expand()
        }
    }

    fun setSpanAnimationInterpolator(interpolator: Interpolator) {
        this.mSpanAnimationInterpolator = interpolator
    }

    fun setOnCollapseChangeStateListener(listener: OnCollapseChangeStateListener?) {
        this.mOnCollapseChangeStateListener = listener
    }

    fun setAirSpace(airSpace: Int) {
        mAirSpace = airSpace
    }

    fun expand() {
        val from = top
        val to = 0
        startSnapAnimation(from, to)
    }

    fun collapse() {

        val from = top
        val to = -height + mAirSpace
        startSnapAnimation(from, to)
    }

    private fun startSnapAnimation(from: Int, to: Int) {
        stopSnapAnimation()
        val lastState = intArrayOf(from)
        mSnapAnimator = ValueAnimator.ofInt(from, to)
        //        mSnapAnimator.setInterpolator(new BounceInterpolator());
        //        mSnapAnimator.setInterpolator(new AccelerateInterpolator());
        mSnapAnimator!!.interpolator = mSpanAnimationInterpolator
        mSnapAnimator!!.duration = SNAP_ANIMATION_DURATION
        mSnapAnimator!!.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            applyOffSetChanges(lastState[0] - animatedValue)
            lastState[0] = animatedValue
        }
        mSnapAnimator!!.start()
    }

    private fun stopSnapAnimation() {
        if (mSnapAnimator != null && mSnapAnimator!!.isRunning) {
            mSnapAnimator!!.cancel()
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        mGestureDetector.onTouchEvent(e)

        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                stopSnapAnimation()
                mStartFingerYPosition = e.y.toInt()
                mIsFingerDown = true
            }
            MotionEvent.ACTION_MOVE -> {
                val positionY = e.y.toInt()
                val dY = positionY - mStartFingerYPosition
                applyOffSetChanges(-dY)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsFingerDown = false
                applySnapEffect()
            }
            else -> {
            }
        }
        super.onTouchEvent(e)
        return true
    }

    /**
     * dY > 0 - motion to UP
     * dY < 0 - motion to DOWN
     */
    private fun applyOffSetChanges(dY: Int) {
        val parent = nestedScrollingParent ?: return

        mLastMoveEventState = if (dY > 0) MoveEventState.MOVE_UP else MoveEventState.MOVE_DOWN

        val height = height
        val top = top
        val scrollY = parent.scrollY

        var childOffSet = -dY
        val offset = Math.abs(dY)

        // Move to DOWN
        if (dY < 0) {
            if (top == 0 || top + offset > 0) {
                childOffSet = -top
            } else {
                childOffSet = offset
            }
        }

        // Move to UP
        if (dY > 0 && top - offset < -height + mAirSpace) {
            childOffSet = -height - top + mAirSpace
        }

        Logger.d("Child: height=%d, scrollY=%d, dY=%d, parentTop=%d, childOffSet=%d",
            height, scrollY, dY, parent.top, childOffSet)

        if (childOffSet != 0) {
            ViewCompat.offsetTopAndBottom(this, childOffSet)
            ViewCompat.offsetTopAndBottom(parent, bottom - parent.top)
        }

        if (isExpanded) {
            mLastMoveEventState = MoveEventState.EXPANDED
        } else if (isCollapsed) {
            mLastMoveEventState = MoveEventState.COLLAPSED
        }

        if (mOnCollapseChangeStateListener != null && mLastOffSet != 0 && childOffSet == 0) {
            if (isCollapsed) {
                mOnCollapseChangeStateListener!!.onCollapsed()
            } else if (isExpanded) {
                mOnCollapseChangeStateListener!!.onExpended()
            }
        }
        mLastOffSet = childOffSet
        mLastTopPosition = getTop()
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {
        // nothing;
    }

    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        // nothing;
    }

    override fun onFling(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
        return false
    }

    class SavedState : View.BaseSavedState {

        var lastTopPosition: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            lastTopPosition = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(lastTopPosition)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    interface OnCollapseChangeStateListener {

        fun onCollapsed()

        fun onExpended()
    }

    private enum class MoveEventState {
        COLLAPSED,
        MOVE_UP,
        MOVE_DOWN,
        EXPANDED
    }

    companion object {

        private const val DEFAULT_AIR_SPACE_DP = 56
        private const val SNAP_PERCENT_OF_COLLAPSING = 40f
        private const val SNAP_ANIMATION_DURATION = 300L

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }
    }

}
