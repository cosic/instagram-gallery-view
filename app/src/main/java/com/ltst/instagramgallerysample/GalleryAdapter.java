package com.ltst.instagramgallerysample;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cosic.instagallery.data.GalleryData;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<GalleryData> mItems = new ArrayList<>();
    private List<Integer> mColors = new ArrayList<>();
    @Nullable
    private OnGalleryClickListener mOnGalleryClickListener;

    public GalleryAdapter(final Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        for (final String s : mContext.getResources().getStringArray(R.array.default_color_choice_values)) {
            mColors.add(Color.parseColor(s));
        }
    }

    public void setOnGalleryClickListener(@Nullable final OnGalleryClickListener listener) {
        mOnGalleryClickListener = listener;
    }

    public void clear() {
        mItems.clear();
    }

    public void addAll(final List<GalleryData> items) {
        mItems.addAll(items);
    }

    public GalleryData getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = mInflater.inflate(R.layout.gallery_item_view, parent, false);
        final GalleryViewHolder viewHolder = new GalleryViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mOnGalleryClickListener != null) {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    GalleryData item = getItem(adapterPosition);
                    mOnGalleryClickListener.onClick(adapterPosition, item);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, final int position) {
        GalleryData item = getItem(position);
        View view = holder.getView();
        View viewById = view.findViewById(R.id.item_view);
        TextView textView = view.findViewById(R.id.item_value);
        viewById.setBackgroundColor(getColor(position));
        textView.setText("" + item.value);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    private int getColor(int position) {
        int i = position % mColors.size();
        return mColors.get(i);
    }

    public static final class GalleryViewHolder extends RecyclerView.ViewHolder {

        public GalleryViewHolder(final View itemView) {
            super(itemView);
        }

        public View getView() {
            return itemView;
        }
    }

    public interface OnGalleryClickListener {
        void onClick(int position, GalleryData item);
    }
}
