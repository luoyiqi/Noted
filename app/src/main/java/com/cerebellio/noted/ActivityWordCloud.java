package com.cerebellio.noted;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.helpers.WordCloudBuilder;
import com.cerebellio.noted.helpers.WordCloudBuilder.CloudColouringSystem;
import com.cerebellio.noted.helpers.WordCloudBuilder.CloudDensity;
import com.cerebellio.noted.models.WordCloud;
import com.cerebellio.noted.utils.ColourFunctions;
import com.cerebellio.noted.utils.PreferenceFunctions;
import com.cerebellio.noted.utils.TextFunctions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows the creation and display of a {@link WordCloud} of a given text
 */
public class ActivityWordCloud extends ActivityBase {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.activity_wordcloud_frame)
    FrameLayout mFrameLayout;

    private static final String LOG_TAG = TextFunctions.makeLogTag(ActivityWordCloud.class);

    private WordCloud mWordCloud;
    private int[] mCustomPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordcloud);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setToolbarTitle(mToolbar, getString(R.string.title_nav_drawer_wordcloud));

//        final String text = FileFunctions.readTextFileFromAssets(this, "dummy_german.txt");
        mCustomPalette = new int[] {
                ColourFunctions.getColourFromAttr(this, R.attr.colorPrimary),
                ColourFunctions.getColourFromAttr(this, R.attr.colorAccent),
                ColourFunctions.getColourFromAttr(this, R.attr.textColorTertiary),
                ColourFunctions.getColourFromAttr(this, R.attr.colorCardBackground),
        };

        if (PreferenceFunctions.getPrefWordCloudIncludeCommonWords(this)) {
            new WordCloudCreatorAsync(this, getCloudShapeFromPrefs()).execute();
        } else {
            new WordCloudCreatorAsync(this, getIgnoredWords(), getCloudShapeFromPrefs()).execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private CloudColouringSystem getColouringSystemFromPrefs() {
        return PreferenceFunctions.getPrefWordCloudColour(this);
    }

    private CloudDensity getCloudDensityFromPrefs() {
        return PreferenceFunctions.getPrefWordCloudDensity(this);
    }

    private boolean getCloudAnimationFromPrefs() {
        return PreferenceFunctions.getPrefWordCloudAnimation(this);
    }

    private WordCloud.CloudShape getCloudShapeFromPrefs() {
        return PreferenceFunctions.getPrefWordCloudShape(this);
    }

    private int getWordCloudNumber() {
        return PreferenceFunctions.getPrefWordCloudNumber(this);
    }

    private String[] getIgnoredWords() {
        return getResources().getStringArray(R.array.words_to_ignore_cloud);
    }

    private class WordCloudCreatorAsync extends AsyncTask<Void, Void, WordCloud> {

        private String[] mIgnored;
        private WordCloud.CloudShape mCloudShape;
        private Context mContext;

        public WordCloudCreatorAsync(Context context, String[] ignored, WordCloud.CloudShape cloudShape) {
            mIgnored = ignored;
            mCloudShape = cloudShape;
            mContext = context;
        }

        public WordCloudCreatorAsync(Context context, WordCloud.CloudShape cloudShape) {
            mCloudShape = cloudShape;
            mContext = context;
        }

        @Override
        protected WordCloud doInBackground(Void... voids) {
            SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(mContext);
            String text = sqlDatabaseHelper.getAllWords();
            sqlDatabaseHelper.closeDB();

            return mIgnored == null ? new WordCloud(text, mCloudShape, getWordCloudNumber())
                    : new WordCloud(text, mIgnored, mCloudShape, getWordCloudNumber());
        }

        @Override
        protected void onPostExecute(WordCloud wordCloud) {
            super.onPostExecute(wordCloud);

            mWordCloud = wordCloud;
            new WordCloudBuilder(mContext, mWordCloud,
                    mFrameLayout, getColouringSystemFromPrefs(),
                    getCloudDensityFromPrefs(), getCloudAnimationFromPrefs(), mCustomPalette);
        }
    }
}