package com.cerebellio.noted.utils;

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

}

