package com.praveens.nytnewssearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.praveens.nytnewssearch.utilities.Constants;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by praveens on 1/31/17.
 */

public class DatePickerFragment extends DialogFragment {//implements DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = "DatePickerFragment";
    DatePickerDialog.OnDateSetListener onDateSet;
    public SharedPreferences settingsPref;

    public DatePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        settingsPref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_SETTINGS, 0);
        return setDateFromSharedPref(settingsPref);
    }

    private Dialog setDateFromSharedPref(SharedPreferences settingsPref) {
        if (StringUtils.isNotBlank(settingsPref.getString(Constants.SHARED_PREF_BEGIN_DATE, null))) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
            try {
                cal.setTime(sdf.parse(settingsPref.getString(Constants.SHARED_PREF_BEGIN_DATE, null)));
                return new DatePickerDialog(getActivity(), onDateSet, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            } catch (ParseException e) {
                Log.w(LOG_TAG, "When parsing date:" + settingsPref.getString(Constants.SHARED_PREF_BEGIN_DATE, null) + "exception occurred:" + e);
            }
        }

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }

}
