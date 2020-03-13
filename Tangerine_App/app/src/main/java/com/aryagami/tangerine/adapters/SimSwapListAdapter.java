package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.SimSwapList;
import com.aryagami.tangerine.activities.SimSwapPdfUploadActivity;
import com.aryagami.tangerine.activities.ValidateSimSwapActivity;

public class SimSwapListAdapter extends ArrayAdapter {
    SimSwapList[] simSwapLists;
    public Activity activity = null;
    Spinner citySpinner;
    String[] cityList = {"Validate"};
    String[] revalidate = {"Revalidate"};
    String[] uploadList = {"Upload Documents"};
    LinearLayout validatelinearLayout, uploaddocumentLayout;
    LinearLayout actionsSpinnerContainer;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    public SimSwapListAdapter(Activity context, SimSwapList[] simSwapLists) {
        super(context, R.layout.item_sim_swap_list, simSwapLists);
        this.simSwapLists = simSwapLists;
        this.activity = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_sim_swap_list, null, true);
        }
        //validatelinearLayout = (LinearLayout) rowView.findViewById(R.id.Sim_Replace_Form);
        validatelinearLayout = (LinearLayout) rowView.findViewById(R.id.new1_sim_swap_list);
        uploaddocumentLayout = (LinearLayout) rowView.findViewById(R.id.new1_sim_swap_list);

        citySpinner = (Spinner) rowView.findViewById(R.id.sim_swap_spinner);

        final TextView msisdn = (TextView) rowView.findViewById(R.id.sim_swap_list_msisdn);
        TextView oldimsi = (TextView) rowView.findViewById(R.id.sim_swap_list_old_imsi);
        TextView newimsi = (TextView) rowView.findViewById(R.id.sim_swap_list_new_imsi);
        TextView reason = (TextView) rowView.findViewById(R.id.sim_swap_list_reason);
        TextView requesteddate = (TextView) rowView.findViewById(R.id.sim_swap_list_requested_date);
        TextView state = (TextView) rowView.findViewById(R.id.sim_swap_list_state);
        TextView status = (TextView) rowView.findViewById(R.id.sim_swap_list_status);
        TextView simSwapLogId = (TextView) rowView.findViewById(R.id.sim_swap_list_simSwapLogId);

        actionsSpinnerContainer = (LinearLayout)rowView.findViewById(R.id.sim_swap_container);

        final SimSwapList rowItem = (SimSwapList) getItem(position);

        if (rowItem != null) {

            if(rowItem.status != null){
                status.setText(rowItem.status.toString());
            }else{
                status.setText("");
            }

            if(rowItem.simSwapLogId != null){
                simSwapLogId.setText(rowItem.simSwapLogId.toString());
            }else{
                simSwapLogId.setText("");
            }

            if(rowItem.msisdn != null){
                msisdn.setText(rowItem.msisdn.toString());
            }else{
                msisdn.setText("");
            }


            if(rowItem.oldImsi != null){
                oldimsi.setText(rowItem.oldImsi.toString());
            }else{
                oldimsi.setText("");
            }

            if(rowItem.newImsi != null){
                newimsi.setText(rowItem.newImsi.toString());
            }else{
                newimsi.setText("");
            }


            if(rowItem.reason != null){
                reason.setText(rowItem.reason.toString());
            }else{
                reason.setText("");
            }

            if(rowItem.swapDate != null){
                requesteddate.setText(rowItem.swapDate.toString());
            }else{
                requesteddate.setText("");
            }


            if(rowItem.state != null){

                if(rowItem.state.equals("Sim Swap Requested")){
                    actionsSpinnerContainer.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, cityList);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    citySpinner.setAdapter(adapter);

                }else if (rowItem.state.equals("OwnerShip Verification Failed")){
                    actionsSpinnerContainer.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, revalidate);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    citySpinner.setAdapter(adapter);

                } else if(rowItem.state.equals("OwnerShip Verified")){
                    actionsSpinnerContainer.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, uploadList);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    citySpinner.setAdapter(adapter);

                }else if (rowItem.state.equals(isEmpty())){
                    actionsSpinnerContainer.setVisibility(View.GONE);

                }else{
                    actionsSpinnerContainer.setVisibility(View.GONE);
                }

                state.setText(rowItem.state.toString());
            }else{
                state.setText("");
            }
        }

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                final String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Validate") || selectedItem.equals("Revalidate")) {
                    Intent intent = new Intent(activity, ValidateSimSwapActivity.class);
                    intent.putExtra("msisdn",rowItem.msisdn);
                    activity.startActivity(intent);
                }else if (selectedItem.equals("Upload Documents")){
                    Intent intent = new Intent(activity, SimSwapPdfUploadActivity.class);
                    intent.putExtra("msisdn",rowItem.msisdn);
                    intent.putExtra("userId",rowItem.userId);
                    intent.putExtra("simSwapLogId", rowItem.simSwapLogId);
                    activity.startActivity(intent);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        return rowView;
        }
}
