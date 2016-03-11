package com.cerebellio.noted.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cerebellio.noted.R;
import com.cerebellio.noted.utils.ColourFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

/**
 * Custom List Preference with theming capabilities
 */
public class NotedListPreference extends ListPreference implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = TextFunctions.makeLogTag(NotedListPreference.class);

    private int mClickedDialogEntryIndex;

    private CharSequence mDialogTitle;

    public NotedListPreference(Context context) {
        super(context);
    }

    public NotedListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        View view = View.inflate(getContext(), R.layout.dialog_list_preference, null);

        mDialogTitle = getDialogTitle();
        if(mDialogTitle == null) mDialogTitle = getTitle();
//        ((TextView) view.findViewById(R.id.dialog_title)).setText(mDialogTitle);

        ListView list = (ListView) view.findViewById(R.id.dialog_list_preference_list);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                getContext(), R.layout.dialog_list_item,
                getEntries());

        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setItemChecked(findIndexOfValue(getValue()), true);
        list.setOnItemClickListener(this);

        view.findViewById(R.id.dialog_list_preference_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        //Adjust summary text colour to 54% opacity
        int textColor = ContextCompat.getColor(getContext(),
                UtilityFunctions.getResIdFromAttribute(R.attr.textColorTertiary, getContext()));
        ((TextView) view.findViewById(android.R.id.title)).setTextColor(textColor);
        ((TextView) view.findViewById(android.R.id.summary))
                .setTextColor(ColourFunctions.adjustAlpha(textColor, ColourFunctions.MATERIAL_ALPHA_54_PER_CENT));
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        if (getEntries() == null || getEntryValues() == null) {
            //Throws exception
            super.onPrepareDialogBuilder(builder);
            return;
        }

        mClickedDialogEntryIndex = findIndexOfValue(getValue());

        //.setTitle(null) to prevent default (blue)
        //title + divider from showing up
        builder.setTitle(null);
        builder.setNegativeButton(null, null);
        builder.setPositiveButton(null, null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && mClickedDialogEntryIndex >= 0
                && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogEntryIndex]
                    .toString();

            //Set the new value
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mClickedDialogEntryIndex = position;

        //Pretend user has clicked positive button
        NotedListPreference.this.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
        getDialog().dismiss();
    }
}
