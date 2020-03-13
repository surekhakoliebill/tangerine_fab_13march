package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddedDevicesAdapter extends ArrayAdapter {
    Activity activity = null;
    List<NewOrderCommand.ProductListing> proListingArray;
    ProgressDialog progressDialog;
    LinearLayout linearlayout1;
    ImageView deleteButton;
    List<NewOrderCommand.ProductListing> proList = new ArrayList<NewOrderCommand.ProductListing>();
    Map<String, NewOrderCommand.ProductListing> originalValues = new HashMap<>();

    public AddedDevicesAdapter(Activity context, List<NewOrderCommand.ProductListing> resellerStaffs) {
        super(context, R.layout.item_list_for_added_device,resellerStaffs);
        this.proListingArray = resellerStaffs;
        this.activity = context;
        originalValues = RegistrationData.getMapList();
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View rowView;

        if (convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_for_added_device, null, true);
        }
        final NewOrderCommand.ProductListing rowItem = (NewOrderCommand.ProductListing) getItem(position);

        deleteButton = (ImageView) rowView.findViewById(R.id.delete_button);

        TextView serialNumber = (TextView)rowView.findViewById(R.id.serial_num);
        TextView productPrice = (TextView)rowView.findViewById(R.id.product_price);
        TextView productId = (TextView)rowView.findViewById(R.id.product_id);

        if(rowItem.serialNumber != null ){
            serialNumber.setText(rowItem.serialNumber.toString());
        }else{
            serialNumber.setText("");
        }
        if(rowItem.listingPrice != null ){
            productPrice.setText(rowItem.listingPrice.toString());
        }else{
            productPrice.setText("");
        }
        if (rowItem.listingId != null){
            productId.setText(rowItem.listingId.toString());
        }else{
            productId.setText("");
        }

        deleteButton.setTag(position);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proListingArray.remove(position);
                originalValues.remove(rowItem.imei);
                RegistrationData.setMapList(originalValues);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
}


