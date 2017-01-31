package com.praveens.nytnewssearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.empty;

/**
 * Created by praveens on 1/30/17.
 */

@Parcel
public class Article {
    private String headline;
    private String webUrl;
    private String thumbnail;

    public Article() {
    }

    public Article(JSONObject jsonObject) throws JSONException {
        this.headline = jsonObject.getJSONObject("headline").get("main").toString();
        this.webUrl = jsonObject.getString("web_url");

        JSONArray multimedia = jsonObject.getJSONArray("multimedia");

        if (multimedia != null && multimedia.length() > 0) {
            JSONObject multimediaFirst = multimedia.getJSONObject(0);
            this.thumbnail = "http://www.nytimes.com/" + multimediaFirst.getString("url");
        }

    }

    public String getHeadline() {
        return headline;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public static List<Article> fromJSONArray(JSONArray articlesJSONArray) throws JSONException {
        List<Article> articles = new ArrayList<Article>();

        for (int i = 0; i < articlesJSONArray.length(); i++) {
            articles.add(new Article(articlesJSONArray.getJSONObject(i)));
        }

        return articles;
    }
}
