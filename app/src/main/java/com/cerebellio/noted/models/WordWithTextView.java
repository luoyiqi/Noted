package com.cerebellio.noted.models;

import android.widget.TextView;

/**
 * Simple data structure to keep a {@link Word} linked with a {@link TextView}
 */
public class WordWithTextView {

    private Word mWord;
    private TextView mTextView;

    public WordWithTextView(Word word, TextView textView) {
        mWord = word;
        mTextView = textView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public Word getWord() {
        return mWord;
    }

}
