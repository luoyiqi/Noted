package com.cerebellio.noted.models;

import com.cerebellio.noted.models.WordCloud.CloudShape;

import java.util.Random;

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
        mDesiredX = calculateDesiredX(cloudShape);
        mDesiredY = calculateDesiredY(cloudShape);
    }

    /**
     * The relative x position this {@link Word} wants to take up in a frame
     *
     * @param cloudShape        {@link CloudShape} of the word
     * @return                  x position
     */
    private float calculateDesiredX(CloudShape cloudShape) {
        switch (cloudShape) {
            default:
            case CIRCULAR:
                return 0.5f;
            case VERTICAL:
                return 0.5f;
            case HORIZONTAL:
                return new Random().nextFloat();
        }
    }

    /**
     * The relative y position this {@link Word} wants to take up in a frame
     *
     * @param cloudShape        {@link CloudShape} of the word
     * @return                  y position
     */
    private float calculateDesiredY(CloudShape cloudShape) {
        switch (cloudShape) {
            default:
            case CIRCULAR:
                return 0.5f;
            case VERTICAL:
                return new Random().nextFloat();
            case HORIZONTAL:
                return 0.5f;
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
