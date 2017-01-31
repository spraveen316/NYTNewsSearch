package com.praveens.nytnewssearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.activities.ArticleActivity;
import com.praveens.nytnewssearch.models.Article;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    public void addAll() {

    }

    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.article_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.article_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Article currentArticle = articles.get(position);
        viewHolder.headline.setText(currentArticle.getHeadline());

        Picasso.with(context).load(currentArticle.getThumbnail())
                //.error(R.drawable.)
                //.placeholder(R.drawable.imageviewplaceholder)
                .into(viewHolder.thumbnail);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArticleActivity.class);
                Article article = articles.get(position);
                intent.putExtra("article", Parcels.wrap(article));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvHeadline)
        TextView headline;
        @BindView(R.id.ivThumbnail)
        ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
