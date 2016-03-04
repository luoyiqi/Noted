package com.cerebellio.noted.helpers;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cerebellio.noted.R;
import com.cerebellio.noted.models.Word;
import com.cerebellio.noted.models.WordCloud;
import com.cerebellio.noted.models.WordWithTextView;
import com.cerebellio.noted.utils.ColourFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.cerebellio.noted.views.NoPaddingTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class to create a {@link WordCloud} representation in a given frame
 */
public class WordCloudBuilder {

    private static final String LOG_TAG = TextFunctions.makeLogTag(WordCloudBuilder.class);

    private static final int INITIAL_CIRCLE_RADIUS = 1;

    private static final int DENSE_RADIUS_INCREASE_FACTOR = 1;
    private static final int NORMAL_RADIUS_INCREASE_FACTOR = 20;
    private static final int LOOSE_RADIUS_INCREASE_FACTOR = 50;

    private Context mContext;
    private WordCloud mWordCloud;
    private FrameLayout mFrame;
    private CloudColouringSystem mCloudColouringSystem;
    private CloudDensity mCloudDensity;
    private List<WordWithTextView> mWordsWithTextViews;
    private List<Rect> mDrawnBoundingRectangles = new ArrayList<>();
    private Rect mFrameRect;
    private Random mRandom = new Random();

    private float mMaxTextSize;
    private int mDrawnTextViewCount;
    private int[] mCustomPalette;
    private boolean mIsAnimationNeeded;

    /**
     * The validity of the position we have attempted to place a {@link Word}
     */
    private enum PositionValidity {
        VALID,
        INTERSECT,
        OUTSIDE_BOUNDS
    }

    public enum CloudColouringSystem {
        MATERIAL,
        MONOTONE,
        CUSTOM_PALETTE
    }

    public enum CloudDensity {
        DENSE,
        NORMAL,
        LOOSE;

        public static int getIncreaseFactor(CloudDensity density) {
            switch (density) {
                default:
                case DENSE:
                    return DENSE_RADIUS_INCREASE_FACTOR;
                case NORMAL:
                    return NORMAL_RADIUS_INCREASE_FACTOR;
                case LOOSE:
                    return LOOSE_RADIUS_INCREASE_FACTOR;
            }
        }
    }

    public WordCloudBuilder(Context context, WordCloud wordCloud,
                            FrameLayout frame, CloudColouringSystem cloudColouringSystem,
                            CloudDensity density, boolean isAnimationNeeded, int[] customPalette) {
        mCustomPalette = customPalette;
        init(context, wordCloud, frame, cloudColouringSystem, density, isAnimationNeeded);
    }

    private void init(Context context, WordCloud wordCloud, FrameLayout frame,
                      CloudColouringSystem cloudColouringSystem, CloudDensity density, boolean isAnimationNeeded) {
        mContext = context;
        mWordCloud = wordCloud;
        mFrame = frame;
        mCloudColouringSystem = cloudColouringSystem;
        mCloudDensity = density;
        mIsAnimationNeeded = isAnimationNeeded;

        //The lower the number of words we're displaying,
        //the greater the size we can afford to make the text
        if (mWordCloud.getWords().size() <= 10) {
            mMaxTextSize = mContext.getResources().getDimension(R.dimen.text_wordcloud_max_10_items);
        } else if (mWordCloud.getWords().size() <= 50) {
            mMaxTextSize = mContext.getResources().getDimension(R.dimen.text_wordcloud_max_50_items);
        } else if (mWordCloud.getWords().size() <= 100) {
            mMaxTextSize = mContext.getResources().getDimension(R.dimen.text_wordcloud_max_100_items);
        } else if (mWordCloud.getWords().size() <= 500) {
            mMaxTextSize = mContext.getResources().getDimension(R.dimen.text_wordcloud_max_500_items);
        } else {
            mMaxTextSize = mContext.getResources().getDimension(R.dimen.text_wordcloud_max_1000_items);
        }

        //Rectangle designating the bounds of the container frame
        mFrameRect = new Rect(
                (int) mFrame.getX(), (int) mFrame.getY(), mFrame.getMeasuredWidth(), mFrame.getMeasuredHeight());
        mWordsWithTextViews = buildWordTextViewPairs();
    }

