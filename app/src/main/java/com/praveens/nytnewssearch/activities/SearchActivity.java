package com.praveens.nytnewssearch.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.adapter.SearchRecyclerViewAdapter;
import com.praveens.nytnewssearch.fragments.SettingsFragment;
import com.praveens.nytnewssearch.models.Article;
import com.praveens.nytnewssearch.models.Settings;

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
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.SaveSettingsDialogListener {

    @BindView(R.id.rvArticleGrid)
    RecyclerView recyclerView;

    private static final String LOG_TAG = SearchActivity.class.getName();
    private static final String NYT_API_KEY = "20a186875f0b4cc7803573e6ca94d2ef";
    private static final String NYT_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private List<Article> articles = new ArrayList<Article>();

    private RecyclerView.Adapter adapter;
    private OkHttpClient client = new OkHttpClient();

    private MenuItem searchMenuItem;
    private Settings settings;

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
        settingsFragment.show(fm, "fragment_settings");
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

    private void fetchArticles(String searchText) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(NYT_URL).newBuilder();
        urlBuilder.addQueryParameter("api-key", NYT_API_KEY);
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("q", searchText);

        Log.d(LOG_TAG, "settings=" + settings);

        if (settings != null) {
            if (settings.getBeginDate() != null) {
                urlBuilder.addQueryParameter("begin_date", settings.getBeginDate());
            }
            if (StringUtils.isNotBlank(settings.getSortSelection())) {
                urlBuilder.addQueryParameter("sort", settings.getSortSelection());
            }
            if (settings.getCheckedNDValues() != null && !settings.getCheckedNDValues().isEmpty()) {
                //&fq=news_desk:("Sports" "Foreign")
                urlBuilder.addEncodedQueryParameter("fq", buildFQParamvalue(settings.getCheckedNDValues()));
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
                    Log.d(LOG_TAG, responseData);
                    JSONObject json = new JSONObject(responseData);
                    articlesJSON = json.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articlesJSON));

                    for (int i = 0; i < articles.size(); i++) {
                        //Log.d(LOG_TAG, articles.get(i).toString());
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

    @Override
    public void onSaveSettings(Settings savedSettings) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MMM dd yy", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
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
        Toast.makeText(this, "Settings=" + savedSettings.getBeginDate() + ", " + settings.getSortSelection()
                + ", " + savedSettings.getCheckedNDValues(), Toast.LENGTH_SHORT).show();
    }
}
