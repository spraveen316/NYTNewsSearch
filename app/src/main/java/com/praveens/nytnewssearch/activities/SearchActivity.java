package com.praveens.nytnewssearch.activities;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.adapter.SearchRecyclerViewAdapter;
import com.praveens.nytnewssearch.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.support.v7.widget.SearchView;

public class SearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = SearchActivity.class.getName();
    private static final String NYT_API_KEY = "20a186875f0b4cc7803573e6ca94d2ef";
    private static final String NYT_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private List<Article> articles = new ArrayList<Article>();

    private RecyclerView.Adapter adapter;

    final Gson gson = new Gson();
    private OkHttpClient client = new OkHttpClient();

    @BindView(R.id.rvArticleGrid)
    RecyclerView recyclerView;

    private MenuItem searchMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchArticles(query);
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

        GridLayoutManager grid = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(grid);

        articles = new ArrayList<Article>();
        adapter = new SearchRecyclerViewAdapter(this, articles);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        //getSupportActionBar().setTitle("My new title"); // set the top title
        //String title = actionBar.getTitle().toString(); // get the title
        //actionBar.hide(); // or even hide the actionbar

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miSearch:
                doSearch();
                return true;
            case R.id.miSettings:
                Log.d(LOG_TAG, "Settings...");
                doSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doSettings() {
    }

    private void doSearch() {
    }

/*    public void onSearchClick(View view) {
    }*/

    private void fetchArticles(String searchText) {
     /*   AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", NYT_API_KEY);
        params.put("page", 0);
        params.put("q", searchText);

        client.get(NYT_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articlesJSON;

                try {
                    articlesJSON = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articlesJSON));

                    for (int i = 0; i < articles.size(); i++) {
                        //Log.d("XXXXXXXX", articles.get(i).toString());
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }); */


        HttpUrl.Builder urlBuilder = HttpUrl.parse(NYT_URL).newBuilder();
        urlBuilder.addQueryParameter("api-key", NYT_API_KEY);
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("q", searchText);
        String url = urlBuilder.build().toString();

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
                    Log.d(LOG_TAG, responseData);
                    JSONObject json = new JSONObject(responseData);
                    articlesJSON = json.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articlesJSON));

                    for (int i = 0; i < articles.size(); i++) {
                        Log.d(LOG_TAG, articles.get(i).toString());
                    }

                    // Run view-related code back on the main thread
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
