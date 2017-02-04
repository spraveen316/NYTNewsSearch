package com.praveens.nytnewssearch.activities;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.adapter.SearchRecyclerViewAdapter;
import com.praveens.nytnewssearch.fragments.SettingsFragment;
import com.praveens.nytnewssearch.listener.EndlessRecyclerViewScrollListener;
import com.praveens.nytnewssearch.models.Article;
import com.praveens.nytnewssearch.models.Settings;
import com.praveens.nytnewssearch.utilities.Constants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.FrameLayout;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.SaveSettingsDialogListener {

    @BindView(R.id.rvArticleGrid)
    RecyclerView recyclerView;

    private EndlessRecyclerViewScrollListener scrollListener;
    private static final String LOG_TAG = SearchActivity.class.getName();
    private List<Article> articles = new ArrayList<Article>();
    private RecyclerView.Adapter adapter;
    private OkHttpClient client = new OkHttpClient();
    private MenuItem searchMenuItem;
    private Settings settings;
    private String pageNum = Constants.STARTING_PAGINATION_NUMBER;
    private String queryBuff;
    private Snackbar snackbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pageNum = Constants.STARTING_PAGINATION_NUMBER;
                scrollListener.resetState();
                queryBuff = query;
                fetchArticles(query, true, pageNum);
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fetchArticles(query, false, String.valueOf(Integer.valueOf(pageNum) + 1));
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onSearchRequested() {
        if (searchMenuItem != null) {
            searchMenuItem.expandActionView();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        checkIfConnected();

        //  GridLayoutManager gridLayoutManager = new GridLayoutManager(this, Constants.ARTICLE_SEARCH_GRID_COLUMNS);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(Constants.ARTICLE_SEARCH_GRID_COLUMNS, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        articles = new ArrayList<Article>();
        adapter = new SearchRecyclerViewAdapter(this, articles);
        recyclerView.setAdapter(adapter);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextPageFromApi();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        adapter.notifyDataSetChanged();

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        //getSupportActionBar().setTitle("My new title"); // set the top title
        //String title = actionBar.getTitle().toString(); // get the title
        //actionBar.hide(); // or even hide the actionbar

    }

    private boolean checkIfConnected() {
        snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "INTERNET CHECK", Snackbar.LENGTH_INDEFINITE);

        ConnectivityManager c = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo n = c.getActiveNetworkInfo();
        if (n != null) {
            snackbar.dismiss();
            return n.isConnectedOrConnecting();
        }

        View view = snackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snackbar.show();
        return false;
    }

    private void loadNextPageFromApi() {
        pageNum = String.valueOf(Integer.valueOf(pageNum) + 1);
        fetchArticles(queryBuff, false, pageNum);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miSettings:
                doSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doSettings() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = SettingsFragment.newInstance();
        settingsFragment.show(fm, Constants.FRAGMENT_SETTINGS_TAG);
    }

    private String buildFQParamvalue(Map<Settings.NewsDeskValues, Boolean> ndValues) {
        if (ndValues != null && !settings.getCheckedNDValues().isEmpty()) {
            StringBuilder sb = new StringBuilder("news_desk:%28");
            for (Map.Entry<Settings.NewsDeskValues, Boolean> entry : ndValues.entrySet()) {
                if (entry.getValue()) {
                    sb.append("%22").append(entry.getKey().getKey()).append("%22 ");
                }
            }
            sb.append("%29");
            Log.d(LOG_TAG, "sb.toString()=" + sb.toString());
            return sb.toString();
        } else return null;
    }

    private void fetchArticles(String searchText, final boolean clear, final String pageNum) {

        // TODO read from sharedPref
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.NYT_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.QUERY_PARAM_API_KEY, Constants.NYT_API_KEY);
        urlBuilder.addQueryParameter(Constants.QUERY_PARAM_PAGE, pageNum);
        urlBuilder.addQueryParameter(Constants.QUERY_PARAM_Q, searchText);

        if (settings != null) {
            if (settings.getBeginDate() != null) {
                urlBuilder.addQueryParameter(Constants.QUERY_PARAM_BEGIN_DATE, settings.getBeginDate());
            }
            if (StringUtils.isNotBlank(settings.getSortSelection())) {
                urlBuilder.addQueryParameter(Constants.QUERY_PARAM_SORT, settings.getSortSelection());
            }
            if (settings.getCheckedNDValues() != null && !settings.getCheckedNDValues().isEmpty()) {
                //&fq=news_desk:("Sports" "Foreign")
                urlBuilder.addEncodedQueryParameter(Constants.QUERY_PARAM_FQ, buildFQParamvalue(settings.getCheckedNDValues()));
            }
        }
        String url = urlBuilder.build().toString();
        Log.d(LOG_TAG, "url=" + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                JSONArray articlesJSON;

                try {
                    String responseData = response.body().string();
                    //Log.d(LOG_TAG, responseData);
                    JSONObject json = new JSONObject(responseData);
                    articlesJSON = json.getJSONObject("response").getJSONArray("docs");
                    if (clear) {
                        articles.clear();
                    }
                    articles.addAll(Article.fromJSONArray(articlesJSON));

                    // Run view-related code back on the main thread
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (snackbar != null) snackbar.dismiss();
                            if (clear) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.notifyItemRangeInserted((Integer.valueOf(pageNum) * 10) + 1, 10);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        });

    }

    @Override
    public void onSaveSettings(Settings savedSettings) {
        if (savedSettings == null) {
            settings = new Settings();
            return;
        }
        SimpleDateFormat originalFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat(Constants.DATE_FORMAT_FOR_QUERY, Locale.getDefault());
        if (StringUtils.isNotBlank(savedSettings.getBeginDate())) {
            try {
                Date date = originalFormat.parse(savedSettings.getBeginDate());
                savedSettings.setBeginDate(targetFormat.format(date));
            } catch (ParseException e) {
                Log.w(LOG_TAG, "When parsing date:" + savedSettings.getBeginDate() + "exception:" + e.getMessage());
                savedSettings.setBeginDate(null);
            }
        }
        settings = savedSettings;
    }
}
