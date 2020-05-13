package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryagami.R;

public class PlaceNewOrderTasksAdapter extends ArrayAdapter {
    private String[] countryNames;
    private Integer[] imageid;
    private Activity context;

    public PlaceNewOrderTasksAdapter(Activity context, String[] countryNames, Integer[] imageid) {
        super(context, R.layout.row_item, countryNames);
        this.context = context;
        this.countryNames = countryNames;
        this.imageid = imageid;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.row_item, null, true);
        TextView textViewCountry = (TextView) row.findViewById(R.id.textViewCountry);
        ImageView imageFlag = (ImageView) row.findViewById(R.id.imageViewFlag);

        textViewCountry.setText(countryNames[position]);
        imageFlag.setImageResource(imageid[position]);
        return  row;
    }
}