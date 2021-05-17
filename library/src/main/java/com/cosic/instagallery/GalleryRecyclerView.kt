package com.cosic.instagallery

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class GalleryRecyclerView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var mOnDispatchTouchListener: OnDispatchTouchListener? = null

    private val mSmoothScroller: SmoothScroller

    private var mChild: AppBarLayout? = null

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

    init {
        mSmoothScroller = GallerySmoothScroller(context)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val child = nestedAppBar
        if (child != null) {
            val childTop = child.top
            val childHeight = child.height
            val childBottom = child.bottom
            val top = top
            Logger.d("onLayout, childBottom=%d childTop=%d, childHeight=%d, top=%d",
                childBottom, childTop, childHeight, top)
            ViewCompat.offsetTopAndBottom(this, childBottom - top)
            //            ViewGroup.LayoutParams lp = getLayoutParams();
            //            lp.height += child.getHeight();
            //            setLayoutParams(lp);
        }
    }

    override fun scrollToPosition(position: Int) {
        val lm = layoutManager ?: return
        mSmoothScroller.targetPosition = position
        lm.startSmoothScroll(mSmoothScroller)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (mOnDispatchTouchListener != null) {
            if (mOnDispatchTouchListener!!.onDispatchTouchEvent(this, e)) {
                return true
            }
        }

        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> Logger.d("GalleryRecyclerView: onTouchEvent ACTION_DOWN")
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> Logger.d("GalleryRecyclerView: onTouchEvent ACTION_UP")
        }
        return super.onTouchEvent(e)
    }

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (mOnDispatchTouchListener != null) {
                    mOnDispatchTouchListener!!.onDispatchTouchEvent(this, e)
                }
                Logger.d("GalleryRecyclerView: dispatchTouchEvent ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> Logger.d("GalleryRecyclerView: dispatchTouchEvent ACTION_UP")
        }
        return super.dispatchTouchEvent(e)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> Logger.d("GalleryRecyclerView: onInterceptTouchEvent ACTION_DOWN")
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> Logger.d("GalleryRecyclerView: onInterceptTouchEvent ACTION_UP")
        }
        return super.onInterceptTouchEvent(e)
    }

    fun setOnDispatchTouchListener(listener: OnDispatchTouchListener?) {
        mOnDispatchTouchListener = listener
    }

    interface OnDispatchTouchListener {
        fun onDispatchTouchEvent(view: View, e: MotionEvent): Boolean
    }

    class GallerySmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return super.computeScrollVectorForPosition(targetPosition)
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
        }

        companion object {

            private val MILLISECONDS_PER_INCH = 50f //default is 25f (bigger == slower)
        }
    }
}
