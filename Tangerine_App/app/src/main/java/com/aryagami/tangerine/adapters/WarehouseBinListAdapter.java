package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.WarehouseBinLotVo;

public class WarehouseBinListAdapter extends ArrayAdapter {
    WarehouseBinLotVo[] warehouseArray;
    Activity activity = null;


    public WarehouseBinListAdapter(Activity context, WarehouseBinLotVo[] warehouseArray) {
        super(context, R.layout.item_warehouse_stock, warehouseArray);
        this.warehouseArray = warehouseArray;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_warehouse_stock, null, true);
        }
        TextView stockName = (TextView) rowView.findViewById(R.id.stock_name);
        TextView product = (TextView) rowView.findViewById(R.id.product);
        TextView inwardQuantity = (TextView) rowView.findViewById(R.id.inward_quantity);
        TextView outwardQuantity = (TextView) rowView.findViewById(R.id.outward_quantity);
        TextView status = (TextView) rowView.findViewById(R.id.status);

        WarehouseBinLotVo rowItem = (WarehouseBinLotVo) getItem(position);

        if (!rowItem.lotName.toString().isEmpty()) {
            stockName.setText(rowItem.lotName.toString());
        } else {
            stockName.setText("");
        }

        if (!rowItem.lotDesc.toString().isEmpty()) {
            product.setText(rowItem.lotDesc.toString());
        } else {
            product.setText("");
        }

        if (!rowItem.inwardQuantity.toString().isEmpty()) {
            inwardQuantity.setText(rowItem.inwardQuantity.toString());
        } else {
            inwardQuantity.setText("");
        }

        if (!rowItem.outwardQuantity.toString().isEmpty()) {
            outwardQuantity.setText(rowItem.outwardQuantity.toString());
        } else {
            outwardQuantity.setText("");
        }

        if (!rowItem.approvalStatus.toString().isEmpty()) {
            status.setText(rowItem.approvalStatus);
        } else {
            status.setText("");
        }
        return rowView;
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
}
