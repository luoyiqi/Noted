package com.cerebellio.noted.models;

import android.graphics.PointF;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.models.WordCloud.CloudShape;
import com.cerebellio.noted.utils.StatisticalFunctions;

/**
 * Represents a single word in a word cloud
 */
public class Word {

    private String mWord;
    private int mCount;
    private float mFraction;

    /**
     * Fraction across the screen this word wants to be placed horizontally
     */
    private float mDesiredX;

    /**
     * Fraction across the screen this word wants to be placed vertically
     */
    private float mDesiredY;

    public Word(String word, int count, CloudShape cloudShape) {
        mWord = word;
        mCount = count;
        PointF desiredPoint = calculateDesiredXY(cloudShape);
        mDesiredX = desiredPoint.x;
        mDesiredY = desiredPoint.y;
    }

    /**
     * The relative x and y position this {@link Word} wants to take up in a frame
     *
     * @param cloudShape        {@link CloudShape} of the word
     * @return                  x and y position
     */
    private PointF calculateDesiredXY(CloudShape cloudShape) {
        switch (cloudShape) {
            default:
            case CIRCLE:
                return new PointF(0.5f, 0.5f);
            case VERTICAL:
                return new PointF(0.5f, ApplicationNoted.random.nextFloat());
            case HORIZONTAL:
                return new PointF(ApplicationNoted.random.nextFloat(), 0.5f);
            case CROSS:
                //50/50 chance of being attached to x or y axis
                //If on x axis, give y random value and vice versa
                float crossX = StatisticalFunctions.coinToss() ? 0.5f : ApplicationNoted.random.nextFloat();
                float crossY = crossX == 0.5f ? ApplicationNoted.random.nextFloat() : 0.5f;
                return new PointF(crossX, crossY);
            case DIAMOND:
                //Do the same as making a cross, but for each float take
                //the average of a few so the positions tend to centralise around 0.5
                //Obviously the higher the repeat factor the closer we are to
                //approximating a diamond
                final int REPEAT_FACTOR = 5;
                float diamondX = StatisticalFunctions.coinToss() ? 0.5f : StatisticalFunctions.randomAverageOfXFloats(REPEAT_FACTOR);
                float diamondY = diamondX == 0.5f ? StatisticalFunctions.randomAverageOfXFloats(REPEAT_FACTOR) : 0.5f;
                return new PointF(diamondX, diamondY);
            case PICTURE_FRAME:
                boolean stuckToSide = StatisticalFunctions.coinToss();

                //If stuck to side, either return hard left or right.
                //If not, we're stuck to top or bottom so need random x
                float pictureFrameX = stuckToSide ? (StatisticalFunctions.coinToss() ? 0f : 1f) : ApplicationNoted.random.nextFloat();

                //As above in reverse
                float pictureFrameY = stuckToSide ?  ApplicationNoted.random.nextFloat() : (StatisticalFunctions.coinToss() ? 0f : 1f);

                return new PointF(pictureFrameX, pictureFrameY);
            case WAR_FORMATION:
                //Words line up at top and bottom of the screen
                return new PointF(ApplicationNoted.random.nextFloat(), StatisticalFunctions.coinToss() ? 0f : 1f);
        }
    }

    public String getWord() {
        return mWord;
    }

    public int getCount() {
        return mCount;
    }

    public float getFraction() {
        return mFraction;
    }

    public void setFraction(float fraction) {
        mFraction = fraction;
    }

    public float getDesiredX() {
        return mDesiredX;
    }

    public float getDesiredY() {
        return mDesiredY;
    }
}
