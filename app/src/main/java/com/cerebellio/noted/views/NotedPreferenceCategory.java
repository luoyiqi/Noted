package com.cerebellio.noted.views;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cerebellio.noted.R;
import com.cerebellio.noted.utils.UtilityFunctions;

/**
 * Extended {@link PreferenceCategory} to change text colour/size
 */
public class NotedPreferenceCategory extends PreferenceCategory {

    public NotedPreferenceCategory(Context context) {
        super(context);
    }
    public NotedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NotedPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        TextView categoryTitle =  (TextView) super.onCreateView(parent);
        categoryTitle.setTextColor(ContextCompat.getColor(getContext(),
                UtilityFunctions.getResIdFromAttribute(R.attr.colorAccent, getContext())));
        categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.text_small));
        return categoryTitle;
    }
}