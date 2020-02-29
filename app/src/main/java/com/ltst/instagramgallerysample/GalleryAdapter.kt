package com.ltst.instagramgallerysample

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cosic.instagallery.data.GalleryData

import java.util.ArrayList

class GalleryAdapter(context: Context) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mItems = ArrayList<GalleryData>()
    private val mColors = ArrayList<Int>()
    private var mOnGalleryClickListener: OnGalleryClickListener? = null

    init {
        for (s in context.resources.getStringArray(R.array.default_color_choice_values)) {
            mColors.add(Color.parseColor(s))
        }
    }

    fun setOnGalleryClickListener(listener: OnGalleryClickListener?) {
        mOnGalleryClickListener = listener
    }

    fun clear() {
        mItems.clear()
    }

    fun addAll(items: List<GalleryData>) {
        mItems.addAll(items)
    }

    fun getItem(position: Int): GalleryData {
        return mItems[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = mInflater.inflate(R.layout.gallery_item_view, parent, false)
        val viewHolder = GalleryViewHolder(view)
        view.setOnClickListener {
            if (mOnGalleryClickListener != null) {
                val adapterPosition = viewHolder.adapterPosition
                val item = getItem(adapterPosition)
                mOnGalleryClickListener!!.onClick(adapterPosition, item)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = getItem(position)
        val view = holder.view
        val viewById = view.findViewById<View>(R.id.item_view)
        val textView = view.findViewById<TextView>(R.id.item_value)
        viewById.setBackgroundColor(getColor(position))
        textView.text = "" + item.value
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    private fun getColor(position: Int): Int {
        val i = position % mColors.size
        return mColors[i]
    }

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val view: View
            get() = itemView
    }

    interface OnGalleryClickListener {
        fun onClick(position: Int, item: GalleryData)
    }
}
