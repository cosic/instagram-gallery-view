package com.ltst.instagramgallerysample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cosic.instagallery.data.GalleryData;
import com.cosic.instagallery.gallery.GalleryAppBarLayout;
import com.cosic.instagallery.gallery.GalleryRecyclerView;
import com.cosic.instagallery.utils.EndlessRecyclerScrollListener;
import com.cosic.instagallery.utils.GridSpacingItemDecoration;
import com.cosic.instagallery.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int spanCount = getResources().getInteger(R.integer.span_count);

        final GalleryAppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setOnCollapseChangeStateListener(new GalleryAppBarLayout.OnCollapseChangeStateListener() {
            @Override
            public void onCollapsed() {
                Logger.d("OnCollapseChangeStateListener: collapsed");
            }

            @Override
            public void onExpended() {
                Logger.d("OnCollapseChangeStateListener: expended");
            }
        });

        final Button collapseButton = findViewById(R.id.collapse_button);
        final Button expandButton = findViewById(R.id.expand_button);
        final GalleryRecyclerView recyclerView = findViewById(R.id.gallery);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(
                spanCount, getResources().getDimensionPixelSize(R.dimen.divider_size), false));

        recyclerView.addOnScrollListener(new EndlessRecyclerScrollListener(layoutManager, 0) {
            @Override
            protected void onScrolledToEndChanged(final boolean isEnded) {
                if (isEnded) {
                    appBarLayout.expand();
                }
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
            public void onClick(final int position, final GalleryData item) { // TODO move to library;
                Toast.makeText(MainActivity.this, "Click by " + item.value, Toast.LENGTH_SHORT).show();
                appBarLayout.expand();
                recyclerView.scrollToPosition(position);
            }
        });

        mockData(adapter);
    }

    private void mockData(GalleryAdapter adapter) {
        List<GalleryData> items = new ArrayList<>();
        int count = 100;
        for (int i = 0; i < count; i++) {
            items.add(new GalleryData(i + 1));
        }
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
