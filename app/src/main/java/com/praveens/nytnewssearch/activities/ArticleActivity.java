package com.praveens.nytnewssearch.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.models.Article;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {
    private static final String LOG_TAG = ArticleActivity.class.getName();

    @BindView(R.id.wvArticle)
    WebView webview;

    Article article;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        MenuItem shareItem = menu.findItem(R.id.miShare);
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        attachShareIntentAction();
        return true;
    }

    public void attachShareIntentAction() {
        if (miShareAction != null && shareIntent != null)
            miShareAction.setShareIntent(shareIntent);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miShare:
                Log.d(LOG_TAG, "Share...");
                //setupFacebookShareIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    private void setupFacebookShareIntent() {
        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(article.getHeadline())
                .setContentDescription(
                        "\"Body Of Test Post\"")
                .setContentUrl(Uri.parse(article.getWebUrl()))
                .build();

        shareDialog.show(linkContent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        article = Parcels.unwrap(getIntent().getParcelableExtra("article"));

        // Configure related browser settings
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        webview.setWebViewClient(new CustomBrowser());
        // Load the initial URL
        webview.loadUrl(article.getWebUrl());

        // Enable responsive layout
        webview.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the veiwport
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        webview.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page

        prepareShareIntent();
        attachShareIntentAction();
    }

    public void prepareShareIntent() {
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, webview.getUrl());
        shareIntent.setType("image/*");
    }

    private class CustomBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
}