    /**
     * Create a List of {@link WordWithTextView} for each {@link Word} in the {@link WordCloud}
     *
     * @return          List of created {@link WordWithTextView}
     */
    private List<WordWithTextView> buildWordTextViewPairs() {
        List<WordWithTextView> wordWithTextViews = new ArrayList<>();

        for (final Word word : mWordCloud.getWords()) {
            final WordWithTextView wordWithTextView = new WordWithTextView(word, buildTextView(word));
            wordWithTextViews.add(wordWithTextView);

            wordWithTextView.getTextView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    wordWithTextView.getTextView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    //When the TextView has been set out, increment the total count
                    incrementDrawnTextViewCount();
                }
            });

        }

        return wordWithTextViews;
    }

    /**
     * Create a TextView with standard parameters
     *
     * @param word          {@link Word} this TextView will display
     * @return              built TextView
     */
    private TextView buildTextView(Word word) {

        //Words greater than this size will be assigned
        //a smaller textsize proportional to the amount over it they are
        final int MAX_LENGTH_AT_FULL_SIZE = 4;

        final NoPaddingTextView textView = new NoPaddingTextView(mContext);

        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (word.getWord().length() <= MAX_LENGTH_AT_FULL_SIZE) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, word.getFraction() * mMaxTextSize);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, word.getFraction() * mMaxTextSize *
                    (MAX_LENGTH_AT_FULL_SIZE / (float) word.getWord().length()));
        }

        textView.setTextColor(getTextColour());
        textView.setText(word.getWord());
        textView.setVisibility(View.INVISIBLE);

        mFrame.addView(textView);

        return textView;
    }

    /**
     * Retrieve the correct colour depending on {@link #mCloudColouringSystem}
     *
     * @return          int representation of colour
     */
    private int getTextColour() {
        switch (mCloudColouringSystem) {
            default:
            case MATERIAL:
                return ColourFunctions.getRandomMaterialColour();
            case MONOTONE:
                return ContextCompat.getColor(mContext,
                        UtilityFunctions.getResIdFromAttribute(R.attr.textColorTertiary, mContext));
            case CUSTOM_PALETTE:
                if (mCustomPalette == null) {
                    return ColourFunctions.getRandomMaterialColour();
                } else  {
                    return mCustomPalette[mRandom.nextInt(mCustomPalette.length)];
                }
        }
    }

    /**
     * Increment count of TextViews that have been laid out
     */
    private void incrementDrawnTextViewCount() {
        mDrawnTextViewCount++;
        if (areAllTextViewsDrawn()) {
            positionTextViewsInFrame();
        }
    }

    /**
     *
     * @return          true if every TextView is drawn
     */
    private boolean areAllTextViewsDrawn() {
        return mDrawnTextViewCount == mWordCloud.getWords().size();
    }

    /**
     * Calculate position, taking into account all other drawn TextViews
     */
    private void positionTextViewsInFrame() {
        for (WordWithTextView wordWithTextView : mWordsWithTextViews) {
            new WordCloudPositionCalculatorAsync(wordWithTextView).execute();
        }
    }

    /**
     * Calculate the coordinates of a point on the circumference of a circle
     *
     * @param originalX             centre x
     * @param originalY             centre y
     * @param angle                 angle from centre to edge point
     * @param radius                radius of circle
     * @return                      coordinates of calculated point
     */
    private Point getPointOnCircle(float originalX, float originalY, float angle, float radius) {
        int newX = (int) (originalX + Math.cos(angle) * radius);
        int newY = (int) (originalY + Math.sin(angle) * radius);

        return new Point(newX, newY);
    }

    /**
     * Check the given Rect is in a valid position
     *
     * @param rect              Rect to check
     * @return                  {@link com.cerebellio.noted.helpers.WordCloudBuilder.PositionValidity}
     */
    private PositionValidity isValidPosition(Rect rect) {

        //Off the screen
        if (!mFrameRect.contains(rect)) {
            return PositionValidity.OUTSIDE_BOUNDS;
        }

        //No other rectangles placed
        if (mDrawnBoundingRectangles.isEmpty()) {
            return PositionValidity.VALID;
        }

        //If the given rectangle intersects any other rectangle,
        //it is not valid
        for (Rect drawnRect : mDrawnBoundingRectangles) {
            if (drawnRect.intersects(rect.left, rect.top, rect.right, rect.bottom)) {
                return PositionValidity.INTERSECT;
            }
        }

        //Passed all checks
        return PositionValidity.VALID;
    }

    /**
     * AsyncTask to calculate position for each {@link Word} on the screen.
     *
     * Needed because this can be a slow operation as the number of {@link Word} increases
     * as we need to check the current bounds against the bounds of all {@link Word} that
     * have already been placed.
     */
    private class WordCloudPositionCalculatorAsync extends AsyncTask<Void, Void, Rect> {

        private int mTextWidth;
        private int mTextHeight;
        private float mFrameWidth;
        private float mFrameHeight;

        private TextView mTextView;
        private WordWithTextView mWordWithTextView;

        public WordCloudPositionCalculatorAsync(WordWithTextView wordWithTextView) {
            mTextWidth = wordWithTextView.getTextView().getMeasuredWidth();
            mTextHeight = wordWithTextView.getTextView().getMeasuredHeight();
            mFrameWidth = mFrame.getMeasuredWidth();
            mFrameHeight = mFrame.getMeasuredHeight();
            mTextView = wordWithTextView.getTextView();
            mWordWithTextView = wordWithTextView;
        }

        @Override
        protected Rect doInBackground(Void... voids) {
            Word word = mWordWithTextView.getWord();

            //X,Y position this word wants to be centred at
            int absoluteDesiredX = (int) (word.getDesiredX() * mFrameWidth);
            int absoluteDesiredY = (int) (word.getDesiredY() * mFrameHeight);

            //Used to calculate edges of bounding box
            int halfWidth = mTextWidth / 2;
            int halfHeight = mTextHeight / 2;

            //Edge coordinates of bounding box
            int left = absoluteDesiredX - halfWidth;
            int top = absoluteDesiredY - halfHeight;
            int right = absoluteDesiredX + halfWidth;
            int bottom = absoluteDesiredY + halfHeight;

            //If rectangle is offscreen, move it back onto screen
            if (left < mFrameRect.left) {
                int translationNeeded = Math.abs(left - mFrameRect.left);
                left += translationNeeded;
                right += translationNeeded;
            }
            if (top < mFrameRect.top) {
                int translationNeeded = Math.abs(top - mFrameRect.top);
                top += translationNeeded;
                bottom += translationNeeded;
            }
            if (right > mFrameRect.right) {
                int translationNeeded = Math.abs(right - mFrameRect.right);
                left -= translationNeeded;
                right -= translationNeeded;
            }
            if (bottom > mFrameRect.bottom) {
                int translationNeeded = Math.abs(bottom - mFrameRect.bottom);
                top -= translationNeeded;
                bottom -= translationNeeded;
            }

            float radius = INITIAL_CIRCLE_RADIUS;

            Rect rect = new Rect(left, top, right, bottom);

            PositionValidity validity = isValidPosition(rect);

            while (!validity.equals(PositionValidity.VALID)) {

                if (validity.equals(PositionValidity.OUTSIDE_BOUNDS)) {
                    //Set back to initial values because we know these were on the screen
                    rect.set(left, top, right, bottom);
                } else {
                    //Move out in a gradually increase circle until
                    //we hit upon a valid position
                    Point nextPoint = getPointOnCircle(left, top, mRandom.nextInt(361), radius);
                    int nextLeft = nextPoint.x;
                    int nextTop = nextPoint.y;

                    //Point is centre, so need to calculate edges by
                    //taking away height/width
                    rect.set(nextLeft - halfWidth,
                            nextTop - halfHeight,
                            nextLeft + halfWidth,
                            nextTop + halfHeight);

                    //Greater this is, the less dense the cloud appears
                    radius += CloudDensity.getIncreaseFactor(mCloudDensity);
                }

                validity = isValidPosition(rect);
            }

            mDrawnBoundingRectangles.add(rect);

            return rect;
        }

        @Override
        protected void onPostExecute(Rect rect) {
            super.onPostExecute(rect);

            //Position TextView in rectangle and make visible
            mTextView.setLeft(rect.left);
            mTextView.setTop(rect.top);
            mTextView.setVisibility(View.VISIBLE);

            //If the user has chosen to apply animations
            //animate in from a random direction
            if (mIsAnimationNeeded) {
                int random = mRandom.nextInt(4);
                switch (random) {
                    default:
                    case 0:
                        mTextView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left));
                        break;
                    case 1:
                        mTextView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right));
                        break;
                    case 2:
                        mTextView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_top));
                        break;
                    case 3:
                        mTextView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));
                        break;
                }
            }
        }
    }

}
