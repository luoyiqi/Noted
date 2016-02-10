package com.cerebellio.noted.models.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;

import java.util.List;

/**
 * Created by Sam on 10/02/2016.
 */
public class ColourSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Integer> mColours;

    public ColourSelectionAdapter(List<Integer> colours) {
        mColours = colours;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Integer colour = mColours.get(position);

        ((ColourSelectionAdapterViewHolder) holder).mTextColour.setBackgroundColor(colour);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColourSelectionAdapterViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_colour_selection, parent, false));
    }

    @Override
    public int getItemCount() {
        return mColours.size();
    }

    private class ColourSelectionAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView mTextColour;

        public ColourSelectionAdapterViewHolder(View v) {
            super(v);
            mTextColour = (TextView) v.findViewById(R.id.recycler_item_colour_selection_colour);

            mTextColour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ApplicationNoted.bus.post(mColours.get(getAdapterPosition()));
                }
            });
        }

    }

}
