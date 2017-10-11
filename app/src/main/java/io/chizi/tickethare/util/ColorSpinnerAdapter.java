package io.chizi.tickethare.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.chizi.tickethare.R;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class ColorSpinnerAdapter extends ArrayAdapter<String> {
    private int[] backgroundColors;
    private int[] textColors;

    public ColorSpinnerAdapter(Context context, String[] items, int[] theTextColors, int[] theBackgroundColors) {
        super(context, android.R.layout.simple_spinner_item, items);
        textColors = theTextColors;
        backgroundColors = theBackgroundColors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(backgroundColors[position]);
        ((TextView) view).setTextColor(textColors[position]);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        applyColor(view, position);
        return view;
    }

    private void applyColor(View view, int position) {
        view.setBackgroundColor(backgroundColors[position]);
        TextView text = (TextView) view.findViewById(R.id.color_spinner_item);
        text.setTextColor(textColors[position]);
    }
}