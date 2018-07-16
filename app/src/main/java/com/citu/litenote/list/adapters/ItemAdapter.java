package com.citu.litenote.list.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.citu.litenote.R;
import com.citu.litenote.data.models.Item;
import com.citu.litenote.list.viewholders.ItemViewHolder;
import com.citu.litenote.ui.icons.Icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shemchavez on 3/12/2018.
 */

public class ItemAdapter extends RecyclerView.Adapter implements Filterable {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public List<Item> mItems = new ArrayList<>();
    public List<Item> mItemsFiltered = new ArrayList<>();

    public ItemAdapter(Context context, List<Item> items) {
        mContext = context;
        mItems = items;
        mItemsFiltered = mItems;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.viewholder_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Item item = mItemsFiltered.get(position);
        HashMap hashMap = Icons.getIcon(mContext, item.getFile());
        itemViewHolder.mImageViewFileType.setImageDrawable((Drawable) hashMap.get("drawable"));
        itemViewHolder.mTextViewPrimaryText.setText(item.getName());
        itemViewHolder.mTextViewSeondaryText.setText(item.getParseCreatedDate());
    }

    @Override
    public int getItemCount() {
        return mItemsFiltered.size();
    }

    public void swapItems(List<Item> items) {
        if (items != null) {
            mItemsFiltered = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                if (searchText.isEmpty()) {
                    mItemsFiltered = mItems;
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item row : mItems) {
                        if (row.getName().toLowerCase().contains(searchText) || row.getParseCreatedDate().toLowerCase().contains(searchText)) {
                            filteredList.add(row);
                        }
                    }
                    mItemsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mItemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mItemsFiltered = (ArrayList<Item>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
