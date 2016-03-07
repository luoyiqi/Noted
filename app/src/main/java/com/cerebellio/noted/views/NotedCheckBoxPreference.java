package com.cerebellio.noted.views;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sam on 06/03/2016.
 */
public class NotedCheckBoxPreference extends CheckBoxPreference {

    public NotedCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        makeMultiline(view);
    }

    protected void makeMultiline( View view)
    {
        if ( view instanceof ViewGroup){

            ViewGroup grp=(ViewGroup)view;

            for ( int index = 0; index < grp.getChildCount(); index++)
            {
                makeMultiline(grp.getChildAt(index));
            }
        } else if (view instanceof TextView){
            TextView textView = (TextView)view;
            textView.setSingleLine(false);
            textView.setEllipsize(null);
        }
    }
}
