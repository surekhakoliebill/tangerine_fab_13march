package com.aryagami.tangerine.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.PlanGroup;

public class BundlePlansListingAdapter extends ArrayAdapter {


    PlanGroup[] planGroupsList;
    private Activity activity = null;

    public BundlePlansListingAdapter(Context activity, PlanGroup[] list1) {
        super(activity, R.layout.item_list_for_bundle_plans,list1);
        this.planGroupsList = list1;
        this.activity = (Activity) activity;
        //this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final PlanGroup item = (PlanGroup) getItem(position);
        final View rowView;

        if (convertView != null){
            rowView = convertView;
        }else {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView  = inflater.inflate(R.layout.item_list_for_bundle_plans, null, true);
        }

        Button selectBtn = (Button)rowView.findViewById(R.id.select_plan);
        PlanGroup planGroup = (PlanGroup) getItem(position);

        TextView bundleName = (TextView)rowView.findViewById(R.id.bundle_name);
        if (planGroup.groupName != null){
            bundleName.setText(planGroup.groupName);
        }else {
            bundleName.setText("");
        }

        TextView description = (TextView)rowView.findViewById(R.id.description_value);
        if (planGroup.planDescription != null){
            description.setText(planGroup.planDescription);
        }else {
            description.setText("");
        }

        TextView type = (TextView)rowView.findViewById(R.id.type_value);
        if (planGroup.groupType != null){
            type.setText(planGroup.groupType);
        }else {
            type.setText("");
        }

        TextView price = (TextView)rowView.findViewById(R.id.price_value);
        if (planGroup.planPrice != null){
            price.setText(planGroup.planPrice.toString());
        }else {
            price.setText("");
        }

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return rowView;
    }
}
