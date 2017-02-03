package com.praveens.nytnewssearch.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.utilities.DynamicHeightImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by praveens on 2/2/17.
 */

public class DefaultViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvHeadline)
    TextView headline;
    @BindView(R.id.ivThumbnail)
    DynamicHeightImageView thumbnail;

    public DefaultViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getHeadline() {
        return headline;
    }

    public DynamicHeightImageView getThumbnail() {
        return thumbnail;
    }
}
