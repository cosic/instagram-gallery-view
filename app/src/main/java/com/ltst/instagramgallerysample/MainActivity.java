package com.ltst.instagramgallerysample;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ltst.instagramgallerysample.data.GalleryData;
import com.ltst.instagramgallerysample.gallery.GalleryAppBarLayout;
import com.ltst.instagramgallerysample.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button collapseButton = findViewById(R.id.collapse_button);
        final Button expandButton = findViewById(R.id.expand_button);
//        final NestedScrollView recyclerView = findViewById(R.id.recyclerView);
        final RecyclerView recyclerView = findViewById(R.id.gallery);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(
                3, getResources().getDimensionPixelSize(R.dimen.divider_size), false));

        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {

            private static final float MILLISECONDS_PER_INCH = 50f; //default is 25f (bigger = slower)

            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return super.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        final GalleryAppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setOnCollapseChangeStateListener(new GalleryAppBarLayout.OnCollapseChangeStateListener() {
            @Override
            public void onCollapsed() {
                Timber.d("OnCollapseChangeStateListener: collapsed");
            }

            @Override
            public void onExpended() {
                Timber.d("OnCollapseChangeStateListener: expended");
            }
        });

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.collapse();
            }
        });
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.expand();
            }
        });

        GalleryAdapter adapter = new GalleryAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnGalleryClickListener(new GalleryAdapter.OnGalleryClickListener() {
            @Override
            public void onClick(final int position, final GalleryData item) {
                Toast.makeText(MainActivity.this, "Click by " + item.value, Toast.LENGTH_SHORT).show();
                appBarLayout.expand();
//                layoutManager.scrollToPositionWithOffset(position, 0);
//                recyclerView.smoothScrollToPosition(position);
                smoothScroller.setTargetPosition(position);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        });

        List<GalleryData> items = new ArrayList<>();
        int count = 60;
        for (int i = 0; i < count; i++) {
            items.add(new GalleryData(i + 1));
        }
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
