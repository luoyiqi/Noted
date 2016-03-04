package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a word cloud
 */
public class WordCloud {

    private static final String LOG_TAG = TextFunctions.makeLogTag(WordCloud.class);

    private List<Word> mWords = new ArrayList<>();

    //Of words to show in cloud
    private int mMaxNumber;

    /**
     * Determines how the word cloud should look
     */
    public enum CloudShape {
        CIRCULAR,
        HORIZONTAL,
        VERTICAL
    }

    public WordCloud(String text, String[] commonWords, CloudShape cloudShape, int maxNumber) {
        //Convert the text to list and then count each instance of a word
        HashMap<String, Integer> counts =
                getWordCounts(getWordsList(text, commonWords));

        mMaxNumber = maxNumber;
        addWords(cloudShape, counts);
        sortWords();
        truncateWordListToSize();
        applyWordFractions();
    }

    public WordCloud(String text, CloudShape cloudShape, int maxNumber) {
        //Convert the text to list and then count each instance of a word
        HashMap<String, Integer> counts =
                getWordCounts(getWordsList(text));

        mMaxNumber = maxNumber;
        addWords(cloudShape, counts);
        sortWords();
        truncateWordListToSize();
        applyWordFractions();
    }

    /**
     * Add {@link Word} to member List
     *
     * @param cloudShape        {@link com.cerebellio.noted.models.WordCloud.CloudShape} of word
     * @param counts            Map of words to their appearance numbers
     */
    private void addWords(CloudShape cloudShape, HashMap<String, Integer> counts) {
        //Add a new Word object for each string and pass in the count
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            mWords.add(new Word(entry.getKey(), entry.getValue(), cloudShape));
        }
    }

    /**
     * Sort {@link Word} in descending order by count
     */
    private void sortWords() {
        //Sort the Words by count in descending order
        Collections.sort(mWords, new Comparator<Word>() {
            @Override
            public int compare(Word word, Word word2) {
                return word2.getCount() - word.getCount();
            }
        });
    }

    /**
     * Cuts the list down to the defined size
     */
    private void truncateWordListToSize() {
        //Truncate list to the smallest of either maximum size or size of list
        mWords = mWords.subList(0, Math.min(mMaxNumber, mWords.size()));
    }

    /**
     * Notify each {@link Word} of its proportion to the rest of the words by count
     */
    private void applyWordFractions() {
        //Set each Word fraction proportional to the highest count
        float highestCount = mWords.get(0).getCount();

        for (int i = 0; i < mWords.size(); i++) {
            Word word = mWords.get(i);
            word.setFraction((float) word.getCount() / highestCount);
        }
    }

    /**
     * Splits a given passage of text into separate words, ignoring common words
     *
     * @param text              passage to operate on
     * @param commonWords       words to ignore
     * @return                  List of words used in the passage
     */
    private List<String> getWordsList(String text, String[] commonWords) {
        //Get array of words with special characters removed
        String[] wordArray =
                TextFunctions.splitStringToWords(TextFunctions.stripSpecialCharacters(text, true));
        List<String> words = new ArrayList<>();

        //Check that each word is not on the ignore list
        for (String word : wordArray) {
            if (!TextFunctions.arrayContains(commonWords, word)) {
                words.add(word);
            }
        }

        return words;
    }

    /**
     * Splits a given passage of text into separate words, ignoring common words
     *
     * @param text              passage to operate on
     * @return                  List of words used in the passage
     */
    private List<String> getWordsList(String text) {
        //Get array of words with special characters removed
        String[] wordArray =
                TextFunctions.splitStringToWords(TextFunctions.stripSpecialCharacters(text, true));
        return Arrays.asList(wordArray);
    }

    /**
     * Count the number of times word appears with a given list of words
     *
     * @param words             words to look through
     * @return                  a Map of words to their counts
     */
    private HashMap<String, Integer> getWordCounts(List<String> words) {
        HashMap<String, Integer> counts = new HashMap<>();

        for (String word : words) {
            counts.put(word, counts.containsKey(word) ? counts.get(word) + 1 : 1);
        }

        return counts;
    }

    public List<Word> getWords() {
        return mWords;
    }
}


