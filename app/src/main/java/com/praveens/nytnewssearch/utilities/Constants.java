package com.praveens.nytnewssearch.utilities;

/**
 * Created by praveens on 2/2/17.
 */

public class Constants {
    public static final String NYT_API_KEY = "20a186875f0b4cc7803573e6ca94d2ef";
    public static final String NYT_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static final String STARTING_PAGINATION_NUMBER = "0";
    public static final int ARTICLE_SEARCH_GRID_COLUMNS = 3;

    public static final String FRAGMENT_SETTINGS_TAG = "fragment_settings";
    public static final String FRAGMENT_DATE_PICKER_TAG = "datePicker";

    public static final String QUERY_PARAM_API_KEY = "api-key";
    public static final String QUERY_PARAM_PAGE = "page";
    public static final String QUERY_PARAM_Q = "q";
    public static final String QUERY_PARAM_BEGIN_DATE = "begin_date";
    public static final String QUERY_PARAM_SORT = "sort";
    public static final String QUERY_PARAM_FQ = "fq";

    public static final String DATE_FORMAT_DISPLAY = "MMM dd yy";
    public static final String DATE_FORMAT_FOR_QUERY = "yyyyMMdd";

    public static final String SHARED_PREF_SETTINGS = "SettingsPrefFile";
    public static final String SHARED_PREF_BEGIN_DATE = "begin_date";
    public static final String SHARED_PREF_SORT_SELECTION = "sort_selection";
    public static final String SHARED_PREF_ARTS = "arts";
    public static final String SHARED_PREF_FASHION = "fashion";
    public static final String SHARED_PREF_SPORTS = "sports";
    public static final String SHARED_PREF_SPINNER_POSITION = "spinner_position";
}
