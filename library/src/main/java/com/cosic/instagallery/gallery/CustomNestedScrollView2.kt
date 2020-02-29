package com.cosic.instagallery.gallery

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import com.cosic.instagallery.utils.Logger
import com.google.android.material.appbar.AppBarLayout

class CustomNestedScrollView2 : NestedScrollView, GestureDetector.OnGestureListener {

    private var mGestureDetector: GestureDetectorCompat? = null

    private var mChild: AppBarLayout? = null

    private val mIsFingerDown: Boolean = false
    private val mStartFingerYPosition: Int = 0

    private val nestedAppBar: AppBarLayout?
        get() {
            if (mChild == null) {
                val parent = parent as ViewGroup
                for (i in 0 until parent.childCount) {
                    val childAt = parent.getChildAt(i)
                    if (childAt is AppBarLayout) {
                        mChild = childAt
                    }
                }
            }
            return mChild
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        mGestureDetector = GestureDetectorCompat(context, this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val child = nestedAppBar
        if (child != null) {
            ViewCompat.offsetTopAndBottom(this, child.bottom - top)
            val lp = layoutParams
            lp.height += child.height
            layoutParams = lp
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
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
        super.onTouchEvent(e)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {
        return true
    }

    /**
     * dY > 0 - motion to UP
     * dY < 0 - motion to DOWN
     */
    private fun applyOffSetChanges(dY: Int) {
        val child = nestedAppBar ?: return
        val childHeight = child.height
        val childTop = child.top
        val childBottom = child.bottom
        val scrollY = scrollY
        val offsetUptoChild = child.bottom - top
        val verticalScrollOffset = computeVerticalScrollOffset()

        var childOffSet = -dY
        val offset = Math.abs(dY)

        // Move to DOWN
        if (dY < 0) {
            if (childTop == 0 || childTop + offset > 0) {
                childOffSet = -childTop
            } else {
                childOffSet = offset
            }
        }

        // Move to UP
        if (dY > 0 && childTop - offset < -childHeight + MARGIN_TOP) {
            childOffSet = -childHeight - childTop + MARGIN_TOP
        }

        if (dY < 0 && scrollY > 100) {
            childOffSet = 0
        }

        Logger.d("childHeight=%d, scrollY=%d, offsetUptoChild=%d, dY=%d, childTop=%d, childOffSet=%d",
            childHeight, scrollY, offsetUptoChild, dY, child.top, childOffSet)

        if (childOffSet != 0) {
            ViewCompat.offsetTopAndBottom(child, childOffSet)
            ViewCompat.offsetTopAndBottom(this, childOffSet)
        }
    }

    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    interface GalleryRecyclerViewChild

    companion object {

        private val MARGIN_TOP = 160
    }
}