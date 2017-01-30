package com.praveens.nytnewssearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.praveens.nytnewssearch.adapter.SearchRecyclerViewAdapter;
import com.praveens.nytnewssearch.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = "XXXXXXXXXX";//SearchActivity.class.getName();
    private static final String API_KEY = "20a186875f0b4cc7803573e6ca94d2ef";
    private final String NYT_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private List<Article> articles = new ArrayList<Article>();

    private RecyclerView.Adapter adapter;

    @BindView(R.id.rvArticleGrid)
    RecyclerView recyclerView;

    @BindView(R.id.etSearch)
    EditText etSearch;

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

    }

    public void onSearchClick(View view) {
        //Toast.makeText(this, "Searching for " + etSearch.getText().toString(), Toast.LENGTH_SHORT).show();
        refreshArticle(etSearch.getText().toString());
    }

    private void refreshArticle(String searchText) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
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
                        Log.d("XXXXXXXX", articles.get(i).toString());
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
