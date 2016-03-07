package com.cerebellio.noted;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.PreferenceFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Settings screen allows user to make a number of changes within the app
 */
public class ActivitySettings extends ActivityBase {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    private List<android.app.Fragment> mStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, UtilityFunctions.getResIdFromAttribute(R.attr.colorPrimary, this)));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_settings_frame, new FragmentSettings())
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (mStack.size() > 0) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_settings_frame, mStack.remove(mStack.size() - 1))
                    .commit();
        } else {
            super.onBackPressed();
        }
    }


    private void switchToScreen(String key) {
        FragmentSettings fragmentSettings = new FragmentSettings();
        Bundle bundle = new Bundle();
        mStack.add(getFragmentManager().findFragmentById(R.id.activity_settings_frame));

        if (key.equals("settings_display")) {
            bundle.putInt(Constants.BUNDLE_SETTINGS_XML, R.xml.settings_display);
            bundle.putString(Constants.BUNDLE_SETTINGS_TITLE, getString(R.string.settings_header_display));
        } else if (key.equals("settings_wordcloud")) {
            bundle.putInt(Constants.BUNDLE_SETTINGS_XML, R.xml.settings_wordcloud);
            bundle.putString(Constants.BUNDLE_SETTINGS_TITLE, getString(R.string.settings_header_wordcloud));
        } else if (key.equals("settings_behaviour")) {
            bundle.putInt(Constants.BUNDLE_SETTINGS_XML, R.xml.settings_behaviour);
            bundle.putString(Constants.BUNDLE_SETTINGS_TITLE, getString(R.string.settings_header_behaviour));
        } else if (key.equals("settings_feedback")) {
            bundle.putInt(Constants.BUNDLE_SETTINGS_XML, R.xml.settings_feedback);
            bundle.putString(Constants.BUNDLE_SETTINGS_TITLE, getString(R.string.settings_header_feedback));
        }

        fragmentSettings.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_settings_frame, fragmentSettings)
                .commit();
    }

    public static class FragmentSettings extends PreferenceFragment {

        private SharedPreferences mPrefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            //Defaults
            int xmlId = R.xml.settings_headers;
            String title = getString(R.string.settings_settings);

            if (getArguments() != null
                    && getArguments().containsKey(Constants.BUNDLE_SETTINGS_XML)
                    && getArguments().containsKey(Constants.BUNDLE_SETTINGS_TITLE)) {

                xmlId = getArguments().getInt(Constants.BUNDLE_SETTINGS_XML);
                title = getArguments().getString(Constants.BUNDLE_SETTINGS_TITLE);

            }

            addPreferencesFromResource(xmlId);
            setScreenTitle(title);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                view.setBackgroundColor(ContextCompat.getColor(getActivity(), UtilityFunctions.getResIdFromAttribute(R.attr.windowBackground, getActivity())));
            }

            ListView listView = (ListView) view.findViewById(android.R.id.list);
            if (listView != null) {

                //Prevent divider showing beneath list
                listView.setFooterDividersEnabled(false);
                listView.setOverscrollFooter(new ColorDrawable(0x00000000));

                listView.setDivider(null);
//                listView.setDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), UtilityFunctions.getResIdFromAttribute(R.attr.colorDivider, getActivity()))));
//                listView.setDividerHeight(1);
            }

            return view;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    getActivity().onBackPressed();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            super.onPreferenceTreeClick(preferenceScreen, preference);
            if (preference instanceof PreferenceScreen) {
                 ((ActivitySettings) getActivity()).switchToScreen(preference.getKey());
            }
            return false;
        }

        @SuppressWarnings("AndroidLintCommitPrefEdits")
        @Override
        public void onResume() {
            super.onResume();

            final ListPreference theme = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_DISPLAY_THEME);
            if (theme != null) {
                int index = theme.findIndexOfValue(PreferenceFunctions.getPrefThemeValue(getActivity()));
                theme.setSummary(getResources().getStringArray(R.array.settings_display_theme_entries)[index]);
                theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_DISPLAY_THEME, (String) o).commit();
                        System.exit(0);
                        return false;
                    }
                });
            }

            final ListPreference columns = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_DISPLAY_COLUMNS);
            if (columns != null) {
                int columnIndex = columns.findIndexOfValue(PreferenceFunctions.getPrefPinboardColumnsValue(getActivity()));
                columns.setSummary(getResources().getStringArray(R.array.settings_display_columns_entries)[columnIndex]);
                columns.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index =  columns.findIndexOfValue((String) o);
                        columns.setSummary(getResources().getStringArray(R.array.settings_display_columns_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_DISPLAY_COLUMNS, o.toString()).commit();
                        columns.setValueIndex(index);
                        return false;
                    }
                });
            }

            final ListPreference dateFormat = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_DISPLAY_DATE_FORMAT);
            if (dateFormat != null) {
                int columnIndex = dateFormat.findIndexOfValue(PreferenceFunctions.getPrefDateFormatValue(getActivity()));
                dateFormat.setSummary(getResources().getStringArray(R.array.settings_display_date_format_entries)[columnIndex]);
                dateFormat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index = dateFormat.findIndexOfValue((String) o);
                        dateFormat.setSummary(getResources().getStringArray(R.array.settings_display_date_format_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_DISPLAY_DATE_FORMAT, o.toString()).commit();
                        dateFormat.setValueIndex(index);
                        return false;
                    }
                });
            }

            final ListPreference itemLength = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_DISPLAY_TRUNCATE_ITEM);
            if (itemLength != null) {
                int columnIndex = itemLength.findIndexOfValue(PreferenceFunctions.getPrefTruncateItemValue(getActivity()));
                itemLength.setSummary(getResources().getStringArray(R.array.settings_display_truncate_item_entries)[columnIndex]);
                itemLength.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index = itemLength.findIndexOfValue((String) o);
                        itemLength.setSummary(getResources().getStringArray(R.array.settings_display_truncate_item_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_DISPLAY_TRUNCATE_ITEM, o.toString()).commit();
                        itemLength.setValueIndex(index);
                        return false;
                    }
                });
            }

            final ListPreference wordCloudColour = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_WORDCLOUD_COLOUR);
            if (wordCloudColour != null) {
                int columnIndex = wordCloudColour.findIndexOfValue(PreferenceFunctions.getPrefWordCloudColourValue(getActivity()));
                wordCloudColour.setSummary(getResources().getStringArray(R.array.settings_wordcloud_colour_entries)[columnIndex]);
                wordCloudColour.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index = wordCloudColour.findIndexOfValue((String) o);
                        wordCloudColour.setSummary(getResources().getStringArray(R.array.settings_wordcloud_colour_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_WORDCLOUD_COLOUR, o.toString()).commit();
                        wordCloudColour.setValueIndex(index);
                        return false;
                    }
                });
            }

            final ListPreference wordCloudShape = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_WORDCLOUD_SHAPE);
            if (wordCloudShape != null) {
                int columnIndex = wordCloudShape.findIndexOfValue(PreferenceFunctions.getPrefWordCloudShapeValue(getActivity()));
                wordCloudShape.setSummary(getResources().getStringArray(R.array.settings_wordcloud_shape_entries)[columnIndex]);
                wordCloudShape.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index = wordCloudShape.findIndexOfValue((String) o);
                        wordCloudShape.setSummary(getResources().getStringArray(R.array.settings_wordcloud_shape_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_WORDCLOUD_SHAPE, o.toString()).commit();
                        wordCloudShape.setValueIndex(index);
                        return false;
                    }
                });
            }

            final ListPreference wordCloudDensity = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_WORDCLOUD_DENSITY);
            if (wordCloudDensity != null) {
                int columnIndex = wordCloudDensity.findIndexOfValue(PreferenceFunctions.getPrefWordCloudDensityValue(getActivity()));
                wordCloudDensity.setSummary(getResources().getStringArray(R.array.settings_wordcloud_density_entries)[columnIndex]);
                wordCloudDensity.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index = wordCloudDensity.findIndexOfValue((String) o);
                        wordCloudDensity.setSummary(getResources().getStringArray(R.array.settings_wordcloud_density_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_WORDCLOUD_DENSITY, o.toString()).commit();
                        wordCloudDensity.setValueIndex(index);
                        return false;
                    }
                });
            }

            final ListPreference wordCloudNumber = (ListPreference) findPreference(PreferenceFunctions.SETTINGS_WORDCLOUD_NUMBER);
            if (wordCloudNumber != null) {
                int columnIndex = wordCloudNumber.findIndexOfValue(PreferenceFunctions.getPrefWordCloudNumberValue(getActivity()));
                wordCloudNumber.setSummary(getResources().getStringArray(R.array.settings_wordcloud_number_entries)[columnIndex]);
                wordCloudNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        int index = wordCloudNumber.findIndexOfValue((String) o);
                        wordCloudNumber.setSummary(getResources().getStringArray(R.array.settings_wordcloud_number_entries)[index]);
                        mPrefs.edit().putString(PreferenceFunctions.SETTINGS_WORDCLOUD_NUMBER, o.toString()).commit();
                        wordCloudNumber.setValueIndex(index);
                        return false;
                    }
                });
            }
        }

        private void setScreenTitle(String title) {
            if (getActivity().findViewById(R.id.toolbar) != null) {
                ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(title);
            }
        }
    }


}
