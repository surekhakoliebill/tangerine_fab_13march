package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.Product;
import com.aryagami.data.SimReplacementForm;
import com.aryagami.data.SimSwapList;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.NavigationMainActivity;
import com.aryagami.tangerine.activities.NewSimSwapActivity;
import com.aryagami.tangerine.activities.SimSwapPdfUploadActivity;
import com.aryagami.tangerine.activities.StaffByResellerActivity;
import com.aryagami.tangerine.activities.ValidateSimSwapActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class SimSwapListAdapter extends ArrayAdapter {
    SimSwapList[] simSwapLists;
    public Activity activity = null;
    Spinner citySpinner;
    String[] cityList = {"Action", "Validate"};
    String[] revalidate = {"Action", "Revalidate"};
    String[] uploadList = {"Action", "Upload Documents"};
    String[] addICCID = {"Action", "Provide New ICCID"};
    LinearLayout validatelinearLayout, uploaddocumentLayout;
    LinearLayout actionsSpinnerContainer;
    ProgressDialog progressDialog;
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

                }else if(rowItem.state.equals("Documents Approved")){

                    actionsSpinnerContainer.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, addICCID);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    citySpinner.setAdapter(adapter);

                } else if (rowItem.state.equals(isEmpty())){
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
                }else if(selectedItem.equals("Provide New ICCID")){
                    displayPopupWindow(rowItem.userId, rowItem.msisdn);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        return rowView;
    }

    private void displayPopupWindow(String userId, String msisdn) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popupwindow_add_iccid,null);

        Button cancel = (Button) customView.findViewById(R.id.cancel_btn);
        Button submit = (Button) customView.findViewById(R.id.submit_btn);
        final TextInputEditText iccidText = (TextInputEditText) customView.findViewById(R.id.iccid_text);


        //instantiate popup window
        final PopupWindow popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(validatelinearLayout, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.update();

        //close the popup window on button click

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimReplacementForm replacementForm = new SimReplacementForm();
                replacementForm.userId = userId;
                replacementForm.msisdn = msisdn;
                if(!iccidText.getText().toString().isEmpty()){
                    replacementForm.newIccid = iccidText.getText().toString();
                }else{
                    MyToast.makeMyToast(activity, "Please Enter ICCID.", Toast.LENGTH_SHORT);
                    return;
                }

                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity,"Please wait..!");

                    serviceHandler.postICCIDForSimSwap(replacementForm, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin login = (UserLogin)data.get(0);
                            if(login.status.equals("success")){

                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("ICCID Provided Successfully.");
                                alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                popupWindow.dismiss();
                                                Intent intent = new Intent(activity, NewSimSwapActivity.class);
                                                activity.startActivity(intent);
                                            }
                                        });
                                alertDialog.show();

                            }else if(login.status.equals("INVALID_SESSION")){
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("Status :"+login.status);
                                alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                popupWindow.dismiss();
                                                Intent intent = new Intent(activity, NewSimSwapActivity.class);
                                                activity.startActivity(intent);
                                            }
                                        });
                                alertDialog.show();

                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"STATUS:"+status,"STAFF USER DE-ACTIVATION");

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+e.getMessage()+"STATUS:"+e.getCause(),"STAFF USER DE-ACTIVATION");

                }
            }
        });

    }
}
