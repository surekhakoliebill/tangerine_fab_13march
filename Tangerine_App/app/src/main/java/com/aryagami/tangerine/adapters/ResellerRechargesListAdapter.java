package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.UserLogin;
import com.aryagami.data.WalletAccountTransactionLogVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.ResellerRechargesListActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.util.List;

public class ResellerRechargesListAdapter extends ArrayAdapter {
    ActivateCommand[] rechargesArray;
    Activity activity;
    ProgressDialog progressDialog;


    public ResellerRechargesListAdapter(Activity context, ActivateCommand[] rechargesArray) {
        super(context, R.layout.item_reseller_recharges, rechargesArray);
        this.rechargesArray = rechargesArray;
        this.activity = context;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_reseller_recharges, null, true);
        }

        TextView msisdnNumber = (TextView) rowView.findViewById(R.id.served_msisdn_text);
        TextView bundleName = (TextView) rowView.findViewById(R.id.bundle_name_text);
        TextView amount = (TextView) rowView.findViewById(R.id.amount_text);
        TextView entityType = (TextView) rowView.findViewById(R.id.entity_type_text);

        Button cancelButton = (Button)rowView.findViewById(R.id.cancel_button);

        final ActivateCommand rowItem = (ActivateCommand) getItem(position);

        if (rowItem.msisdn != null) {
            msisdnNumber.setText(rowItem.msisdn.toString());
        } else {
            msisdnNumber.setText("");
        }

        if (rowItem.planGroupName != null) {
            bundleName.setText(rowItem.planGroupName.toString());
        } else {
            bundleName.setText("N/A");
        }

        if (rowItem.entityType != null) {
            entityType.setText(rowItem.entityType.toString());
        } else {
            entityType.setText("");
        }

        if (rowItem.airtimeAmount != null) {
            amount.setText(rowItem.airtimeAmount.toString());
        } else {
            amount.setText("0");
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                ActivateCommand command = new ActivateCommand();
                command.entityType = rowItem.entityType;
                command.entityId = rowItem.transactionId;
                try{
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                    serviceHandler.cancelRechargeRequest(command, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin response = (UserLogin)data.get(0);
                            if(response.status.equals("success")){
                                ProgressDialogUtil.stopProgressDialog(progressDialog);

                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                alertBuilder.setIcon(R.drawable.success_icon);
                                alertBuilder.setTitle("Success");
                                alertBuilder.setMessage("Recharge Request Cancelled Successfully.");
                                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        callParentActivity();
                                    }
                                });
                                alertBuilder.create().show();

                            }else if(response.status.equals("INVALID_SESSION")){
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                alertBuilder.setIcon(R.drawable.success_icon);
                                alertBuilder.setTitle("Message!");
                                alertBuilder.setMessage("Status: "+command.status);
                                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        callParentActivity();
                                    }
                                });
                                alertBuilder.create().show();
                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS: "+ status, "");
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR"+e.getCause()+"MESSAGE: "+e.getMessage(), "");

                }
            }
        });

        return rowView;
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerRechargesListActivity.class);
        activity.startActivity(intent);
    }
}
