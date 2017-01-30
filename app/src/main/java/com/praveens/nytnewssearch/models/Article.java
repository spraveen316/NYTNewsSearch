package com.praveens.nytnewssearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by praveens on 1/30/17.
 */

public class Article {
    private String headline;
    private String webUrl;
    private String thumbnail;

    public Article(String headline, String webUrl, String thumbnail) {
        this.headline = headline;
        this.webUrl = webUrl;
        this.thumbnail = thumbnail;
    }

    public Article(JSONObject jsonObject) throws JSONException {
        this.headline = jsonObject.getJSONObject("headline").get("main").toString();
        this.webUrl = jsonObject.getString("web_url");

        JSONArray mutimedia = jsonObject.getJSONArray("multimedia");

        if (mutimedia != null && mutimedia.length() > 0) {
            JSONObject mutimediaFirst = mutimedia.getJSONObject(0);
            this.thumbnail = "http://www.nytimes.com/" + mutimediaFirst.getString("url");
        }

    }

    @Override
    public String toString() {
        return "Article{" +
                "headline='" + headline + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
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
