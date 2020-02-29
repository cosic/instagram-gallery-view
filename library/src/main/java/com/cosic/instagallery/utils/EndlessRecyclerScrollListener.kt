package com.cosic.instagallery.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerScrollListener
@JvmOverloads
constructor(
    private val mLayoutManager: RecyclerView.LayoutManager,
   /**
    * The minimum amount of items to have below your current scroll position
    * before mLoading more.
    */
   private val mVisibleThreshold: Int = DEFAULT_VISIBLE_THRESHOLD
) : RecyclerView.OnScrollListener() {

    /**
     * True if RecyclerView is scrolled up to end of list items;
     */
    private var mIsScrolledToEnd: Boolean = false

    init {

        require(mLayoutManager is GridLayoutManager
            || mLayoutManager is LinearLayoutManager
            || mLayoutManager is StaggeredGridLayoutManager) { "Unknown Layout Manager parent class;" }
    }

    /**
     * This happens many times a second during a scroll, so be wary of the code you place here.
     * We are given a few useful parameters to help us work out if we need to load some more com.cosic.instagallery.data,
     * but first we check if we are waiting for the previous load to finish.
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        val visibleItemCount = mLayoutManager.childCount
        val totalItemCount = mLayoutManager.itemCount
        val firstVisibleItemPositions = IntArray(2)
        var firstVisibleItem = 0
        if (mLayoutManager is StaggeredGridLayoutManager) {
            firstVisibleItem = mLayoutManager
                .findFirstVisibleItemPositions(firstVisibleItemPositions)[0]
        } else if (mLayoutManager is GridLayoutManager) {
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
        }

        onScrolled(firstVisibleItem, visibleItemCount, totalItemCount)

        val isEnd = totalItemCount - visibleItemCount <= firstVisibleItem + mVisibleThreshold
        mIsScrolledToEnd = isEnd
        onScrolledToEndChanged(isEnd) // TODO handle the end of recyclerview;
        //        if (mIsScrolledToEnd != isEnd) {
        //            mIsScrolledToEnd = isEnd;
        //            onScrolledToEndChanged(isEnd);
        //        }

    }

    protected fun onScrolled(firstVisibleItem: Int, visibleItemCount: Int, totalItemsCount: Int) {
        // Empty method for overriding if you would to handle scroll events;
    }

    /**
     * Is called when [.mIsScrolledToEnd] changes it state;
     * @param isEnded - true if RecyclerView is scrolled up to end of list items. Otherwise false;
     */
    protected abstract fun onScrolledToEndChanged(isEnded: Boolean)

    companion object {

        private val DEFAULT_VISIBLE_THRESHOLD = 10
    }

}