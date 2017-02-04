package com.praveens.nytnewssearch.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.models.Article;
import com.praveens.nytnewssearch.viewholder.DefaultViewHolder;
import com.praveens.nytnewssearch.viewholder.TextOnlyViewHolder;

import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by praveens on 1/30/17.
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int DEFAULT_VIEW = 0, TEXT_ONLY_VIEW = 1;

    public SearchRecyclerViewAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    private List<Article> articles;
    private Context context;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case TEXT_ONLY_VIEW:
                viewHolder = new TextOnlyViewHolder(inflater.inflate(R.layout.article_textonly_item, parent, false));
                break;
            default:
                viewHolder = new DefaultViewHolder(inflater.inflate(R.layout.article_item, parent, false));
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, final int position) {
        final Article currentArticle = articles.get(position);

        switch (customViewHolder.getItemViewType()) {
            case TEXT_ONLY_VIEW:
                TextOnlyViewHolder textOnlyViewHolder = (TextOnlyViewHolder) customViewHolder;
                configureTextOnlyViewHolder(textOnlyViewHolder, currentArticle);
                break;
            default:
                DefaultViewHolder defaultViewHolder = (DefaultViewHolder) customViewHolder;
                configureDefaultViewHolder(defaultViewHolder, currentArticle);
        }

        /*customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArticleActivity.class);
                Article article = articles.get(position);
                intent.putExtra("article", Parcels.wrap(article));
                context.startActivity(intent);
            }
        });*/

        openChromeTab(currentArticle.getWebUrl());
    }

    private void openChromeTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorAccent));

        Intent shareIntent = new Intent();
        shareIntent.setType("text/plain");
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);

        int requestCode = 100;
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                requestCode,
                shareIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_share);
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
        CustomTabsIntent customTabsIntent = builder.build();
        builder.addDefaultShareMenuItem();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    private void configureTextOnlyViewHolder(TextOnlyViewHolder textOnlyViewHolder, Article article) {
        textOnlyViewHolder.getHeadline().setText(article.getHeadline());
        textOnlyViewHolder.getSource().setText(article.getSource());
    }

    private void configureDefaultViewHolder(DefaultViewHolder defaultViewHolder, Article article) {
        defaultViewHolder.getHeadline().setText(article.getHeadline());
        defaultViewHolder.getSource().setText(article.getSource());

        Glide.with(context).load(article.getThumbnail()).centerCrop().dontAnimate()
                .error(R.drawable.imageviewplaceholder)
                .placeholder(R.drawable.imageviewplaceholder)
                .into(defaultViewHolder.getThumbnail());
    }

    @Override
    public int getItemViewType(int position) {
        if (StringUtils.isNotBlank(articles.get(position).getThumbnail())) {
            return DEFAULT_VIEW;
        } else {
            return TEXT_ONLY_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return (null != articles ? articles.size() : 0);
    }

}
