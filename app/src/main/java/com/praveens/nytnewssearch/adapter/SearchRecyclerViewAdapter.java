package com.praveens.nytnewssearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by praveens on 1/30/17.
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    public SearchRecyclerViewAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    private List<Article> articles;
    private Context context;

    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.article_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Article currentArticle = articles.get(position);
        viewHolder.headline.setText(currentArticle.getHeadline());

        Picasso.with(context).load(currentArticle.getThumbnail())
                //.error(R.drawable.)
                //.placeholder(R.drawable.imageviewplaceholder)
                .into(viewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView headline;
        ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            this.headline = (TextView) view.findViewById(R.id.tvHeadline);
            this.thumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
        }
    }
}
