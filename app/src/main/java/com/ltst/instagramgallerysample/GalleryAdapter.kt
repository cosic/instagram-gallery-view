package com.ltst.instagramgallerysample

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import java.util.ArrayList

class GalleryAdapter(context: Context) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val items = ArrayList<GalleryData>()
    private val colors = ArrayList<Int>()
    private var onGalleryClickListener: OnGalleryClickListener? = null

    init {
        for (s in context.resources.getStringArray(R.array.default_color_choice_values)) {
            colors.add(Color.parseColor(s))
        }
    }

    fun setOnGalleryClickListener(listener: OnGalleryClickListener?) {
        onGalleryClickListener = listener
    }

    fun clear() {
        items.clear()
    }

    fun addAll(items: List<GalleryData>) {
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = inflater.inflate(R.layout.gallery_item_view, parent, false)
        val viewHolder = GalleryViewHolder(view)
        view.setOnClickListener {
            if (onGalleryClickListener != null) {
                val adapterPosition = viewHolder.adapterPosition
                val item = getItem(adapterPosition)
                onGalleryClickListener!!.onClick(adapterPosition, item)
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
        textView.text = item.value.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    private fun getItem(position: Int): GalleryData {
        return items[position]
    }

    private fun getColor(position: Int): Int {
        val i = position % colors.size
        return colors[i]
    }

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val view: View
            get() = itemView
    }

    interface OnGalleryClickListener {
        fun onClick(position: Int, item: GalleryData)
    }
}
