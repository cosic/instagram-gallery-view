package com.cosic.instagallery

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerScrollListener
@JvmOverloads
constructor(
    private val layoutManager: RecyclerView.LayoutManager,
    /**
    * The minimum amount of items to have below your current scroll position
    * before mLoading more.
    */
   private val visibleThreshold: Int = DEFAULT_VISIBLE_THRESHOLD
) : RecyclerView.OnScrollListener() {

    /**
     * True if RecyclerView is scrolled up to end of list items;
     */
    private var isScrolledToEnd: Boolean = false

    init {
        require(layoutManager is GridLayoutManager
            || layoutManager is LinearLayoutManager
            || layoutManager is StaggeredGridLayoutManager) { "Unknown Layout Manager parent class;" }
    }

    /**
     * This happens many times a second during a scroll, so be wary of the code you place here.
     * We are given a few useful parameters to help us work out if we need to load some more com.ltst.instagramgallerysample.data,
     * but first we check if we are waiting for the previous load to finish.
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPositions = IntArray(2)
        var firstVisibleItem = 0
        firstVisibleItem = when (layoutManager) {
            is StaggeredGridLayoutManager -> layoutManager.findFirstVisibleItemPositions(firstVisibleItemPositions)[0]
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> -1
        }

        onScrolled(firstVisibleItem, visibleItemCount, totalItemCount)

        val isEnd = totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold
        isScrolledToEnd = isEnd
        onScrolledToEndChanged(isEnd) // TODO handle the end of recyclerview;
        //        if (mIsScrolledToEnd != isEnd) {
        //            mIsScrolledToEnd = isEnd;
        //            onScrolledToEndChanged(isEnd);
        //        }
        // TODO handle the end of recyclerview;
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
        private const val DEFAULT_VISIBLE_THRESHOLD = 10
    }
}