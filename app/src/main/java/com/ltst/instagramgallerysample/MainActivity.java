package com.ltst.instagramgallerysample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.ltst.instagramgallerysample.gallery.GalleryAppBarLayout;

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
//        final NestedScrollView gallery = findViewById(R.id.gallery);
        final RecyclerView gallery = findViewById(R.id.gallery);
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
        gallery.setLayoutManager(new GridLayoutManager(this, 3));
        gallery.setAdapter(adapter);

        List<GalleryData> items = new ArrayList<>();
        int count = 60;
        for (int i = 0; i < count; i++) {
            items.add(new GalleryData());
        }
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
