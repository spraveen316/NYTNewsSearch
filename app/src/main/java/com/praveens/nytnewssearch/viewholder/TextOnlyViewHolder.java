package com.praveens.nytnewssearch.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.praveens.nytnewssearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by praveens on 2/2/17.
 */

public class TextOnlyViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvHeadline)
    TextView headline;

    public TextOnlyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getHeadline() {
        return headline;
    }
}
