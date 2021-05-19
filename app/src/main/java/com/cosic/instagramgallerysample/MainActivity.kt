package com.cosic.instagramgallerysample

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cosic.instagallery.GalleryAppBarLayout
import com.cosic.instagallery.GalleryRecyclerView
import com.cosic.instagallery.EndlessRecyclerScrollListener
import com.cosic.instagallery.Logger
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spanCount = resources.getInteger(R.integer.span_count)

        val appBarLayout = findViewById<GalleryAppBarLayout>(R.id.appbar)
        appBarLayout.setOnCollapseChangeStateListener(object : GalleryAppBarLayout.OnCollapseChangeStateListener {
            override fun onCollapsed() {
                Logger.d("OnCollapseChangeStateListener: collapsed")
            }

            override fun onExpended() {
                Logger.d("OnCollapseChangeStateListener: expended")
            }
        })

        val collapseButton = findViewById<Button>(R.id.collapse_button)
        val expandButton = findViewById<Button>(R.id.expand_button)
        val recyclerView = findViewById<GalleryRecyclerView>(R.id.gallery)
        val layoutManager = GridLayoutManager(this, spanCount)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(GridSpacingItemDecoration(
            spanCount, resources.getDimensionPixelSize(R.dimen.divider_size), false))

        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager, 0) {
            override fun onScrolledToEndChanged(isEnded: Boolean) {
                if (isEnded) {
                    appBarLayout.expand()
                }
            }
        })

        collapseButton.setOnClickListener { appBarLayout.collapse() }

        expandButton.setOnClickListener { appBarLayout.expand() }

        val adapter = GalleryAdapter(this)
        recyclerView.adapter = adapter
        adapter.setOnGalleryClickListener(object : GalleryAdapter.OnGalleryClickListener {
            override fun onClick(position: Int, item: GalleryData) {
                // TODO move to library;
                Toast.makeText(this@MainActivity, "Click by " + item.value, Toast.LENGTH_SHORT).show()
                appBarLayout.expand()
                recyclerView.scrollToPosition(position)
            }
        })

        mockData(adapter)
    }

    private fun mockData(adapter: GalleryAdapter) {
        val items = ArrayList<GalleryData>()
        val count = 100
        for (i in 0 until count) {
            items.add(GalleryData(i + 1))
        }
        adapter.addAll(items)
        adapter.notifyDataSetChanged()
    }
}
