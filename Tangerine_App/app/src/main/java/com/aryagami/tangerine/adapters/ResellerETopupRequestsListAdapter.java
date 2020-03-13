package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.UserLogin;
import com.aryagami.data.WalletAccountTransactionLogVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.ResellerETopupRequestsActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

public class ResellerETopupRequestsListAdapter extends ArrayAdapter {
    WalletAccountTransactionLogVo[] walletAccountTransactionLogVos;
    Activity activity = null;
    ProgressDialog progressDialog, progressDialog1;


    public ResellerETopupRequestsListAdapter(Activity context, WalletAccountTransactionLogVo[] warehouseArray) {
        super(context, R.layout.item_etopup_requests, warehouseArray);
        this.walletAccountTransactionLogVos = warehouseArray;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_etopup_requests, null, true);
        }
        final TextView transactionId = (TextView) rowView.findViewById(R.id.transaction_id);
        TextView transactionType = (TextView) rowView.findViewById(R.id.transaction_type);
        TextView walletName = (TextView) rowView.findViewById(R.id.wallet_name);
        TextView requestedOn = (TextView) rowView.findViewById(R.id.requested_on);
        TextView amount = (TextView) rowView.findViewById(R.id.amount);
        TextView status = (TextView) rowView.findViewById(R.id.status);

        Button rejectButton = (Button)rowView.findViewById(R.id.reject_button);
        Button approveButton = (Button)rowView.findViewById(R.id.approve_button);

        final WalletAccountTransactionLogVo rowItem = (WalletAccountTransactionLogVo) getItem(position);

        if (rowItem.transactionId != null) {
            transactionId.setText(rowItem.transactionId.toString());
        } else {
            transactionId.setText("");
        }

        if (!rowItem.transcationType.toString().isEmpty()) {
            transactionType.setText(rowItem.transcationType.toString());
        } else {
            transactionType.setText("");
        }

        if (rowItem.transactionAmount != null) {
            amount.setText(rowItem.transactionAmount.toString());
        } else {
            amount.setText("");
        }

        if (rowItem.walletName != null) {
            walletName.setText(rowItem.walletName.toString());
        } else {
            walletName.setText("");
        }

        if (rowItem.requestedOn != null) {
            requestedOn.setText(rowItem.requestedOn.toString());
        } else {
            requestedOn.setText("");
        }


        if(rowItem.isRequest && rowItem.isCancelled){
            rejectButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.GONE);
            status.setText("Rejected");

        }else if(!rowItem.isRequest && !rowItem.isCancelled){
            rejectButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.GONE);
            status.setText("Approved");

        }else if (rowItem.isRequest && !rowItem.isCancelled){
            rejectButton.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.VISIBLE);
            status.setText("Requested");
        }

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestServiceHandler serviceHandler = new RestServiceHandler();

                if(rowItem.transactionId != null) {
                    try{
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                        serviceHandler.approveETopupRequest(rowItem.transactionId.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                               // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (userLogin.status.equals("success")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                    alertBuilder.setIcon(R.drawable.success_icon);
                                    alertBuilder.setTitle("Success!");
                                    alertBuilder.setMessage("Request Approved successfully.");
                                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            callParentActivity();
                                        }
                                    });
                                    alertBuilder.create().show();

                                } else if (userLogin.status.equals("INVALID_SESSION")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                    alertBuilder.setTitle("Alert");
                                    alertBuilder.setMessage("Status:" + userLogin.status);
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
                                BugReport.postBugReport(activity, Constants.emailId,"Error"+error+", STATUS:"+status,"");
                            }
                        });

                    }catch (IOException io){
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                    }
                }
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            RestServiceHandler serviceHandler = new RestServiceHandler();
                if(rowItem.transactionId != null) {
                    try{
                        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                        serviceHandler.cancelETopupRequest(rowItem.transactionId.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (userLogin.status.equals("success")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);

                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                    alertBuilder.setIcon(R.drawable.success_icon);
                                    alertBuilder.setTitle("Success!");
                                    alertBuilder.setMessage("Request Rejected successfully.");
                                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            callParentActivity();
                                        }
                                    });
                                    alertBuilder.create().show();

                                } else if (userLogin.status.equals("INVALID_SESSION")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                    alertBuilder.setTitle("Alert");
                                    alertBuilder.setMessage("Status:" + userLogin.status);
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
                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"Reject Etopup Request" );
                            }
                        });

                    }catch (IOException io){
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                    }
                }
            }
        });

        return rowView;
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerETopupRequestsActivity.class);
        activity.startActivity(intent);
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}
