package com.praveens.nytnewssearch.models;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

import static android.media.tv.TvContract.Programs.Genres.ARTS;
import static android.media.tv.TvContract.Programs.Genres.SPORTS;
import static com.praveens.nytnewssearch.R.array.sort;

/**
 * Created by praveens on 1/31/17.
 */
@Parcel
public class Settings {
    // YYYYMMDD
    String beginDate;
    String sortSelection;
    Map<NewsDeskValues, Boolean> checkedNDValues = new HashMap<NewsDeskValues, Boolean>();

    public Settings() {
    }

    public Settings(String beginDate, String sortSelection, Map<NewsDeskValues, Boolean> checkedNDValues) {
        this.beginDate = beginDate;
        this.sortSelection = sortSelection;
        if (checkedNDValues == null) {
            this.checkedNDValues = new HashMap<NewsDeskValues, Boolean>();
        } else {
            this.checkedNDValues = checkedNDValues;
        }
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getSortSelection() {
        return sortSelection;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public Map<NewsDeskValues, Boolean> getCheckedNDValues() {
        return checkedNDValues;
    }

    public Map<NewsDeskValues, Boolean> addToCheckedNDValues(NewsDeskValues key, Boolean value) {
        checkedNDValues.put(key, value);
        return checkedNDValues;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "beginDate='" + beginDate + '\'' +
                ", sortSelection='" + sortSelection + '\'' +
                ", checkedNDValues=" + checkedNDValues +
                '}';
    }

    public static enum NewsDeskValues {

        ARTS("Arts"), FASHION_STYLE("Fashion & Style"), SPORTS("Sports");

        private final String key;

        private NewsDeskValues(String name) {
            key = name;
        }

        public String getKey() {
            return key;
        }
    }
}
