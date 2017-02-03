package com.praveens.nytnewssearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.praveens.nytnewssearch.R;
import com.praveens.nytnewssearch.models.Settings;
import com.praveens.nytnewssearch.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.media.tv.TvContract.Programs.Genres.ARTS;
import static android.provider.Settings.System.DATE_FORMAT;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;
import static com.praveens.nytnewssearch.R.drawable.settings;
import static com.praveens.nytnewssearch.R.id.cbArts;
import static com.praveens.nytnewssearch.R.id.cbFS;
import static com.praveens.nytnewssearch.R.id.cbSports;
import static com.praveens.nytnewssearch.R.id.etBeginDate;
import static com.praveens.nytnewssearch.R.id.search_edit_frame;
import static com.praveens.nytnewssearch.utilities.Constants.SHARED_PREF_SETTINGS;

import android.widget.Toast;

/**
 * Created by praveens on 1/31/17.
 */

public class SettingsFragment extends DialogFragment {//implements DatePickerFragment.DatePickerListener {

    public static final String LOG_TAG = "SettingsFragment";
    public SharedPreferences settingsPref;

    public interface SaveSettingsDialogListener {
        void onSaveSettings(Settings settings);
    }

    Settings settings;

    Map<Settings.NewsDeskValues, Boolean> checkedNDValues = new HashMap<Settings.NewsDeskValues, Boolean>();

    @BindView(R.id.etBeginDate)
    EditText beginDate;

    @BindView(R.id.btnSave)
    Button save;

    @BindView(R.id.ssort)
    Spinner spinner;

    @BindView(R.id.cbArts)
    CheckBox arts;

    @BindView(R.id.cbFS)
    CheckBox fashionAndStyle;

    @BindView(R.id.cbSports)
    CheckBox sports;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        if (window.getWindowManager() != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
            //window.setLayout((int) (size.x * 0.75), (int) (size.y * 0.75));
            window.setGravity(Gravity.CENTER);
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        String[] values = getActivity().getResources().getStringArray(R.array.sort);//{"newest", "oldest"};
        //Spinner spinner = (Spinner) v.findViewById(R.id.ssort);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        setupCheckboxes();

        settingsPref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_SETTINGS, 0);
        setFieldsFromSharedPref(settingsPref);

        return view;
        //return getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, container);
        //return inflater.inflate(R.layout.fragment_settings, container);
    }

    private void setFieldsFromSharedPref(SharedPreferences settingsPref) {
        beginDate.setText(settingsPref.getString(Constants.SHARED_PREF_BEGIN_DATE, null));
        arts.setChecked(settingsPref.getBoolean(Constants.SHARED_PREF_ARTS, false));
        fashionAndStyle.setChecked(settingsPref.getBoolean(Constants.SHARED_PREF_FASHION, false));
        sports.setChecked(settingsPref.getBoolean(Constants.SHARED_PREF_SPORTS, false));
        spinner.setSelection(settingsPref.getInt(Constants.SHARED_PREF_SPINNER_POSITION, 0));
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.i(LOG_TAG, "year=" + year + ", monthOfYear=" + monthOfYear + ", dayOfMonth=" + dayOfMonth);
            beginDate.setText(formatDate(year, monthOfYear, dayOfMonth));
        }
    };

    private String formatDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY);
        sdf.setCalendar(calendar);
        return sdf.format(calendar.getTime());
    }

    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean checked) {
            switch (view.getId()) {
                case R.id.cbArts:
                    if (checked) {
                        checkedNDValues.put(Settings.NewsDeskValues.ARTS, true);
                    } else {
                        checkedNDValues.put(Settings.NewsDeskValues.ARTS, false);
                    }
                    break;
                case R.id.cbFS:
                    if (checked) {
                        checkedNDValues.put(Settings.NewsDeskValues.FASHION_STYLE, true);
                    } else {
                        checkedNDValues.put(Settings.NewsDeskValues.FASHION_STYLE, false);
                    }
                    break;
                case R.id.cbSports:
                    if (checked) {
                        checkedNDValues.put(Settings.NewsDeskValues.SPORTS, true);
                    } else {
                        checkedNDValues.put(Settings.NewsDeskValues.SPORTS, false);
                    }
                    break;
            }
        }
    };

    private void setupCheckboxes() {
        arts.setOnCheckedChangeListener(checkListener);
        fashionAndStyle.setOnCheckedChangeListener(checkListener);
        sports.setOnCheckedChangeListener(checkListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        beginDate.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        beginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DatePickerFragment newFragment = new DatePickerFragment();
                // newFragment.show(getFragmentManager(), "datePicker");
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setCallBack(onDate);
                datePickerFragment.show(getFragmentManager().beginTransaction(), Constants.FRAGMENT_DATE_PICKER_TAG);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSettingsDialogListener saveSettingsDialogListener = (SaveSettingsDialogListener) getActivity();
                String[] values = getActivity().getResources().getStringArray(R.array.sort);
                settings = new Settings(beginDate.getText().toString(), values[spinner.getSelectedItemPosition()], checkedNDValues);
                Log.i(LOG_TAG, "settings=" + settings);
                saveSettingsDialogListener.onSaveSettings(settings);

                SharedPreferences.Editor editor = settingsPref.edit();
                editor.putString(Constants.SHARED_PREF_BEGIN_DATE, beginDate.getText().toString());
                editor.putString(Constants.SHARED_PREF_SORT_SELECTION, values[spinner.getSelectedItemPosition()]);
                editor.putInt(Constants.SHARED_PREF_SPINNER_POSITION, spinner.getSelectedItemPosition());
                if (settings.getCheckedNDValues().get(Settings.NewsDeskValues.ARTS) != null) {
                    editor.putBoolean(Constants.SHARED_PREF_ARTS, settings.getCheckedNDValues().get(Settings.NewsDeskValues.ARTS));
                } else {
                    editor.putBoolean(Constants.SHARED_PREF_ARTS, false);
                }
                if (settings.getCheckedNDValues().get(Settings.NewsDeskValues.FASHION_STYLE) != null) {
                    editor.putBoolean(Constants.SHARED_PREF_FASHION, settings.getCheckedNDValues().get(Settings.NewsDeskValues.FASHION_STYLE));
                } else {
                    editor.putBoolean(Constants.SHARED_PREF_FASHION, false);
                }
                if (settings.getCheckedNDValues().get(Settings.NewsDeskValues.SPORTS) != null) {
                    editor.putBoolean(Constants.SHARED_PREF_SPORTS, settings.getCheckedNDValues().get(Settings.NewsDeskValues.SPORTS));
                } else {
                    editor.putBoolean(Constants.SHARED_PREF_SPORTS, false);
                }

                editor.commit();

                dismiss();
            }
        });
    }

}