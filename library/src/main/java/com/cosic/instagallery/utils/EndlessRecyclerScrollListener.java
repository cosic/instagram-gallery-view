package com.cosic.instagallery.utils;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerScrollListener extends RecyclerView.OnScrollListener {

    private static final int DEFAULT_VISIBLE_THRESHOLD = 10;

    /**
     * The minimum amount of items to have below your current scroll position
     * before mLoading more.
     */
    private int mVisibleThreshold;

    /**
     * True if RecyclerView is scrolled up to end of list items;
     */
    private boolean mIsScrolledToEnd;

    private final RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerScrollListener(RecyclerView.LayoutManager layoutManager) {
        this(layoutManager, DEFAULT_VISIBLE_THRESHOLD);
    }

    public EndlessRecyclerScrollListener(RecyclerView.LayoutManager layoutManager, int visibleThreshold) {
        this.mLayoutManager = layoutManager;
        this.mVisibleThreshold = visibleThreshold;

        if (!(layoutManager instanceof GridLayoutManager
                || layoutManager instanceof LinearLayoutManager
                || layoutManager instanceof StaggeredGridLayoutManager)) {
            throw new IllegalArgumentException("Unknown Layout Manager parent class;");
        }
    }

    /**
     * This happens many times a second during a scroll, so be wary of the code you place here.
     * We are given a few useful parameters to help us work out if we need to load some more com.cosic.instagallery.data,
     * but first we check if we are waiting for the previous load to finish.
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int[] firstVisibleItemPositions = new int[2];
        int firstVisibleItem = 0;
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            firstVisibleItem = ((StaggeredGridLayoutManager) mLayoutManager)
                    .findFirstVisibleItemPositions(firstVisibleItemPositions)[0];
        } else if (mLayoutManager instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        onScrolled(firstVisibleItem, visibleItemCount, totalItemCount);

        boolean isEnd = (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold);
            mIsScrolledToEnd = isEnd;
            onScrolledToEndChanged(isEnd); // TODO handle the end of recyclerview;
//        if (mIsScrolledToEnd != isEnd) {
//            mIsScrolledToEnd = isEnd;
//            onScrolledToEndChanged(isEnd);
//        }

    }

    protected void onScrolled(int firstVisibleItem, int visibleItemCount, int totalItemsCount) {
        // Empty method for overriding if you would to handle scroll events;
    }

    /**
     * Is called when {@link #mIsScrolledToEnd} changes it state;
     * @param isEnded - true if RecyclerView is scrolled up to end of list items. Otherwise false;
     */
    abstract protected void onScrolledToEndChanged(boolean isEnded);

}