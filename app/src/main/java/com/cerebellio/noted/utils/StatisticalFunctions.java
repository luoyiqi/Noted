package com.cerebellio.noted.utils;

import com.cerebellio.noted.ApplicationNoted;

/**
 * Helper for common statistical functions, mean, variance, standard deviation etc.
 */
public class StatisticalFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(StatisticalFunctions.class);

    private StatisticalFunctions(){}

    public static float getMean(float[] data) {
        float total = 0f;

        for (float current : data) {
            total += current;
        }

        return total / data.length;
    }

    public static float getVariance(float[] data) {
        float mean = getMean(data);
        float total = 0f;

        for (float current : data) {
            total += Math.pow(mean - current, 2f);
        }

        return total / data.length;
    }

    /**
     * Retrieve the standard deviation of the contained data
     *
     * @return          standard deviation
     */
    public static float getStandardDeviation(float[] data) {
        return (float) Math.sqrt(getVariance(data));
    }

    public static float[] logTransform(float[] data) {
        float[] transformed = new float[data.length];

        for (int i = 0; i < data.length; i++) {
            transformed[i] = (float) Math.log(data[i]);
        }

        return transformed;
    }

    public static float randomAverageOfXFloats(int x) {
        float total = 0f;

        for (int i = 0; i < x; i++) {
            total += ApplicationNoted.random.nextFloat();
        }

        return total / (float) x;
    }

    public static boolean coinToss() {
        return ApplicationNoted.random.nextInt(2) == 1;
    }

    /**
     * Get a random integer in a given inclusive range
     *
     * @param minInclusive      min random value
     * @param maxInclusive      max random value
     * @return                  random value created
     */
    public static int getRandomIntegerInInclusiveRange(int minInclusive, int maxInclusive) {
        return ApplicationNoted.random.nextInt((maxInclusive - minInclusive) + minInclusive);
    }

    public static float getRandomFloatInInclusiveRange(float minInclusive, float maxInclusive) {
        return ApplicationNoted.random.nextFloat() * (maxInclusive - minInclusive) + minInclusive;
    }

}

