package com.ltst.instagramgallerysample;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button collapseButton = findViewById(R.id.collapse_button);
        final Button expandButton = findViewById(R.id.expand_button);
        final NestedScrollView gallery = findViewById(R.id.gallery);
        final GalleryAppBarLayout appBarLayout = findViewById(R.id.appbar);

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
    }
}
