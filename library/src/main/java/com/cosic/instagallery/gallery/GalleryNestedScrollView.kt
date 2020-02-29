package com.cosic.instagallery.gallery

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout

class GalleryNestedScrollView : NestedScrollView {

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

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

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
}