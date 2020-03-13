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
import com.aryagami.data.ResellerRequestVo;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.ResellerVoucherRequestsActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

public class ResellerVoucherRequestsListAdapter extends ArrayAdapter {
    ResellerRequestVo[] resellerRequestVos;
    Activity activity = null;
    ProgressDialog progressDialog, progressDialog1;


    public ResellerVoucherRequestsListAdapter(Activity context, ResellerRequestVo[] warehouseArray) {
        super(context, R.layout.item_voucher_requests, warehouseArray);
        this.resellerRequestVos = warehouseArray;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_voucher_requests, null, true);
        }
        TextView name= (TextView) rowView.findViewById(R.id.from_bin_name);
        TextView reqStatus = (TextView) rowView.findViewById(R.id.requested_status);
        TextView reqDate = (TextView) rowView.findViewById(R.id.requested_date);

        TextView type= (TextView) rowView.findViewById(R.id.type);
        TextView quantity = (TextView) rowView.findViewById(R.id.quantity_text);
        TextView amount = (TextView) rowView.findViewById(R.id.amount_text);

        Button rejectButton = (Button)rowView.findViewById(R.id.reject_button);
        Button approveButton = (Button)rowView.findViewById(R.id.approve_button);

        final ResellerRequestVo rowItem = (ResellerRequestVo) getItem(position);

        if (rowItem.resellerName != null) {
            name.setText(rowItem.resellerName.toString());
        } else {
            name.setText("");
        }

        if (!rowItem.status.toString().isEmpty()) {
            reqStatus.setText(rowItem.status.toString());
        } else {
            reqStatus.setText("");
        }

        if (rowItem.creationDate != null) {
            reqDate.setText(rowItem.creationDate.toString());
        } else {
            reqDate.setText("");
        }

        if(rowItem.resellerRequestEntities != null){

            if(rowItem.resellerRequestEntities.size() != 0){
                ResellerRequestVo.ResellerRequestEntityMapperVo entityMapperVo = rowItem.resellerRequestEntities.get(0);
                if (entityMapperVo.quantity != null) {
                    quantity.setText(entityMapperVo.quantity.toString());
                } else {
                    quantity.setText("");
                }
                if (entityMapperVo.price != null) {
                    amount.setText(entityMapperVo.price.toString());
                } else {
                    amount.setText("");
                }
                if (entityMapperVo.entityType != null) {
                    type.setText(entityMapperVo.entityType.toString());
                } else {
                    type.setText("");
                }
            }else{
                type.setText("");
                quantity.setText("");
                amount.setText("");
            }


        }else{
            type.setText("");
            quantity.setText("");
            amount.setText("");
        }

        if(rowItem.status!= null){
            if(rowItem.status.equals("VOUCHER_TRANSFER_REQUEST_SUBMITTED")){
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
            }else{
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            }
        }

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             ResellerRequestVo requestVo = new ResellerRequestVo();
             requestVo.resellerRequestId = rowItem.requestId;
             requestVo.status = "VOUCHER_TRANSFER_REQUEST_APPROVED";

             RestServiceHandler serviceHandler = new RestServiceHandler();
                if(rowItem.requestId != null) {
                    try{
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                        serviceHandler.approveVoucherRequest(requestVo, new RestServiceHandler.Callback() {
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
                                BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"" );
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

                ResellerRequestVo requestVo = new ResellerRequestVo();
                requestVo.resellerRequestId = rowItem.requestId;
                requestVo.status = "VOUCHER_TRANSFER_REQUEST_APPROVAL_FAILED";

                RestServiceHandler serviceHandler = new RestServiceHandler();
                if(rowItem.requestId != null) {
                    try{
                        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                        serviceHandler.approveVoucherRequest(requestVo, new RestServiceHandler.Callback() {
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
                                BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"" );
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
    public  void onTrimMemory(int level) {
        System.gc();
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerVoucherRequestsActivity.class);
        activity.startActivity(intent);
    }
}
