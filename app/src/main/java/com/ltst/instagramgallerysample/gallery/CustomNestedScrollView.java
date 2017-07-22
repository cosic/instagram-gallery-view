package com.ltst.instagramgallerysample.gallery;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CustomNestedScrollView extends NestedScrollView {

    private AppBarLayout mChild;

    public CustomNestedScrollView(Context context) {
        super(context);
    }

    public CustomNestedScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
}