package com.cerebellio.noted;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.widget.TextView;

import com.cerebellio.noted.utils.ColourFunctions;

/**
 * Fragment base
 */
public abstract class FragmentBase extends Fragment {

    protected void customiseSnackbar(Context context, Snackbar snackbar) {
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textview = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textview.setTextColor(ColourFunctions.getPrimaryTextColour(context));
        textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_large));
    }

}
