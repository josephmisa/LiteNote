package com.citu.litenote.list.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.citu.litenote.R;

/**
 * Created by shemchavez on 3/12/2018.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView mImageViewFileType;
    public TextView mTextViewPrimaryText;
    public TextView mTextViewSeondaryText;

    public ItemViewHolder(View itemView) {
        super(itemView);
        mImageViewFileType = itemView.findViewById(R.id.image_view_file_type);
        mTextViewPrimaryText = itemView.findViewById(R.id.text_view_primary_text);
        mTextViewSeondaryText = itemView.findViewById(R.id.text_view_secondary_text);
    }
}
